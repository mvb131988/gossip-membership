package root;

public class MemberStateMonitor {

	private String memberId;
	private MemberStateTable table;
	
	public MemberStateMonitor() {
		
	}
	
	public MemberStateMonitor(String memberId) {
		super();
		this.memberId = memberId;
		this.table = new MemberStateTable();
		this.table.add(new MemberState(this.memberId, 1, System.currentTimeMillis(), "ACTIVE"));
		this.table.addSeenBy(memberId);
	}

	/**
	 * Updates Lamport timestamp and local timestamp (local timestamp is the last timestamp
	 * on the given machine obtained by System.currentTimeMillis(); it shows when member
	 * record was updated last time) of the current member only!
	 * 
	 * @param currentTimestamp - timestamp of the running member
	 *
	 * @return vector clock table of the current member
	 */
	public synchronized 
	VectorClockTable updateMemberStateAndGetVectorClockTable(long currentTimestamp) {
		updateMemberState(currentTimestamp);
		return table.toVectorClockTable();
	}

	private void updateMemberState(long currentTimestamp) {
		for (MemberState state : table.getTable()) {
			if (state.getMemberId().equals(memberId)) {
				state.setLamportTimestamp(state.getLamportTimestamp() + 1);
				state.setLocalTimestamp(currentTimestamp);
			}
		}
	}
	
	/**
	 * Updates Lamport timestamp, local timestamp and member state (current member + the rest of
	 * cluster members). Cluster members are updated from received vectorClockTable.
	 * 
	 * Together with member state table seenByMember set is updated. It shows members that have
	 * seen the same set of ACTIVE members as the current one. This means that seenByMember is 
	 * updated (new entry is added) if and only if set of ACTIVE members at the current member
	 * exactly matches to set of members in vectorClock table. If not seenByMember is reset.
	 * 
	 * Here 3 cases are possible:
	 * - member state table matches vector clock table. seenByMember is updated with senderMember.
	 * - member state table is bigger than vector clock table. seenByMember is reset.
	 * - member state table is smaller than vector clock table. seenByMember is reset.
	 * 
	 * @param vectorClockTable - vectorClock table received from senderMember
	 * @param senderMember - external member that sends vectorClock
	 * @param currentTimestamp - timestamp of the running member
	 */
	public synchronized void 
	updateMembersState(VectorClockTable vectorClockTable, 
					   String senderMember, 
					   long currentTimestamp) 
	{
		updateMemberState(currentTimestamp);

		boolean insertMemberState = false;
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
										  vc.getLamportTimestamp(), 
										  currentTimestamp, 
										  "ACTIVE")
						 );
				insertMemberState = true;
			} else {
				if(ms1.getLamportTimestamp() < vc.getLamportTimestamp()) {
					ms1.setLamportTimestamp(vc.getLamportTimestamp());
					ms1.setLocalTimestamp(currentTimestamp);
					ms1.setState("ACTIVE");
				}
			}
		}
		
		////////////////////////////////////////////////////////////////////////////////////
		// all entries from vectorClockTable are present in memberStateTable and are ACTIVE
		// because all of them either inserted or updated in the previous step.
		// 
		// the only required step is to check if all records from member state table are in
		// vector clock table (if not number of records in vector clock table is smaller than
		// the number in member state table)
		////////////////////////////////////////////////////////////////////////////////////
		
		if(insertMemberState) {
			table.resetSeenBy();
			table.addSeenBy(memberId);
			return;
		}
		
		boolean allMSsInVCT = true;
		for(MemberState ms: table.getTable()) {
			if(!ms.getState().equals("ACTIVE")) {
				continue;
			}
			
			// find current member state in vector clock table 
			boolean msFound = false;
			for(VectorClock vc: vectorClockTable.getTable()) {
				if(ms.getMemberId().equals(vc.getMemberId())) {
					msFound = true;
					break;
				}
			}
			
			if (!msFound) {
				allMSsInVCT = false;
				break;
			}
		}
		
		if(allMSsInVCT) {
			table.addSeenBy(senderMember);
		} else {
			table.resetSeenBy();
			table.addSeenBy(memberId);
		}
		
	}
	
	/**
	 * Inactivates members that are currently ACTIVE but no healthcheck/gossip messages
	 * received within the timeout (since last message).
	 * 
	 * @param timestamp - timestamp of the running member
	 * @param timeout - timeout between two sequential gossip messages. When it's reached member 
	 * 					might be considered INACTIVE
	 */
	public synchronized void inactivateMember(long timestamp, long timeout) {
		for(MemberState state: table.getTable()) {
			if(state.getState().equals("ACTIVE") &&
			   state.getMemberId() != memberId && 
			   state.getLocalTimestamp() + timeout < timestamp) 
			{
				state.setState("INACTIVE");
				table.resetSeenBy();
				table.addSeenBy(memberId);
			}
		}
	}
	
	/**
	 * Copies member state table into new object.
	 * 
	 * @return - copy of member state table.
	 */
	public synchronized MemberStateTable copyMemberStateTable() {
		MemberStateTable copy = new MemberStateTable();
		
		for(MemberState ms: table.getTable()) {
			copy.add(new MemberState(ms.getMemberId(), 
									 ms.getLamportTimestamp(), 
									 ms.getLocalTimestamp(), 
									 ms.getState()));
		}
		
		for(String memberId: table.getSeenByMembers()) {
			copy.addSeenBy(memberId);
		}
		
		return copy;
	}
	
}
