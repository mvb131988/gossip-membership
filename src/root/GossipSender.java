package root;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GossipSender implements Runnable {

	private static final Logger logger 
	  = LoggerFactory.getLogger(GossipSender.class);
	
	private ConnectionRegistry cr;
	
	private String host;
	
	private int port;
	
	private MemberStateMonitor monitor;
	
	public GossipSender(String host, 
						int port, 
						ConnectionRegistry cr, 
						MemberStateMonitor monitor) 
	{
		this.cr = cr;
		this.host = host;
		this.port = port;
		this.monitor = monitor;
	}
	
	@Override
	public void run() {
		for(;;) {
			Member m = cr.nextOutbound();
			if (m != null) {
				try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
						ObjectOutputStream out = new ObjectOutputStream(bos)) 
				{
					long timestamp = System.currentTimeMillis();
					VectorClockTable vct = monitor.updateMemberStateAndGetVectorClockTable(timestamp);
					GossipMessage gm = new GossipMessage(vct);
					
					out.writeObject(gm);
					byte[] output = bos.toByteArray();
					
					logger.info("Member " + host + ":" + port + " sends " + output.length + 
							" bytes to " + m.getHostPort());
					
					OutputStream os = m.getSocket().getOutputStream();
					os.write(output.length & 0xff);
					os.write((output.length & 0xff00) >> 8);
					os.write((output.length & 0xff0000) >> 16);
					os.write((output.length & 0xff000000) >> 24);
					
					os.write(output);
					
					logger.info("Member " + host + ":" + port + " sends vector clock {} to " + 
								m.getHostPort(), gm);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					cr.removeConnection(m.getHostPort());
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
}
