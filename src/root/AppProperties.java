package root;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppProperties {
	
	private static final Logger logger 
	  = LoggerFactory.getLogger(AppProperties.class);
	
	public static List<InetSocketAddress> getNodes() {
		logger.info("NODES system variable = " + System.getenv("NODES"));
		
		String[] nodes = ResourceBundle.getBundle("app").getString("nodes").split(";"); 
		if (System.getenv("NODES") != null && !System.getenv("NODES").equals("")) {
			nodes = System.getenv("NODES").split(";");
		}
		
		List<InetSocketAddress> l = 
				Stream.of(nodes)
					  .map(n -> new InetSocketAddress(n.split(":")[0], 
							  						  Integer.parseInt(n.split(":")[1])
							  						 )
						  )
					  .collect(Collectors.toList());
		return l;
	}

	public static Long connectionInboundFrequency() {
		return Long.parseLong(
				ResourceBundle.getBundle("app").getString("connection.inbound.frequency"));
	}
	
	public static Long connectionOutboundFrequency() {
		return Long.parseLong(
				ResourceBundle.getBundle("app").getString("connection.outbound.frequency"));
	}
	
	public static Long gossipSendFrequency() {
		return Long.parseLong(
				ResourceBundle.getBundle("app").getString("gossip.send.frequency"));
	}

	public static Long gossipReceiveFrequency() {
		return Long.parseLong(
				ResourceBundle.getBundle("app").getString("gossip.receive.frequency"));
	}
	
	public static Long memberstatetableChangeFrequency() {
		return Long.parseLong(
				ResourceBundle.getBundle("app").getString("memberstatetable.change.frequency"));
	}
	
	public static Long memberstatetablePollFrequency() {
		return Long.parseLong(
				ResourceBundle.getBundle("app").getString("memberstatetable.poll.frequency"));
	}
	
}
