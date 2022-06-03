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
	
	//TODO: instead of real <ip,port> it's possible to use memberId.
	//		separate class that maps memeberId to <ip,port> could be used here
	//		to decouple real <ip,port> from gossip-membership module
	// host names and ports of all known external members (except the current)
	private List<InetSocketAddress> members;
	
	private String host;
	
	private int port;
	
	private ConnectionRegistry cr;
	
	private long timeout;
	
	private String memberId;
	
	public OutboundConnectionManager(List<InetSocketAddress> members, 
									 String host, 
									 int port,
									 ConnectionRegistry cr,
									 long timeout) 
	{
		this.members = members;
		this.host = host;
		this.port = port;
		this.cr = cr;
		this.timeout = timeout;
		this.memberId = host + ":" + port;
	}
	
	@Override
	public void run() {
		for (;;) {			
			maintainOutboundConnections();

			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private void maintainOutboundConnections() {
		for(int i=0; i<members.size(); i++) {
			InetSocketAddress m = members.get(i);
			if(!(m.getHostName().equals(host) && m.getPort() == port) && 
			   !cr.existOutbound(m.getHostName() + ":" + m.getPort())) 
			{
				try {
					logger.info("Connecting to : " + m.getHostName() + ":" + m.getPort());
					
					Socket s = new Socket(m.getHostName(), m.getPort());
					cr.registerOutbound(m.getHostName() + ":" + m.getPort(), s);
					
					logger.info("Outbound connection [" + this.memberId + 
								" connects to " + m.getHostName() + ":" + m.getPort() + "]");
					
					byte[] bCurrentMemberId = memberId.getBytes();
					s.getOutputStream().write(bCurrentMemberId.length);
					s.getOutputStream().write(bCurrentMemberId);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

}
