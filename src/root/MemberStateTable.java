package root;

import java.util.ArrayList;
import java.util.List;

public class MemberStateTable {
	
	private List<MemberState> table;
	
	public MemberStateTable() {
		super();
		this.table = new ArrayList<>();
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
	
	public VectorClockTable toVectorClockTable() {
		VectorClockTable vectorClockTable = new VectorClockTable();
		
		for(MemberState ms: table) {
			if(ms.getState().equals("ACTIVE")) {
				vectorClockTable.add(ms.toVectorClock());
			}
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
	
}
