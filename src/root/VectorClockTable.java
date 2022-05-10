package root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VectorClockTable implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<VectorClock> table;
	
	private Set<String> seenByMembers;
	
	public VectorClockTable() {
		super();
		this.table = new ArrayList<>();
		this.seenByMembers = new HashSet<>();
	}

	public Set<String> getSeenByMembers() {
		return seenByMembers;
	}

	public List<VectorClock> getTable() {
		return table;
	}

	public void setTable(List<VectorClock> table) {
		this.table = table;
	}

	public void add(VectorClock vc) {
		table.add(vc);
	}
	
	public void addSeenBy(String seenMemberId) {
		seenByMembers.add(seenMemberId);
	}
	
}
