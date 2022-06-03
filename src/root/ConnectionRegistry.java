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
	
	// value here is MemberLink name
	// when MemberLink is created its memberId value saved here. Then it's retrieved
	// in a round robin manner
	private Deque<String> inboundQueue  = new LinkedList<>();
	
	// value here is MemberLink name
	// when MemberLink is created its memberId value saved here. Then it's retrieved
	// in a round robin manner
	private Deque<String> outboundQueue  = new LinkedList<>();
	
	/**
	 * Register inbound connection
	 * 
	 * @param memberId - memberId of the member who connects to the current one
	 * @param in - socket for the given key
	 */
	public synchronized void registerInbound(String memberId, Socket in) {
		MemberLink ml = registry.get(memberId);
		if(ml == null) {
			ml = new MemberLink();
			registry.put(memberId, ml);
		}
		inboundQueue.add(memberId);
		ml.setInboundConnection(in);
	}
	
	/**
	 * Register outbound connection.
	 * 
	 * @param memberId - memberId of the member where the current one connects
	 * @param out - socket for the given key
	 */
	public synchronized void registerOutbound(String memberId, Socket out) {
		MemberLink ml = registry.get(memberId);
		if(ml == null) {
			ml = new MemberLink();
			registry.put(memberId, ml);
		}
		outboundQueue.add(memberId);
		ml.setOutboundConnection(out);
	}
	
	/**
	 * Removes member link, closes both inbound, outbound connections.
	 * 
	 * @param memberId - memberId of the removing member link
	 */
	public synchronized void removeConnection(String memberId) {
		MemberLink ml = registry.get(memberId);
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
			
			registry.remove(memberId);
		} 
	}
	
	/**
	 * Checks if for given member exists outbound connection 
	 * 
	 * @param memberId - member id if the verifying member link 
	 * @return true if outbound connection exists, false otherwise
	 */
	public synchronized boolean existOutbound(String memberId) {
		MemberLink ml = registry.get(memberId);
		return ml != null && ml.getOutboundConnection() != null;
	}
	
	/**
	 * Connection allocation algorithm round robin like approach. The idea is to extract head 
	 * of the queue, return it and insert head to the tail of the same queue.
	 * 
	 * There are two concerns:
	 * - when MemberLink is removed queue is not cleared immediately. During queue traversal
	 *   if for the selected memberId value there is no MemberLink in the registry (this is 
	 *   the indicator that MemberLink has been removed) then it's removed from the queue
	 *   and the next element is chosen and verified.
	 * - when there is non empty registry, but all MemberLinks are not initialized 
	 *   lookupCounter is used to prevent infinite loop. The idea is to break the loop when
	 *   all MemberLinks were visited once. 
	 * 
	 * @return next inbound connection or null if there are no initialized member links. 
	 */
	public synchronized Member nextInbound() {
		Member m = null;
		int lookupCounter = 0;
		while (lookupCounter < inboundQueue.size() && inboundQueue.size() > 0) {
			String mlMemberId = inboundQueue.pollFirst();
			if (registry.get(mlMemberId) != null) {
				lookupCounter++;
				
				inboundQueue.addLast(mlMemberId);

				if (isInitialized(mlMemberId)) {
					MemberLink ml = registry.get(mlMemberId);
					m = new Member(mlMemberId, ml.getInboundConnection());
					break;
				}
			}
		}
		return m;
	}
	
	/**
	 * Check nextInbound as the same approach used here.
	 * 
	 * @return @return next outbound connection or null if there are no initialized member links.
	 */
	public synchronized Member nextOutbound() {
		Member m = null;
		int lookupCounter = 0;
		while(lookupCounter < outboundQueue.size() && outboundQueue.size() > 0) {
			String mlMemberId = outboundQueue.pollFirst();
			if(registry.get(mlMemberId) != null) {
				lookupCounter++;
				
				outboundQueue.addLast(mlMemberId);
				
				if(isInitialized(mlMemberId)) {
					MemberLink ml = registry.get(mlMemberId);
					m = new Member(mlMemberId, ml.getOutboundConnection());
					break;
				}
			}
		}
		return m;
	}
	
	private boolean isInitialized(String key) {
		MemberLink ml = registry.get(key);
		return ml.getInboundConnection() != null && ml.getOutboundConnection() != null;
	}
	
}
