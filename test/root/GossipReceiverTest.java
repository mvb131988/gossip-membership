package root;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.Test;


public class GossipReceiverTest {

	@Test
	public void test() throws IOException {
		String s = "root.GossipReceiver - Member localhost:8082 receives vector clock "
				 + "{localhost:8081->14, localhost:8082->12, localhost:8083->142, "
				 + "localhost:8084->112, localhost:8085->13, localhost:8086->142, "
				 + "localhost:8087->113, localhost:8088->14, localhost:8089->122, "
				 + "localhost:8090->172, localhost:8091->182, localhost:8092->2} from "
				 + "localhost:8081";
		byte[] b = s.getBytes();
		
		InputStreamAdapter adapter = new InputStreamAdapter();
		
		ServerSocket ssMock = mock(ServerSocket.class);
		Socket sMock = mock(Socket.class);
		when(ssMock.accept()).thenReturn(sMock);
		when(sMock.getInputStream()).thenReturn(adapter);
		
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		
		InboundConnectionManager icm = new InboundConnectionManager(ssMock, null, 0, cr);
		Thread t = new Thread(icm);
		t.start();
		t.interrupt();
		
		int n = 467;
	}
	
private static class InputStreamAdapter extends InputStream {
		
//		private String s = 
//					"root.GossipReceiver - Member localhost:8082 receives vector clock "
//				 + "{localhost:8081->14, localhost:8082->12, localhost:8083->142, "
//				 + "localhost:8084->112, localhost:8085->13, localhost:8086->142, "
//				 + "localhost:8087->113, localhost:8088->14, localhost:8089->122, "
//				 + "localhost:8090->172, localhost:8091->182, localhost:8092->2} from "
//				 + "localhost:8081";
		
		private String s = "192.168.0.13:8081";
		
		// s length is split in 4 bytes, lowest come first.
		// arr saves s length
		private int[] arr = {17, 0, 0, 0};  
		
		private int pos = 0;
		
		@Override
		public int read() throws IOException {
			return arr[pos++];
		}
		
		@Override
		public int read(byte[] acc) throws IOException {
			byte[] bytes = s.getBytes();
			for(int i=0; i<acc.length; i++) {
				acc[i] = bytes[i];
			}
			return -1;
		}
		
		public String getMember() {
			return s;
		}
		
	}

@Test
public void testRestoreLength() {
	int n = 2_147_483_637;
}
	
}
