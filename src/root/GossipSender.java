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
	
	private MemberStateMonitor monitor;
	
	private long timeout;
	
	private String memberId;
	
	public GossipSender(String host, 
						int port, 
						ConnectionRegistry cr, 
						MemberStateMonitor monitor,
						long timeout) 
	{
		this.cr = cr;
		this.monitor = monitor;
		this.timeout = timeout;
		this.memberId = host + ":" + port;
	}
	
	@Override
	public void run() {
		for(;;) {
			long timestamp = System.currentTimeMillis();
			sendVectorClock(timestamp);
			
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private void sendVectorClock(Long timestamp) {
		Member m = cr.nextOutbound();
		if (m != null) {
			try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream out = new ObjectOutputStream(bos)) 
			{
				VectorClockTable vct = 
						monitor.updateMemberStateAndGetVectorClockTable(timestamp);
				GossipMessage gm = new GossipMessage(vct);
				
				out.writeObject(gm);
				byte[] output = bos.toByteArray();
				
				logger.info("Member " + memberId + " sends " + output.length + 
						" bytes to " + m.getMemberId());
				
				OutputStream os = m.getSocket().getOutputStream();
				os.write(output.length & 0xff);
				os.write((output.length & 0xff00) >> 8);
				os.write((output.length & 0xff0000) >> 16);
				os.write((output.length & 0xff000000) >> 24);
				
				os.write(output);
				
				logger.info("Member " + memberId + " sends vector clock {} to " + 
							m.getMemberId(), gm);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				cr.removeConnection(m.getMemberId());
			}
		}
	}
	
}
