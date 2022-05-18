package root;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemberStateTable {

	private List<MemberState> table;
	
	// members that saw current state of MemberStateTable
	private Set<String> seenByMembers;
	
	private Set<String> seenResetByMembers;
	
	private Set<String> seenConfirmResetByMembers;
	
	public MemberStateTable() {
		super();
		this.table = new ArrayList<>();
		this.seenByMembers = new HashSet<>();
		this.seenResetByMembers = new HashSet<>();
		this.seenConfirmResetByMembers = new HashSet<>();
	}

	public List<MemberState> getTable() {
		return table;
	}

	public void setTable(List<MemberState> table) {
		this.table = table;
	}

	//TODO: encapsulate addSeenBy here, make it private
	public void add(MemberState vc) {
		table.add(vc);
	}
	
	public void resetSeenBy() {
		seenByMembers.clear();
	}
	
	public void addSeenBy(String memberId) {
		seenByMembers.add(memberId);
	}
	
	public Set<String> getSeenByMembers() {
		return seenByMembers;
	}
	
	public void addSeenResetBy(String memberId) {
		seenResetByMembers.add(memberId);
	}
	
	public void addSeenConfirmResetBy(String memberId) {
		seenConfirmResetByMembers.add(memberId);
	}
	
	public void resetSeenResetBy() {
		seenResetByMembers.clear();
	}

	public void resetSeenConfirmResetBy() {
		seenConfirmResetByMembers.clear();
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
			sb.append(ms.getMemberId() + "->" + ms.getState() + "->" +
					  ms.getLamportTimestamp() + ";");
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
	
	public boolean seenResetByAll() {
		for(MemberState ms: table) {
			if (ms.getState().equals("ACTIVE")) {
				if(!seenResetByMembers.contains(ms.getMemberId())) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean seenConfirmResetByAll() {
		for(MemberState ms: table) {
			if (ms.getState().equals("ACTIVE")) {
				if(!seenConfirmResetByMembers.contains(ms.getMemberId())) {
					return false;
				}
			}
		}
		return true;
	}
	
}
