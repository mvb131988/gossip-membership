package root;

import java.net.Socket;

public class Member {

	private String hostPort;
	
	private Socket socket;

	public Member(String hostPort, Socket socket) {
		super();
		this.hostPort = hostPort;
		this.socket = socket;
	}

	public String getHostPort() {
		return hostPort;
	}

	public void setHostPort(String hostPort) {
		this.hostPort = hostPort;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
}
