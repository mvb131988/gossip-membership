package root;

public class TimeoutConnectionManager implements Runnable {

	private MemberStateManager manager;
	
	public TimeoutConnectionManager(MemberStateManager manager) {
		this.manager = manager;
	}
	
	@Override
	public void run() {
		for(;;) {
			manager.inactivateMember(System.currentTimeMillis(), 20_000);
			
			try {
				Thread.sleep(20_000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
