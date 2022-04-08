package root;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GossipSender implements Runnable {

	private static final Logger logger 
	  = LoggerFactory.getLogger(GossipSender.class);
	
	private ConnectionRegistry cr;
	
	private String host;
	
	private int port;
	
	public GossipSender(String host, int port, ConnectionRegistry cr) {
		this.cr = cr;
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void run() {
		for(;;) {
			Member m = cr.nextOutbound();
			if (m != null) {
				try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
						ObjectOutputStream out = new ObjectOutputStream(bos)) 
				{
					Map<String, Integer> vectorClock = new HashMap<>();
					vectorClock.put(host + port, 1);
					out.writeObject(vectorClock);
					byte[] output = bos.toByteArray();
					
					OutputStream os = m.getSocket().getOutputStream();
					os.write(output.length & 0xff);
					os.write(output.length & 0xff00);
					os.write(output.length & 0xff0000);
					os.write(output.length & 0xff000000);
					
					os.write(output);
					
					logger.info("Member " + host + ":" + port + " sends vector clock to" + 
								m.getHostPort());
				} catch (IOException e) {
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
