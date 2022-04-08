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
	
	// host names and ports of all known external members (except the current)
	private List<InetSocketAddress> members;
	
	private String host;
	
	private int port;
	
	private ConnectionRegistry cr;
	
	public OutboundConnectionManager(List<InetSocketAddress> members, 
									 String host, 
									 int port,
									 ConnectionRegistry cr) 
	{
		this.members = members;
		this.host = host;
		this.port = port;
		this.cr = cr;
	}
	
	@Override
	public void run() {
		for(int i=0; i<members.size(); i++) {
			InetSocketAddress m = members.get(i);
			if(!(m.getHostName().equals(host) && m.getPort() == port)) {
				try {
					logger.info("Connecting to : " + m.getHostName() + ":" + m.getPort());
					
					Socket s = new Socket(m.getHostName(), m.getPort());
					cr.registerOutbound(m.getHostName() + ":" + m.getPort(), s);
					
					logger.info("Outbound connection [" + host + ":" + port + 
								" connects to " + m.getHostName() + ":" + m.getPort() + "]");
					
					String currentMember = host + ":" + port;
					byte[] bCurrentMemberNode = currentMember.getBytes();
					s.getOutputStream().write(bCurrentMemberNode.length & 0xff);
					s.getOutputStream().write(bCurrentMemberNode.length & 0xff00);
					s.getOutputStream().write(bCurrentMemberNode.length & 0xff0000);
					s.getOutputStream().write(bCurrentMemberNode.length & 0xff000000);
					s.getOutputStream().write(bCurrentMemberNode);
				} catch (IOException e) {
					i--;
					logger.error(e.getMessage(), e);
				}
				
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

}
