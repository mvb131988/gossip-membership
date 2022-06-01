package root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeoutConnectionManager implements Runnable {

	private static final Logger logger 
	  = LoggerFactory.getLogger(TimeoutConnectionManager.class);
	
	private MemberStateMonitor monitor;
	
	public TimeoutConnectionManager(MemberStateMonitor monitor) {
		this.monitor = monitor;
	}
	
	@Override
	public void run() {
		for(;;) {
			try {
				Thread.sleep(60_000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			
			monitor.inactivateMember(System.currentTimeMillis(), 60_000);
			monitor.removeMember(System.currentTimeMillis(), 60_000);
		}
	}

}
