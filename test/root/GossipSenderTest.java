package root;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class GossipSenderTest {

	private long timeout;
	
	public GossipSenderTest() {
		this.timeout = AppPropertiesTest.gossipSendFrequency();
	}
	
	@Test
	public void testSendVectorClock1() throws IOException, 
											 IllegalAccessException, 
											 IllegalArgumentException, 
											 InvocationTargetException, 
											 NoSuchMethodException, 
											 SecurityException 
	{
		long timestamp = System.currentTimeMillis();
		
		OutputStreamCaptor osc = new OutputStreamCaptor();
		
		Socket s = mock(Socket.class);
		Member m = new Member("localhost:8081", s);
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		when(cr.nextOutbound()).thenReturn(m);
		when(s.getOutputStream()).thenReturn(osc); 
		
		VectorClockTable vct = new VectorClockTable();
		vct.add(new VectorClock("localhost:8081", 1));
		vct.addSeenBy("localhost:8081");
		MemberStateMonitor msm = mock(MemberStateMonitor.class);
		when(msm.updateMemberStateAndGetVectorClockTable(timestamp)).thenReturn(vct);
		
		GossipSender gs = new GossipSender("localhost", 8082, cr, msm, timeout);
		
		Method md = gs.getClass().getDeclaredMethod("sendVectorClock", Long.class);
		md.setAccessible(true);
		md.invoke(gs, timestamp);
		md.setAccessible(false);
		
		//asserts logic
		int[] arr = osc.getArr();
		int length = arr[0] ^ (arr[1] << 8) ^ (arr[2] << 16) ^ (arr[2] << 32);
		assertEquals(length, osc.getBytes().length);
		
		GossipMessage gossipMessage = null;
		try (ByteArrayInputStream bis = new ByteArrayInputStream(osc.getBytes());
			 ObjectInputStream in = new ObjectInputStream(bis)) 
		{
			gossipMessage = (GossipMessage) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		List<VectorClock> vct1 = gossipMessage.getVectorClock().getTable();
		Set<String> seen = gossipMessage.getVectorClock().getSeenByMembers();
		assertAll("gossipMessage",
				() -> assertEquals(vct.getTable().size(), vct1.size()),
				() -> assertEquals(vct.getSeenByMembers().size(), seen.size()),
				() -> assertEquals(vct.getTable().get(0).getMemberId(), 
						vct1.get(0).getMemberId()),
				() -> assertEquals(vct.getTable().get(0).getLamportTimestamp(), 
						vct1.get(0).getLamportTimestamp()),
				() -> assertEquals(vct.getSeenByMembers().iterator().next(), 
						seen.iterator().next())
		);
	}
	
	private static class OutputStreamCaptor extends OutputStream {

		// length of bytes array is stored here. int length is split
		// in 4 bytes, lower byte come first.
		private int[] arr = new int[4];

		private int pos = 0;
		
		// String message in bytes representation.
		private byte[] bytes;
		
		@Override
		public void write(int b) throws IOException {
			arr[pos++] = b;
		}
		
		@Override
		public void write(byte[] bytes) throws IOException {
			this.bytes = bytes;
		}
		
		public int[] getArr() {
			return arr;
		}

		public byte[] getBytes() {
			return bytes;
		}
		
	}
	
	@Test
	public void testSendVectorClock2() throws IOException, 
											 IllegalAccessException, 
											 IllegalArgumentException, 
											 InvocationTargetException, 
											 NoSuchMethodException, 
											 SecurityException 
	{
		long timestamp = System.currentTimeMillis();
		
		FailOutputStreamAdapter fosa = new FailOutputStreamAdapter();
		
		Socket s = mock(Socket.class);
		Member m = new Member("localhost:8081", s);
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		when(cr.nextOutbound()).thenReturn(m);
		when(s.getOutputStream()).thenReturn(fosa); 
		
		VectorClockTable vct = new VectorClockTable();
		vct.add(new VectorClock("localhost:8081", 1));
		vct.addSeenBy("localhost:8081");
		MemberStateMonitor msm = mock(MemberStateMonitor.class);
		when(msm.updateMemberStateAndGetVectorClockTable(timestamp)).thenReturn(vct);
		
		GossipSender gs = new GossipSender("localhost", 8082, cr, msm, timeout);
		
		Method md = gs.getClass().getDeclaredMethod("sendVectorClock", Long.class);
		md.setAccessible(true);
		md.invoke(gs, timestamp);
		md.setAccessible(false);
		
		verify(cr, times(1)).removeConnection("localhost:8081");
	}
	
	private static class FailOutputStreamAdapter extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			throw new IOException();
		}
	}
	
	@Test
	public void testSendVectorClock3() throws IOException, 
											 IllegalAccessException, 
											 IllegalArgumentException, 
											 InvocationTargetException, 
											 NoSuchMethodException, 
											 SecurityException 
	{
		long timestamp = System.currentTimeMillis();
		
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		when(cr.nextOutbound()).thenReturn(null);
		
		MemberStateMonitor msm = mock(MemberStateMonitor.class);
		when(msm.updateMemberStateAndGetVectorClockTable(timestamp)).thenReturn(null);
		
		GossipSender gs = new GossipSender("localhost", 8082, cr, msm, timeout);
		
		Method md = gs.getClass().getDeclaredMethod("sendVectorClock", Long.class);
		md.setAccessible(true);
		md.invoke(gs, timestamp);
		md.setAccessible(false);
		
		verify(msm, times(0)).updateMemberStateAndGetVectorClockTable(timestamp);
		verify(cr, times(0)).removeConnection(any());
	}
	
}
