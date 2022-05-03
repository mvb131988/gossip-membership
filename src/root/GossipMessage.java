package root;

import java.io.Serializable;

public class GossipMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private VectorClockTable vectorClockTable;

	public GossipMessage(VectorClockTable vectorClockTable) {
		this.vectorClockTable = vectorClockTable;
	}
	
	public VectorClockTable getVectorClock() {
		return vectorClockTable;
	}
	
	public void setVectorClock(VectorClockTable vectorClockTable) {
		this.vectorClockTable = vectorClockTable;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		
		for(VectorClock vc: vectorClockTable.getTable()) {
			sb.append(vc.getMemberId() + "->" + vc.getLamportTimestamp() + ";");
		}
		
		if(sb.length() > 1) {
			sb.delete(sb.length()-1, sb.length());
		}
		
		sb.append("}");
		
		return sb.toString();
	}
	
}
