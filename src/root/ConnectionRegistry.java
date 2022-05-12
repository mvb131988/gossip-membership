package root;

import java.io.IOException;
import java.net.Socket;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionRegistry {

	private static final Logger logger 
	  = LoggerFactory.getLogger(ConnectionRegistry.class);
	
	private Map<String, MemberLink> registry = new HashMap<>();
	
	// queue of free indexes, that where released by released member links
	private Deque<Integer> freeIndexes = new LinkedList<>();
	// max member link index that has ever been created 
	private int maxIndex = 0;
	
	// as a key contains int from 1 to N (number of members)
	// as a value member's host name
	// used for member fast lookup based on it's position
	private Map<Integer, String> registryIndex = new HashMap<>();
	
	private int currentOutboundIndex = 0;
	
	private int currentInboundIndex = 0;
	
	/**
	 * Here two cases are possible:
	 * - there was removed member link and its id could be reused;
	 * - cluster max size ever increases, new index need to be allocated.
	 * 
	 * @return order index
	 */
	//TODO: likely sync is required
	private int allocateIndex() {
		if(freeIndexes.size() > 0) {
			return freeIndexes.pollFirst();
		}
		maxIndex++;
		return maxIndex;
	}
	
	/**
	 * When member link is removed its order index could be reused, so
	 * it's returning to the free index queue. 
	 * 
	 * @param index
	 */
	private void releaseIndex(int index) {
		freeIndexes.addLast(index);
	}
	
	/**
	 * Register inbound connection
	 * 
	 * @param key - hostname of the member who connects to the current
	 * @param in - socket for the given key
	 */
	public void registerInbound(String key, Socket in) {
		MemberLink ml = registry.get(key);
		if(ml == null) {
			int index = allocateIndex();
			ml = new MemberLink(index);
			registry.put(key, ml);
			registryIndex.put(index, key);
		}
		ml.setInboundConnection(in);
	}
	
	/**
	 * Register outbound connection.
	 * 
	 * @param key - hostname of the member where the current connects
	 * @param out - socket for the given key
	 */
	public void registerOutbound(String key, Socket out) {
		MemberLink ml = registry.get(key);
		if(ml == null) {
			int index = allocateIndex();
			ml = new MemberLink(index);
			registry.put(key, ml);
			registryIndex.put(index, key);
		}
		ml.setOutboundConnection(out);
	}
	
	/**
	 * Removes member link, closes both inbound, outbound connections and
	 * releases order index.
	 * 
	 * @param key - member link name (host:port name)
	 */
	public void removeConnection(String key) {
		MemberLink ml = registry.get(key);
		if(ml != null) {
			try {
				if(ml.getInboundConnection() != null) {
					ml.getInboundConnection().close();
				}
			} catch(IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
			
			try {
				if(ml.getOutboundConnection() != null) {
					ml.getOutboundConnection().close();
				}
			} catch(IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
			
			registry.remove(key);
			registryIndex.remove(ml.getOrderIndex());
			releaseIndex(ml.getOrderIndex());
		} 
	}
	
	public boolean existOutbound(String key) {
		return registry.get(key) != null && registry.get(key).getOutboundConnection() != null;
	}
	
	/**
	 * Connection allocation algorithm round robin like approach. Go from left to right until
	 * active connection is not found. If end of queue is reached move back to the beginning
	 * of the queue. If no active are in the queue return null.
	 * 
	 * Example1(A-active,I-inactive)
	 * 
	 * index      1  2  3  4
	 * host name  m1 m2 m3 m4
	 * status     A  I  A  I
	 * 
	 * 1st call:
	 * Suppose currentInboundIndex=0 (that translates in 1 in registryIndexMap). First connection
	 * is active return it, increase currentInboundIndex.
	 * 
	 * 2nd call:
	 * currentInboundIndex = 1 (that translates in 2 in registryIndexMap). Connection is inactive
	 * try next one. Next one (3 in registryIndexMap) is active return it and change 
	 * currentInboundIndex to the current position + 1(4 in registryIndexMap translates in 
	 * currentInboundIndex = 3).
	 * 
	 * 3rd call:
	 * currentInboundIndex = 3 (that translates in 4 in registryIndexMap). Since connection is 
	 * inactive and end of queue is reached come back to the beginning of the queue.
	 * It's active, return it and change currentInboundIndex = 0+1
	 * 
	 * @return next inbound connection or null if there are no initialized member links. 
	 */
	public Member nextInbound() {
		int index = currentInboundIndex + 1;
		int tmpIndex = index;
		Member m = null;
		
		for(int i=index; i<=registryIndex.size(); i++) {
			String hostPort = registryIndex.get(i);
			if(isInitialized(hostPort)) {
				index = i;
				
				MemberLink ml = registry.get(hostPort);
				m = new Member(hostPort, ml.getInboundConnection());
				break;
			}
		}
		
		if(m == null) {
			for(int i=1; i<tmpIndex; i++) {
				String hostPort = registryIndex.get(i);
				if(isInitialized(hostPort)) {
					index = i;
					
					MemberLink ml = registry.get(hostPort);
					m = new Member(hostPort, ml.getInboundConnection());
					break;
				}
			}
		}
		
		currentInboundIndex = index+1 <= registryIndex.size() ? index+1 : 1;
		currentInboundIndex--;
		return m;
	}
	
	/**
	 * Check nextInbound as the same approach used here.
	 * 
	 * @return @return next outbound connection or null if there are no initialized member links.
	 */
	public Member nextOutbound() {
		int index = currentOutboundIndex + 1;
		int tmpIndex = index;
		Member m = null;
		
		for(int i=index; i<=registryIndex.size(); i++) {
			String hostPort = registryIndex.get(i);
			if(isInitialized(hostPort)) {
				index = i;
				
				MemberLink ml = registry.get(hostPort);
				m = new Member(hostPort, ml.getOutboundConnection());
				break;
			}
		}
		
		if(m == null) {
			for(int i=1; i<tmpIndex; i++) {
				String hostPort = registryIndex.get(i);
				if(isInitialized(hostPort)) {
					index = i;
					
					MemberLink ml = registry.get(hostPort);
					m = new Member(hostPort, ml.getOutboundConnection());
					break;
				}
			}
		}
		
		currentOutboundIndex = index+1 <= registryIndex.size() ? index+1 : 1;
		currentOutboundIndex--;
		return m;
	}
	
	private boolean isInitialized(String key) {
		MemberLink ml = registry.get(key);
		return ml.getInboundConnection() != null && ml.getOutboundConnection() != null;
	}
	
}
