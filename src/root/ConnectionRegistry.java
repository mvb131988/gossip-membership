package root;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionRegistry {

	private static final Logger logger 
	  = LoggerFactory.getLogger(ConnectionRegistry.class);
	
	private Map<String, MemberLink> registry = new HashMap<>();
	
	//TODO: when member is removed the key it's associated with is 
	//released and hence could be reused. Reordering of the map would take O(N).
	//It could be done better using a separate set of free indexes.
	//On lookup when there is a gap in indexes, this is necessary to find a way
	//to return closest available index
	
	// as a key contains int from 1 to N (number of members)
	// as a value member's host name
	// used for member fast lookup based on it's position
	private Map<Integer, String> registryIndex = new HashMap<>();
	
	private int currentOutboundIndex = 0;
	
	private int currentInboundIndex = 0;
	
	/**
	 * Register inbound connection
	 * key - hostname of the member who connects to the current
	 * in - socket for the given key
	 */
	public void registerInbound(String key, Socket in) {
		MemberLink ml = registry.get(key);
		if(ml == null) {
			int size = registry.size();
			ml = new MemberLink();
			registry.put(key, ml);
			registryIndex.put(size + 1, key);
		}
		ml.setInboundConnection(in);
	}
	
	/**
	 * Register outbound connection
	 * key - hostname of the member where the current connects
	 * out - socket for the given key
	 */
	public void registerOutbound(String key, Socket out) {
		MemberLink ml = registry.get(key);
		if(ml == null) {
			int size = registry.size();
			ml = new MemberLink();
			registry.put(key, ml);
			registryIndex.put(size + 1, key);
		}
		ml.setOutboundConnection(out);
	}
	
	public boolean existOutbound(String key) {
		return registry.get(key) != null && registry.get(key).getOutboundConnection() != null;
	}
	
	public void removeConnection(String key) {
		MemberLink ml = registry.get(key);
		if(ml != null) {
			try {
				ml.getInboundConnection().close();
				ml.getOutboundConnection().close();
			} catch(IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
			registry.remove(key);
		} 
	}
	
	public Member nextOutbound() {
		while(currentOutboundIndex < registryIndex.size()) {
			
			currentOutboundIndex++;
			
			String hostPort = registryIndex.get(currentOutboundIndex);
			if(hostPort == null || !isInitialized(hostPort)) {
				continue;
			}
			
			MemberLink ml = registry.get(hostPort);
			Member m = new Member(hostPort, ml.getOutboundConnection());
			return m;
		}
		currentOutboundIndex = 0;
		return null;
	}
	
	public Member nextInbound() {
		while(currentInboundIndex < registryIndex.size()) {

			currentInboundIndex++;
			
			String hostPort = registryIndex.get(currentInboundIndex);
			if(hostPort == null || !isInitialized(hostPort)) {
				continue;
			}
			
			MemberLink ml = registry.get(hostPort);
			Member m = new Member(hostPort, ml.getInboundConnection());
			return m;
		}
		currentInboundIndex = 0;
		return null;
	}
	
	private boolean isInitialized(String key) {
		MemberLink ml = registry.get(key);
		if (ml == null) {
			return false;
		}
		return ml.getInboundConnection() != null && ml.getOutboundConnection() != null;
	}
	
}
