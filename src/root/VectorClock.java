package root;

import java.io.Serializable;

public class VectorClock implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String memberId;
	private int lamportTimestamp;
	
	public VectorClock() {
		
	}
	
	public VectorClock(String memberId, int lamportTimestamp) {
		super();
		this.memberId = memberId;
		this.lamportTimestamp = lamportTimestamp;
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
	
}
