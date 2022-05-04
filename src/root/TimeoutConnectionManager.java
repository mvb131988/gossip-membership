package root;

public class TimeoutConnectionManager implements Runnable {

	private MemberStateMonitor monitor;
	
	public TimeoutConnectionManager(MemberStateMonitor monitor) {
		this.monitor = monitor;
	}
	
	@Override
	public void run() {
		for(;;) {
			try {
				Thread.sleep(200_000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			monitor.inactivateMember(System.currentTimeMillis(), 200_000);
		}
	}

}