package root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberStateObserver implements Runnable {

	private static final Logger logger 
	  = LoggerFactory.getLogger(MemberStateObserver.class);
	
	private String host;
	
	private int port;

	private MemberStateMonitor monitor;
	
	public MemberStateObserver(String host, int port, MemberStateMonitor monitor) {
		super();
		this.host = host;
		this.port = port;
		this.monitor = monitor;
	}

	@Override
	public void run() {
		for(;;) {
			try {
				Thread.sleep(30_000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			MemberStateTable table = monitor.copyMemberStateTable();
			logger.info("Members state from member " + host + ":" + port + " " + table.toString());
			
			logger.info("Members state from member " + host + ":" + port + " seen by members: " + table.toStringSeenBy());
		}
	}

}
