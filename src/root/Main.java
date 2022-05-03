package root;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger logger 
	  = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		List<InetSocketAddress> nodes = AppProperties.getNodes();
		
		String host = "localhost";
		if (System.getenv("HOST") != null) {
			host = System.getenv("HOST");
		}

		int port = 0;
		if (System.getenv("PORT") != null) {
			port = Integer.parseInt(System.getenv("PORT"));
		} else {
			port = Integer.parseInt(args[0]);
		}
		
		logger.info("Starting member at " + host + ":" + port);
		
		ServerSocket ss = null;
		try {
			ss = new ServerSocket();
			final InetSocketAddress isa = new InetSocketAddress("0.0.0.0", port);
			ss.bind(isa);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		logger.info("Starting inbound connection manager thread");
		
		ConnectionRegistry cr = new ConnectionRegistry();
		
		InboundConnectionManager icm = new InboundConnectionManager(ss, host, port, cr);
		Thread icmt = new Thread(icm);
		icmt.setName("InboundConnectionManagerThread");
		icmt.start();
		
		logger.info("Starting outbound connection manager thread");
		
		OutboundConnectionManager ocm = new OutboundConnectionManager(nodes, host, port, cr);
		Thread ocmt = new Thread(ocm);
		ocmt.setName("OutboundConnectionManagerThread");
		ocmt.start();
		
		MemberStateManager msm = new MemberStateManager(host + ":" + port);
		
		logger.info("Starting gossip sender thread");
		
		GossipSender gs = new GossipSender(host, port, cr, msm);
		Thread gst = new Thread(gs);
		gst.setName("GossipSenderThread");
		gst.start();
		
		logger.info("Starting gossip receiver thread");
		
		GossipReceiver gr = new GossipReceiver(host, port, cr, msm);
		Thread grt = new Thread(gr);
		grt.setName("GossipRecevierThread");
		grt.start();
		
		logger.info("Starting timeout connection thread");
		
		TimeoutConnectionManager tcm = new TimeoutConnectionManager(msm);
		Thread tcmt = new Thread(tcm);
		tcmt.setName("TimeoutConnectionManager");
		tcmt.start();
	}
	
}
