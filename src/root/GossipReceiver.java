package root;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GossipReceiver implements Runnable {

	private static final Logger logger 
	  = LoggerFactory.getLogger(GossipReceiver.class);

	private ConnectionRegistry cr;
	
	private String host;
	
	private int port;
	
	private MemberStateManager manager;
	
	public GossipReceiver(String host, int port, 
						  ConnectionRegistry cr, 
						  MemberStateManager manager) 
	{
		this.cr = cr;
		this.host = host;
		this.port = port;
		this.manager = manager;
	}
	
	@Override
	public void run() {
		for(;;) {
			Member m = cr.nextInbound();
			if (m != null) {

				logger.info("Member " + host + ":" + port + " checks for incoming messages from " + 
						m.getHostPort());
				
				byte[] input = null;
				try {
					int b1 = m.getSocket().getInputStream().read();
					int b2 = m.getSocket().getInputStream().read();
					int b3 = m.getSocket().getInputStream().read();
					int b4 = m.getSocket().getInputStream().read();
	
					int length = b1 ^ (b2 << 8) ^ (b3 << 16) ^ (b4 << 24);
					input = new byte[length];
					m.getSocket().getInputStream().read(input);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
				
				logger.info("Member " + host + ":" + port + " receives from " + 
						m.getHostPort() + " " + input.length + " bytes");
				
				try (ByteArrayInputStream bis = new ByteArrayInputStream(input);
						ObjectInputStream in = new ObjectInputStream(bis)) {
					GossipMessage gm = null;
					gm = (GossipMessage) in.readObject();
				
					logger.info("Member " + host + ":" + port + " receives vector clock {} from " + 
							m.getHostPort(), gm);
					
					manager.updateMembersState(gm.getVectorClock());
					
				} catch (ClassNotFoundException | IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
}
