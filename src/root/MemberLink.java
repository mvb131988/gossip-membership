package root;

import java.net.Socket;

public class MemberLink {

	// when external member is connected to current then connection is called inbound
	private Socket inboundConnection;
	// when current member is connected to external then connection is called outbound
	private Socket outboundConnection;
	
	private int orderIndex;
	
	public MemberLink() {
	}
	
	public MemberLink(int orderIndex) {
		this.orderIndex = orderIndex;
	}
	
	public Socket getOutboundConnection() {
		return outboundConnection;
	}
	
	public void setOutboundConnection(Socket outboundConnection) {
		this.outboundConnection = outboundConnection;
	}

	public Socket getInboundConnection() {
		return inboundConnection;
	}

	public void setInboundConnection(Socket inboundConnection) {
		this.inboundConnection = inboundConnection;
	}
	
	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}
	
}
