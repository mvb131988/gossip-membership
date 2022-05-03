package root;

public class MemberStateManager {

	private String memberId;
	private MemberStateTable table;
	
	public MemberStateManager(String memberId) {
		super();
		this.memberId = memberId;
		this.table = new MemberStateTable();
		this.table.add(new MemberState(this.memberId, 1, System.currentTimeMillis(), "ACTIVE"));
	}

	public synchronized VectorClockTable updateMemberStateAndGetVectorClockTable() {
		for(MemberState state: table.getTable()) {
			if (state.getMemberId().equals(memberId)) {
				state.setLamportTimestamp(state.getLamportTimestamp() + 1);
				state.setLocalTimestamp(System.currentTimeMillis());
			}
		}
		return table.toVectorClockTable();
	}
	
	public synchronized void updateMembersState(VectorClockTable vectorClockTable) {
		for(MemberState state: table.getTable()) {
			if (state.getMemberId().equals(memberId)) {
				state.setLamportTimestamp(state.getLamportTimestamp() + 1);
				state.setLocalTimestamp(System.currentTimeMillis());
			}
		}
		
		for(VectorClock vc: vectorClockTable.getTable()) {
			MemberState ms1 = null;
			
			for(MemberState ms: table.getTable()) {
				if(ms.getMemberId().equals(vc.getMemberId())) {
					ms1 = ms;
					break;
				}
			}
			
			if(ms1 == null) {
				table.add(new MemberState(vc.getMemberId(), 
										  1, 
										  System.currentTimeMillis(), 
										  "ACTIVE")
						 );
			} else {
				if(ms1.getLamportTimestamp() < vc.getLamportTimestamp()) {
					ms1.setLamportTimestamp(vc.getLamportTimestamp());
					ms1.setLocalTimestamp(System.currentTimeMillis());
					ms1.setState("ACTIVE");
				}
			}
		}
	}
	
	public synchronized void inactivateMember(long timestamp, long timeout) {
		for(MemberState state: table.getTable()) {
			if(state.getLocalTimestamp() + timeout > timestamp) {
				state.setState("INACTIVE");
			}
		}
	}
	
}
