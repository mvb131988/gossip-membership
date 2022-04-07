package root;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutboundConnectionManager implements Runnable {

	private static final Logger logger 
	  = LoggerFactory.getLogger(OutboundConnectionManager.class);
	
	private List<InetSocketAddress> nodes;
	
	private String host;
	
	private int port;
	
	public OutboundConnectionManager(List<InetSocketAddress> nodes, String host, int port) {
		this.nodes = nodes;
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void run() {
		for(int i=0; i<nodes.size(); i++) {
			InetSocketAddress node = nodes.get(i);
			if(!(node.getHostName().equals(host) && node.getPort() == port)) {
				try {
					logger.info("Connecting to : " + node.getHostName() + ":" + node.getPort());
					
					Socket s = new Socket(node.getHostName(), node.getPort());
					logger.info("Outbound connection [" + host + ":" + port + 
								" connects to " + node.getHostName() + ":" + node.getPort() + "]");
					
					String localNode = host + ":" + port;
					byte[] bLocalNode = localNode.getBytes();
					s.getOutputStream().write(bLocalNode.length&0xff);
					s.getOutputStream().write(bLocalNode.length&0xff00);
					s.getOutputStream().write(bLocalNode.length&0xff0000);
					s.getOutputStream().write(bLocalNode.length&0xff000000);
					s.getOutputStream().write(bLocalNode);
				} catch (IOException e) {
					i--;
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				}
				
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
