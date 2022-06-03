package root;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;


public class GossipReceiverTest {

	private long timeout;
	
	public GossipReceiverTest() {
		this.timeout = AppPropertiesTest.gossipReceiveFrequency();
	}
	
	@Test
	public void testReceive1() throws IllegalAccessException, 
									  IllegalArgumentException, 
									  InvocationTargetException, 
									  NoSuchMethodException, 
									  SecurityException, 
									  IOException 
	{
		InputStreamAdapter isa = new InputStreamAdapter();
		
		Socket s = mock(Socket.class);
		when(s.getInputStream()).thenReturn(isa);
		
		Member m = new Member("localhost:8082", s);
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		when(cr.nextInbound()).thenReturn(m);
		
		MemberStateMonitor msm = mock(MemberStateMonitor.class);
		
		GossipReceiver gr = new GossipReceiver("localhost", 8081, cr, msm, timeout);
		
		long timestamp = System.currentTimeMillis();
		
		ArgumentCaptor<VectorClockTable> arg1= ArgumentCaptor.forClass(VectorClockTable.class);
		ArgumentCaptor<String> arg2= ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Long> arg3= ArgumentCaptor.forClass(Long.class);
		
		Method md = gr.getClass().getDeclaredMethod("receive", Long.class);
		md.setAccessible(true);
		md.invoke(gr, timestamp);
		md.setAccessible(false);
		
		verify(msm, times(1)).updateMembersState(arg1.capture(), arg2.capture(), arg3.capture());
		VectorClockTable vct1 = arg1.getValue();
		String host1 = arg2.getValue();
		Long timestamp1 = arg3.getValue();
		
		assertAll("vct",
				() -> assertEquals(12, vct1.getTable().size()),
				() -> assertEquals("localhost:8081", vct1.getTable().get(0).getMemberId()),
				() -> assertEquals(14, vct1.getTable().get(0).getLamportTimestamp()),
				() -> assertEquals("localhost:8082", vct1.getTable().get(1).getMemberId()),
				() -> assertEquals(12, vct1.getTable().get(1).getLamportTimestamp()),
				() -> assertEquals("localhost:8083", vct1.getTable().get(2).getMemberId()),
				() -> assertEquals(142, vct1.getTable().get(2).getLamportTimestamp()),
				() -> assertEquals("localhost:8084", vct1.getTable().get(3).getMemberId()),
				() -> assertEquals(112, vct1.getTable().get(3).getLamportTimestamp()),
				() -> assertEquals("localhost:8085", vct1.getTable().get(4).getMemberId()),
				() -> assertEquals(13, vct1.getTable().get(4).getLamportTimestamp()),
				() -> assertEquals("localhost:8086", vct1.getTable().get(5).getMemberId()),
				() -> assertEquals(142, vct1.getTable().get(5).getLamportTimestamp()),
				() -> assertEquals("localhost:8087", vct1.getTable().get(6).getMemberId()),
				() -> assertEquals(113, vct1.getTable().get(6).getLamportTimestamp()),
				() -> assertEquals("localhost:8088", vct1.getTable().get(7).getMemberId()),
				() -> assertEquals(14, vct1.getTable().get(7).getLamportTimestamp()),
				() -> assertEquals("localhost:8089", vct1.getTable().get(8).getMemberId()),
				() -> assertEquals(122, vct1.getTable().get(8).getLamportTimestamp()),
				() -> assertEquals("localhost:8090", vct1.getTable().get(9).getMemberId()),
				() -> assertEquals(172, vct1.getTable().get(9).getLamportTimestamp()),
				() -> assertEquals("localhost:8091", vct1.getTable().get(10).getMemberId()),
				() -> assertEquals(182, vct1.getTable().get(10).getLamportTimestamp()),
				() -> assertEquals("localhost:8092", vct1.getTable().get(11).getMemberId()),
				() -> assertEquals(2, vct1.getTable().get(11).getLamportTimestamp()),
				() -> assertEquals(1, vct1.getSeenByMembers().size()),
				() -> assertEquals("localhost:8082", vct1.getSeenByMembers().iterator().next()));
		
		assertEquals("localhost:8082", host1);
		assertEquals(timestamp, timestamp1);
	}
	
	private static class InputStreamAdapter extends InputStream {
		
		private byte[] stream;
		
		// s length is split in 4 bytes, lowest come first.
		// arr saves s length
		private int[] arr = {181, 2, 0, 0};  
		
		private int pos = 0;
		
		public InputStreamAdapter() throws IOException {
			VectorClockTable vct = new VectorClockTable();
			vct.add(new VectorClock("localhost:8081", 14));
			vct.add(new VectorClock("localhost:8082", 12));
			vct.add(new VectorClock("localhost:8083", 142));
			vct.add(new VectorClock("localhost:8084", 112));
			vct.add(new VectorClock("localhost:8085", 13));
			vct.add(new VectorClock("localhost:8086", 142));
			vct.add(new VectorClock("localhost:8087", 113));
			vct.add(new VectorClock("localhost:8088", 14));
			vct.add(new VectorClock("localhost:8089", 122));
			vct.add(new VectorClock("localhost:8090", 172));
			vct.add(new VectorClock("localhost:8091", 182));
			vct.add(new VectorClock("localhost:8092", 2));
			vct.addSeenBy("localhost:8082");
			
			GossipMessage gm = new GossipMessage(vct);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(gm);
			oos.flush();
			stream = bos.toByteArray();
		}
		
		@Override
		public int read() throws IOException {
			return arr[pos++];
		}
		
		@Override
		public int read(byte[] acc) throws IOException {
			byte[] bytes = stream;
			for(int i=0; i<acc.length; i++) {
				acc[i] = bytes[i];
			}
			return -1;
		}
		
	}
	
	@Test
	public void testReceive2() throws IllegalAccessException, 
									  IllegalArgumentException, 
									  InvocationTargetException, 
									  NoSuchMethodException, 
									  SecurityException, 
									  IOException 
	{
		FailInputStreamAdapter2 fisa = new FailInputStreamAdapter2();
		
		Socket s = mock(Socket.class);
		when(s.getInputStream()).thenReturn(fisa);
		
		Member m = new Member("localhost:8082", s);
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		when(cr.nextInbound()).thenReturn(m);
		
		MemberStateMonitor msm = mock(MemberStateMonitor.class);
		
		GossipReceiver gr = new GossipReceiver("localhost", 8081, cr, msm, timeout);
		
		long timestamp = System.currentTimeMillis();
		
		ArgumentCaptor<String> arg1= ArgumentCaptor.forClass(String.class);
		
		Method md = gr.getClass().getDeclaredMethod("receive", Long.class);
		md.setAccessible(true);
		md.invoke(gr, timestamp);
		md.setAccessible(false);
		
		verify(msm, times(0)).updateMembersState(any(), any(), any());
		verify(cr, times(1)).removeConnection(arg1.capture());
		
		String memberId = arg1.getValue();
		assertEquals("localhost:8082", memberId);
	}

	private static class FailInputStreamAdapter2 extends InputStream {
		
		@Override
		public int read() throws IOException {
			throw new IOException();
		}
		
	}
	
	@Test
	public void testReceive3() throws IllegalAccessException, 
									  IllegalArgumentException, 
									  InvocationTargetException, 
									  NoSuchMethodException, 
									  SecurityException, 
									  IOException 
	{
		FailInputStreamAdapter3 fisa = new FailInputStreamAdapter3();
		
		Socket s = mock(Socket.class);
		when(s.getInputStream()).thenReturn(fisa);
		
		Member m = new Member("localhost:8082", s);
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		when(cr.nextInbound()).thenReturn(m);
		
		MemberStateMonitor msm = mock(MemberStateMonitor.class);
		
		GossipReceiver gr = new GossipReceiver("localhost", 8081, cr, msm, timeout);
		
		long timestamp = System.currentTimeMillis();
		
		ArgumentCaptor<String> arg1= ArgumentCaptor.forClass(String.class);
		
		Method md = gr.getClass().getDeclaredMethod("receive", Long.class);
		md.setAccessible(true);
		md.invoke(gr, timestamp);
		md.setAccessible(false);
		
		verify(msm, times(0)).updateMembersState(any(), any(), any());
		verify(cr, times(1)).removeConnection(arg1.capture());
		
		String memberId = arg1.getValue();
		assertEquals("localhost:8082", memberId);
	}

	private static class FailInputStreamAdapter3 extends InputStream {
		
		// s length is split in 4 bytes, lowest come first.
		// arr saves s length
		private int[] arr = {181, 2, 0, 0};  
		
		private int pos = 0;
		
		public FailInputStreamAdapter3() throws IOException {
		}
		
		@Override
		public int read() throws IOException {
			return arr[pos++];
		}
		
		@Override
		public int read(byte[] acc) throws IOException {
			return -1;
		}
		
	}
	
	@Test
	public void testReceive4() throws IllegalAccessException, 
									  IllegalArgumentException, 
									  InvocationTargetException, 
									  NoSuchMethodException, 
									  SecurityException, 
									  IOException 
	{
		ConnectionRegistry cr = mock(ConnectionRegistry.class);
		when(cr.nextInbound()).thenReturn(null);
		
		MemberStateMonitor msm = mock(MemberStateMonitor.class);
		
		GossipReceiver gr = new GossipReceiver("localhost", 8081, cr, msm, timeout);
		
		long timestamp = System.currentTimeMillis();
		
		Method md = gr.getClass().getDeclaredMethod("receive", Long.class);
		md.setAccessible(true);
		md.invoke(gr, timestamp);
		md.setAccessible(false);
		
		verify(msm, times(0)).updateMembersState(any(), any(), any());
		verify(cr, times(0)).removeConnection(any());
	}
	
}
