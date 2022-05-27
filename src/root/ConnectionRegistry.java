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
	// when MemberLink is created its hostPort value saved here. Then it's retrieved
	// in a round robin manner
	private Deque<String> inboundQueue  = new LinkedList<>();
	
	// value here is MemberLink name
	// when MemberLink is created its hostPort value saved here. Then it's retrieved
	// in a round robin manner
	private Deque<String> outboundQueue  = new LinkedList<>();
	
	/**
	 * Register inbound connection
	 * 
	 * @param key - hostname of the member who connects to the current
	 * @param in - socket for the given key
	 */
	public synchronized void registerInbound(String key, Socket in) {
		MemberLink ml = registry.get(key);
		if(ml == null) {
			ml = new MemberLink();
			registry.put(key, ml);
		}
		inboundQueue.add(key);
		ml.setInboundConnection(in);
	}
	
	/**
	 * Register outbound connection.
	 * 
	 * @param key - hostname of the member where the current connects
	 * @param out - socket for the given key
	 */
	public synchronized void registerOutbound(String key, Socket out) {
		MemberLink ml = registry.get(key);
		if(ml == null) {
			ml = new MemberLink();
			registry.put(key, ml);
		}
		outboundQueue.add(key);
		ml.setOutboundConnection(out);
	}
	
	/**
	 * Removes member link, closes both inbound, outbound connections.
	 * 
	 * @param key - member link name (host:port name)
	 */
	public synchronized void removeConnection(String key) {
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
		} 
	}
	
	/**
	 * Checks if for given member exists outbound connection 
	 * 
	 * @param key - member id
	 * @return true if outbound connection exists, false otherwise
	 */
	public synchronized boolean existOutbound(String key) {
		return registry.get(key) != null && registry.get(key).getOutboundConnection() != null;
	}
	
	/**
	 * Connection allocation algorithm round robin like approach. The idea is to extract head 
	 * of the queue, return it and insert head to the tail of the same queue.
	 * 
	 * There are two concerns:
	 * - when MemberLink is removed queue is not cleared immediately. During queue traversal
	 *   if for the selected hostPort value there is no MemberLink in the registry (this is 
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
			String mlHostPort = inboundQueue.pollFirst();
			if (registry.get(mlHostPort) != null) {
				lookupCounter++;
				
				inboundQueue.addLast(mlHostPort);

				if (isInitialized(mlHostPort)) {
					MemberLink ml = registry.get(mlHostPort);
					m = new Member(mlHostPort, ml.getInboundConnection());
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
			String mlHostPort = outboundQueue.pollFirst();
			if(registry.get(mlHostPort) != null) {
				lookupCounter++;
				
				outboundQueue.addLast(mlHostPort);
				
				if(isInitialized(mlHostPort)) {
					MemberLink ml = registry.get(mlHostPort);
					m = new Member(mlHostPort, ml.getOutboundConnection());
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
