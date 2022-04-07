package root;

import java.net.Socket;

public class MemberLink {

	// when external member is connected to current then connection is called inbound
	private Socket inboundConnection;
	// when current member is connected to external then connection is called outbound
	private Socket outboundConnection;
	
	public MemberLink() {
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
	
}
