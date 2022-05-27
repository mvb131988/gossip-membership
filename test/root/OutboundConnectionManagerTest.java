package root;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class OutboundConnectionManagerTest {

	@Test
	public void testMaintainOutboundConnection1() throws IOException, 
													 IllegalAccessException, 
													 IllegalArgumentException, 
													 InvocationTargetException, 
													 NoSuchMethodException, 
													 SecurityException, 
													 InterruptedException 
	{
		ThreadCaptor tc = new ThreadCaptor();
		Thread t = new Thread(tc);
		t.start();
		
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		
		List<InetSocketAddress> members = new ArrayList<>();
		members.add(new InetSocketAddress("localhost", 8081));
		members.add(new InetSocketAddress("localhost", 8082));
		
		OutboundConnectionManager icm = 
				new OutboundConnectionManager(members, "localhost", 8082, cr);
		
		ArgumentCaptor<String> arg1= ArgumentCaptor.forClass(String.class);
		Method m = icm.getClass().getDeclaredMethod("maintainOutboundConnections", 
													(Class<?>[]) null);
		m.setAccessible(true);
		m.invoke(icm, (Object[]) null);
		m.setAccessible(false);
		
		t.join();
		
		assertEquals("localhost:8082", tc.getMember());
		
		verify(cr, times(1)).registerOutbound(arg1.capture(), any());
		String v1 = arg1.getValue();
		assertEquals("localhost:8081",v1);
	}
	
	@Test
	public void testMaintainOutboundConnection2() throws IOException, 
													 IllegalAccessException, 
													 IllegalArgumentException, 
													 InvocationTargetException, 
													 NoSuchMethodException, 
													 SecurityException, 
													 InterruptedException 
	{
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		
		List<InetSocketAddress> members = new ArrayList<>();
		members.add(new InetSocketAddress("localhost", 8081));
		members.add(new InetSocketAddress("localhost", 8082));
		
		OutboundConnectionManager icm = 
				new OutboundConnectionManager(members, "localhost", 8082, cr);
		
		Method m = icm.getClass().getDeclaredMethod("maintainOutboundConnections", 
													(Class<?>[]) null);
		m.setAccessible(true);
		m.invoke(icm, (Object[]) null);
		m.setAccessible(false);
		
		verify(cr, times(0)).registerOutbound(any(), any());
	}
	
	@Test
	public void testMaintainOutboundConnection3() throws IOException, 
													 IllegalAccessException, 
													 IllegalArgumentException, 
													 InvocationTargetException, 
													 NoSuchMethodException, 
													 SecurityException, 
													 InterruptedException 
	{
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		when(cr.existOutbound("localhost:8081")).thenReturn(true);
		
		List<InetSocketAddress> members = new ArrayList<>();
		members.add(new InetSocketAddress("localhost", 8081));
		members.add(new InetSocketAddress("localhost", 8082));
		
		OutboundConnectionManager icm = 
				new OutboundConnectionManager(members, "localhost", 8082, cr);
		
		Method m = icm.getClass().getDeclaredMethod("maintainOutboundConnections", 
													(Class<?>[]) null);
		m.setAccessible(true);
		m.invoke(icm, (Object[]) null);
		m.setAccessible(false);
		
		verify(cr, times(0)).registerOutbound(any(), any());
	}
	
	@Test
	public void testMaintainOutboundConnection4() throws IOException, 
													 IllegalAccessException, 
													 IllegalArgumentException, 
													 InvocationTargetException, 
													 NoSuchMethodException, 
													 SecurityException, 
													 InterruptedException 
	{
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		
		List<InetSocketAddress> members = new ArrayList<>();
		members.add(new InetSocketAddress("localhost", 8082));
		
		OutboundConnectionManager icm = 
				new OutboundConnectionManager(members, "localhost", 8082, cr);
		
		Method m = icm.getClass().getDeclaredMethod("maintainOutboundConnections", 
													(Class<?>[]) null);
		m.setAccessible(true);
		m.invoke(icm, (Object[]) null);
		m.setAccessible(false);
		
		verify(cr, times(0)).registerOutbound(any(), any());
	}
	
	
	private static class ThreadCaptor implements Runnable {
		
		private String member;
		
		@Override
		public void run() {
			ServerSocket ss = null;
			try {
				ss = new ServerSocket(8081);
				Socket s = ss.accept();

				int length = s.getInputStream().read();
				byte[] bytes = new byte[length];
				s.getInputStream().read(bytes);

				member = new String(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public String getMember() {
			return member;
		}
		
	};
	
}
