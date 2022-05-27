package root;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.State;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.Test;

public class InboundConnectionManagerTest {

	@Test
	public void testRun() throws IOException, InterruptedException {
		FailInputStreamAdapter adapter = new FailInputStreamAdapter();
		
		ServerSocket ssMock = mock(ServerSocket.class);
		Socket sMock = mock(Socket.class);
		when(ssMock.accept()).thenReturn(sMock);
		when(sMock.getInputStream()).thenReturn(adapter);
		
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		
		InboundConnectionManager icm = 
				new InboundConnectionManager(ssMock, "localhost", 8081, cr);
		Thread t = new Thread(icm);
		t.start();

		Thread.sleep(1000);
		assertFalse(t.getState().equals(State.TERMINATED));
	}
	
	@Test
	public void testAcceptInboundConnection() throws IOException, 
													 IllegalAccessException, 
													 IllegalArgumentException, 
													 InvocationTargetException, 
													 NoSuchMethodException, 
													 SecurityException 
	{
		InputStreamAdapter adapter = new InputStreamAdapter();
		
		ServerSocket ssMock = mock(ServerSocket.class);
		Socket sMock = mock(Socket.class);
		when(ssMock.accept()).thenReturn(sMock);
		when(sMock.getInputStream()).thenReturn(adapter);
		
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		
		InboundConnectionManager icm = new InboundConnectionManager(ssMock, null, 0, cr);
		
		Method m = icm.getClass().getDeclaredMethod("acceptInboundConnection", (Class<?>[]) null);
		m.setAccessible(true);
		String member = (String) m.invoke(icm, (Object[]) null);
		m.setAccessible(false);
		
		assertEquals(adapter.getMember(), member);
	}
	
	private static class InputStreamAdapter extends InputStream {
		
		private String s = "192.168.0.13:8081";
		
		@Override
		public int read() throws IOException {
			return s.length();
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

	private static class FailInputStreamAdapter extends InputStream {
		@Override
		public int read() throws IOException {
			throw new IOException();
		}
	}
	
}
