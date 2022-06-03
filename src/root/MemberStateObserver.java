package root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visualizes cluster state how it is seen from the current node side.
 */
public class MemberStateObserver implements Runnable {

	private static final Logger logger 
	  = LoggerFactory.getLogger(MemberStateObserver.class);
	
	private String host;
	
	private int port;

	private MemberStateMonitor monitor;
	
	private long timeout;
	
	private long clusterstateConvergencePeriod;
	
	public MemberStateObserver(String host, int port, MemberStateMonitor monitor, long timeout, long cscp) {
		super();
		this.host = host;
		this.port = port;
		this.monitor = monitor;
		this.timeout = timeout;
		this.clusterstateConvergencePeriod = cscp;
	}

	@Override
	public void run() {
		for(;;) {
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			
			MemberStateTable table = monitor.copyMemberStateTable();
			
			logger.info("Members state from member " + host + ":" + port + " " 
					   + table.toString());
			
			logger.info("Members state from member " + host + ":" + port + " seen by members: " 
					   + table.toStringSeenBy());
			
			boolean convergedState = true;
			for(MemberState ms: table.getTable()) {
				if(!table.getSeenByMembers().contains(ms.getMemberId())) {
					convergedState = false;
					break;
				}
			}
			
			// convergence period - defines time period throughout which cluster state seen by
			//                      all members remains unchanged. Hence cluster state is 
			//						considered stable
			long sp = clusterstateConvergencePeriod;
			if (table.getLastResetTimestamp() + sp >= System.currentTimeMillis()) {
				convergedState = false;
			}
			
			if(convergedState) {
				logger.info("Cluster state is stable");
			} else {
				logger.info("Cluster state is unstable");
			}
		}
	}

}
