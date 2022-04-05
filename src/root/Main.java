package root;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger logger 
	  = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		List<InetSocketAddress> nodes = AppProperties.getNodes();
		
		logger.info("Test logger");
		
		int port = Integer.parseInt(args[0]);
		
		final InetSocketAddress isa = new InetSocketAddress("localhost", port);
		new Thread(() ->{
			try {
				ServerSocket serverSocket = new ServerSocket();
				serverSocket.bind(isa);
				for(;;) {
					Socket clientSocket = serverSocket.accept();
					
					int b1 = clientSocket.getInputStream().read();
					int b2 = clientSocket.getInputStream().read();
					int b3 = clientSocket.getInputStream().read();
					int b4 = clientSocket.getInputStream().read();
					
					int length = b1^b2^b3^b4;
					
					byte[] bClientNode = new byte[length];
					clientSocket.getInputStream().read(bClientNode);
					
					logger.info("Inbound connection [" + isa.getHostName() + ":" + isa.getPort() + 
								" accepts connection from " + new String(bClientNode) + "]");
					Thread.sleep(5000);
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		for(int i=0; i<nodes.size(); i++) {
			InetSocketAddress node = nodes.get(i);
			if(!node.equals(isa)) {
				try {
					Socket s = new Socket(node.getHostName(), node.getPort());
					logger.info("Outbound connection [" + isa.getHostName() + ":" + isa.getPort() + 
								" connects to " + node.getHostName() + ":" + node.getPort() + "]");
					
					String localNode = isa.getHostName() + ":" + isa.getPort();
					byte[] bLocalNode = localNode.getBytes();
					s.getOutputStream().write(bLocalNode.length&0xff);
					s.getOutputStream().write(bLocalNode.length&0xff00);
					s.getOutputStream().write(bLocalNode.length&0xff0000);
					s.getOutputStream().write(bLocalNode.length&0xff000000);
					s.getOutputStream().write(bLocalNode);
					
				} catch (IOException e) {
					i--;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
