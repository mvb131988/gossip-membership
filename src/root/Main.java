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
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
		logger.info("Starting inbound connection manager thread");
		
		ConnectionRegistry cr = new ConnectionRegistry();
		
		long t1 = AppProperties.connectionInboundFrequency();
		InboundConnectionManager icm = new InboundConnectionManager(ss, host, port, cr, t1);
		Thread icmt = new Thread(icm);
		icmt.setName("InboundConnectionManagerThread");
		icmt.start();
		
		logger.info("Starting outbound connection manager thread");
		
		long t2 = AppProperties.connectionOutboundFrequency();
		OutboundConnectionManager ocm = new OutboundConnectionManager(nodes, host, port, cr, t2);
		Thread ocmt = new Thread(ocm);
		ocmt.setName("OutboundConnectionManagerThread");
		ocmt.start();
		
		MemberStateMonitor msm = new MemberStateMonitor(host + ":" + port);
		
		logger.info("Starting gossip sender thread");
		
		long t3 = AppProperties.gossipSendFrequency();
		GossipSender gs = new GossipSender(host, port, cr, msm, t3);
		Thread gst = new Thread(gs);
		gst.setName("GossipSenderThread");
		gst.start();
		
		logger.info("Starting gossip receiver thread");
		
		long t4 = AppProperties.gossipReceiveFrequency();
		GossipReceiver gr = new GossipReceiver(host, port, cr, msm, t4);
		Thread grt = new Thread(gr);
		grt.setName("GossipRecevierThread");
		grt.start();
		
		logger.info("Starting timeout connection thread");
		
		long t5 = AppProperties.memberstatetableChangeFrequency();
		MemberStateManager msma = new MemberStateManager(msm, t5);
		Thread tcmt = new Thread(msma);
		tcmt.setName("TimeoutConnectionManager");
		tcmt.start();
		
		logger.info("Member state observer thread");
		
		long t6 = AppProperties.memberstatetablePollFrequency();
		MemberStateObserver mso = new MemberStateObserver(host, port, msm, t6);
		Thread msot = new Thread(mso);
		msot.setName("MemberStateObserver");
		msot.start();
	}
	
}
