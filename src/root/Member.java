package root;

import java.net.Socket;

public class Member {

	private String memberId;
	
	private Socket socket;

	public Member(String memberId, Socket socket) {
		super();
		this.memberId = memberId;
		this.socket = socket;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
}
