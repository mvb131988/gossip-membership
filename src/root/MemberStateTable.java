package root;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemberStateTable {

	private List<MemberState> table;
	
	// members that saw current state of MemberStateTable
	private Set<String> seenByMembers;
	
	private long lastResetTimestamp;
	
	public MemberStateTable() {
		super();
		this.table = new ArrayList<>();
		this.seenByMembers = new HashSet<>();
		this.lastResetTimestamp = System.currentTimeMillis();
	}

	public List<MemberState> getTable() {
		return table;
	}

	public void setTable(List<MemberState> table) {
		this.table = table;
	}

	public void add(MemberState vc) {
		table.add(vc);
	}
	
	public void resetSeenBy() {
		seenByMembers.clear();
		this.lastResetTimestamp = System.currentTimeMillis();
	}
	
	public void addSeenBy(String memberId) {
		seenByMembers.add(memberId);
	}
	
	public void addSeenBy(Set<String> membersIds) {
		seenByMembers.addAll(membersIds);
	}
	
	public Set<String> getSeenByMembers() {
		return seenByMembers;
	}

	public void setSeenByMembers(Set<String> seenByMembers) {
		this.seenByMembers = seenByMembers;
	}
	
	public VectorClockTable toVectorClockTable() {
		VectorClockTable vectorClockTable = new VectorClockTable();
		
		for(MemberState ms: table) {
			if(ms.getState().equals("ACTIVE")) {
				vectorClockTable.add(ms.toVectorClock());
			}
		}
		
		for(String memberId: seenByMembers) {
			vectorClockTable.addSeenBy(memberId);
		}
		
		return vectorClockTable;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		
		for(MemberState ms: table) {
			sb.append(ms.getMemberId() + "->" + ms.getState() + ";");
		}
		
		if(sb.length() > 1) {
			sb.delete(sb.length()-1, sb.length());
		}
		
		sb.append("}");
		
		return sb.toString();
	}
	
	public String toStringSeenBy() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		
		for(String seenBy: seenByMembers) {
			sb.append(seenBy + ";");
		}
		
		if(sb.length() > 1) {
			sb.delete(sb.length()-1, sb.length());
		}
		
		sb.append("}");
		
		return sb.toString();
	}

	public long getLastResetTimestamp() {
		return lastResetTimestamp;
	}

	public void setLastResetTimestamp(long lastResetTimestamp) {
		this.lastResetTimestamp = lastResetTimestamp;
	}
	
}
