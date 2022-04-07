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
	
	public InboundConnectionManager(ServerSocket ss, String host, int port) {
		this.ss = ss;
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			for (;;) {
				Socket clientSocket = ss.accept();

				int b1 = clientSocket.getInputStream().read();
				int b2 = clientSocket.getInputStream().read();
				int b3 = clientSocket.getInputStream().read();
				int b4 = clientSocket.getInputStream().read();

				int length = b1 ^ b2 ^ b3 ^ b4;

				byte[] bClientNode = new byte[length];
				clientSocket.getInputStream().read(bClientNode);

				logger.info("Inbound connection [" + host + ":" + port
						+ " accepts connection from " + new String(bClientNode) + "]");
				Thread.sleep(5000);
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

}
