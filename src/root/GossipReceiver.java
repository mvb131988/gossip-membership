package root;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GossipReceiver implements Runnable {

	private static final Logger logger 
	  = LoggerFactory.getLogger(GossipReceiver.class);

	private ConnectionRegistry cr;
	
	private String host;
	
	private int port;
	
	public GossipReceiver(String host, int port, ConnectionRegistry cr) {
		this.cr = cr;
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void run() {
		for(;;) {
			Member m = cr.nextInbound();
			if (m != null) {
				byte[] input = null;
				try {
					int b1 = m.getSocket().getInputStream().read();
					int b2 = m.getSocket().getInputStream().read();
					int b3 = m.getSocket().getInputStream().read();
					int b4 = m.getSocket().getInputStream().read();
	
					int length = b1 ^ b2 ^ b3 ^ b4;
					input = new byte[length];
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
	
				Map<String, Integer> vectorCLock = null;
				try (ByteArrayInputStream bis = new ByteArrayInputStream(input);
						ObjectInputStream in = new ObjectInputStream(bis)) {
					vectorCLock = (Map<String, Integer>) in.readObject();
				} catch (ClassNotFoundException | IOException e) {
					logger.error(e.getMessage(), e);
				}
				
				logger.info("Member " + host + ":" + port + " receives vector clock from" + 
							m.getHostPort());
			}
			
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
}
