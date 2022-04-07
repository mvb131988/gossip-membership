package root;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ConnectionRegistry {

	private Map<String, MemberLink> registry = new HashMap<>();
	
	/**
	 * Register inbound connection
	 * key - hostname of the member who connects to the current
	 * in - socket for the given key
	 */
	public void registerInbound(String key, Socket in) {
		MemberLink ml = registry.getOrDefault(key, new MemberLink());
		ml.setInboundConnection(in);
	}
	
	/**
	 * Register outbound connection
	 * key - hostname of the member where the current connects
	 * out - socket for the given key
	 */
	public void registerOutbound(String key, Socket out) {
		MemberLink ml = registry.getOrDefault(key, new MemberLink());
		ml.setInboundConnection(out);
	}
	
	public boolean isInitialized(String key) {
		MemberLink ml = registry.get(key);
		if (ml == null) {
			return false;
		}
		return ml.getInboundConnection() != null && ml.getOutboundConnection() != null;
	}
	
}
