package root;

import java.io.Serializable;
import java.util.Map;

public class GossipMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String, Integer> vectorClock;

	public GossipMessage(Map<String, Integer> vectorClock) {
		this.vectorClock = vectorClock;
	}
	
	public Map<String, Integer> getVectorClock() {
		return vectorClock;
	}
	
	public void setVectorClock(Map<String, Integer> vectorClock) {
		this.vectorClock = vectorClock;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		
		for(Map.Entry<String, Integer> entry: vectorClock.entrySet()) {
			sb.append(entry.getKey() + "->" + entry.getValue() + ";");
		}
		
		sb.delete(sb.length()-1, sb.length());
		
		sb.append("}");
		
		return sb.toString();
	}
	
}
