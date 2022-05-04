package root;

public class TimeoutConnectionManager implements Runnable {

	private MemberStateManager manager;
	
	public TimeoutConnectionManager(MemberStateManager manager) {
		this.manager = manager;
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
			
			manager.inactivateMember(System.currentTimeMillis(), 200_000);
		}
	}

}
