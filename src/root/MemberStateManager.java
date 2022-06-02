package root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for member state table modification (similarly to 
 * MemberStateMonitor), however it manages only specific two transitions:
 * (1) ACTIVE -> INACTIVE
 * (2) ACTIVE -> removed from member state table 
 */
public class MemberStateManager implements Runnable {

	private static final Logger logger 
	  = LoggerFactory.getLogger(MemberStateManager.class);
	
	private MemberStateMonitor monitor;
	
	private long timeout;
	
	public MemberStateManager(MemberStateMonitor monitor, long timeout) {
		this.monitor = monitor;
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		for(;;) {
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			
			monitor.inactivateMember(System.currentTimeMillis(), timeout);
			monitor.removeMember(System.currentTimeMillis(), timeout);
		}
	}

}
