package root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VectorClockTable implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<VectorClock> table;
	
	public VectorClockTable() {
		super();
		this.table = new ArrayList<>();
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
	
}
