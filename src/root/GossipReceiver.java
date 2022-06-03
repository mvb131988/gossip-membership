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
	
	private MemberStateMonitor monitor;
	
	private long timeout;
	
	private String memberId;
	
	public GossipReceiver(String host, int port, 
						  ConnectionRegistry cr, 
						  MemberStateMonitor monitor,
						  long timeout) 
	{
		this.cr = cr;
		this.memberId = host + ":" + port;
		this.monitor = monitor;
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		for(;;) {
			
			long timestamp = System.currentTimeMillis();
			receive(timestamp);
			
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private void receive(Long timestamp) {
		Member m = cr.nextInbound();
		if (m != null) {

			logger.info("Member " + memberId + " checks for incoming messages "
					+ "from " + m.getMemberId());
			
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
				cr.removeConnection(m.getMemberId());
			}
			
			// when failure happens input could be left uninitialized
			// input == null
			// when invoked in logger this leads to NullPointer and to GossipReceiver 
			// thread death
			if(input != null) {
				logger.info("Member " + memberId + " receives from " + 
						m.getMemberId() + " " + input.length + " bytes");
				
				try (ByteArrayInputStream bis = new ByteArrayInputStream(input);
						ObjectInputStream in = new ObjectInputStream(bis)) {
					GossipMessage gm = null;
					gm = (GossipMessage) in.readObject();
				
					logger.info("Member " + memberId + " receives vector clock "
							+ "{} from " + m.getMemberId(), gm);
					
					monitor.updateMembersState(gm.getVectorClock(), 
											   m.getMemberId(), 
											   timestamp);
					
				} catch (ClassNotFoundException | IOException e) {
					logger.error(e.getMessage(), e);
					cr.removeConnection(m.getMemberId());
				}
			}
		}
	}
	
}
