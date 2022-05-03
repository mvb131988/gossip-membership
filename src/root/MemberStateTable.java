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
	
}
