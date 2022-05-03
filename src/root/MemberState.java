package root;

public class MemberState {

	private String memberId;
	private int lamportTimestamp;
	private long localTimestamp;
	// ACTIVE/INACTIVE
	private String state;

	public MemberState(String memberId, int lamportTimestamp, long localTimestamp, String state) {
		super();
		this.memberId = memberId;
		this.lamportTimestamp = lamportTimestamp;
		this.localTimestamp = localTimestamp;
		this.state = state;
	}

	public VectorClock toVectorClock() {
		return new VectorClock(memberId, lamportTimestamp);
	}
	
	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public int getLamportTimestamp() {
		return lamportTimestamp;
	}

	public void setLamportTimestamp(int lamportTimestamp) {
		this.lamportTimestamp = lamportTimestamp;
	}

	public long getLocalTimestamp() {
		return localTimestamp;
	}

	public void setLocalTimestamp(long localTimestamp) {
		this.localTimestamp = localTimestamp;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
