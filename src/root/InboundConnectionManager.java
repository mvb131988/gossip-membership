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
	
	private String host;
	
	private int port;
	
	private ConnectionRegistry cr;
	
	private long timeout;
	
	public InboundConnectionManager(ServerSocket ss, 
									String host, 
									int port, 
									ConnectionRegistry cr,
									long timeout) 
	{
		this.ss = ss;
		this.host = host;
		this.port = port;
		this.cr = cr;
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		for (;;) {
			try {
				String member = acceptInboundConnection();

				logger.info("Inbound connection [" + host + ":" + port + 
						" accepts connection from " + member + "]");

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

		// host name of the connected member
		byte[] bMember = new byte[length];
		clientSocket.getInputStream().read(bMember);
		String member = new String(bMember);
		
		cr.registerInbound(member, clientSocket);
		
		return member;
	}

}
