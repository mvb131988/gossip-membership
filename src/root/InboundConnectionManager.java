package root;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboundConnectionManager implements Runnable {

	private static final Logger logger 
	  = LoggerFactory.getLogger(InboundConnectionManager.class);
	
	private ServerSocket ss;
	
	private ConnectionRegistry cr;
	
	private long timeout;
	
	private String memberId;
	
	public InboundConnectionManager(ServerSocket ss, 
									String host, 
									int port, 
									ConnectionRegistry cr,
									long timeout) 
	{
		this.ss = ss;
		this.cr = cr;
		this.timeout = timeout;
		this.memberId = host + ":" + port;
	}
	
	@Override
	public void run() {
		for (;;) {
			try {
				String inMemberId = acceptInboundConnection();

				logger.info("Inbound connection [" + this.memberId + 
					   " accepts connection from " + inMemberId + "]");

			} catch (IOException | InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
				logger.error("InboundConnectionManager thread has been terminated");
				break;
			}
		}
	}
	
	private String acceptInboundConnection() throws IOException, InterruptedException {
		Socket clientSocket = ss.accept();

		int length = clientSocket.getInputStream().read();

		byte[] bMemberId = new byte[length];
		clientSocket.getInputStream().read(bMemberId);
		String memberId = new String(bMemberId);
		
		cr.registerInbound(memberId, clientSocket);
		
		return memberId;
	}

}
