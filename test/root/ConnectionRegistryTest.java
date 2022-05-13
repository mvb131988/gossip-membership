package root;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ConnectionRegistryTest {

	@Test
	public void testRegisterInbound1() throws NoSuchFieldException,
											  SecurityException, 
											  IllegalArgumentException,
											  IllegalAccessException 
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		MemberLink ml = new MemberLink();
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml);
		Deque<String> inboundQueue = new LinkedList<>();
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "inboundQueue", inboundQueue);
		
		Socket sIn = mock(Socket.class);
		cr.registerInbound("member1", sIn);
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 1),
				() -> assertEquals(registry.get("member1").getInboundConnection(), sIn),
				() -> assertEquals(registry.get("member1").getOutboundConnection(), null));
		
		assertAll("inboundQueue",
				() -> assertEquals(1, inboundQueue.size()),
				() -> assertEquals("member1", inboundQueue.getFirst()));
	}
	
	@Test
	public void testRegisterInbound2() throws NoSuchFieldException,
											  SecurityException, 
											  IllegalArgumentException,
											  IllegalAccessException 
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		MemberLink ml = new MemberLink();
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml);
		Deque<String> inboundQueue = new LinkedList<>();
		inboundQueue.add("member1");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "inboundQueue", inboundQueue);
		
		Socket sIn = mock(Socket.class);
		cr.registerInbound("member2", sIn);
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 2),
				() -> assertEquals(registry.get("member1").getInboundConnection(), null),
				() -> assertEquals(registry.get("member1").getOutboundConnection(), null),
				() -> assertEquals(registry.get("member2").getInboundConnection(), sIn),
				() -> assertEquals(registry.get("member2").getOutboundConnection(), null));
		
		assertAll("inboundQueue",
				() -> assertEquals(2, inboundQueue.size()),
				() -> assertEquals("member1", inboundQueue.getFirst()),
				() -> assertEquals("member2", inboundQueue.getLast()));
	}
	
	@Test
	public void testRegisterOutbound1() throws NoSuchFieldException,
											   SecurityException, 
											   IllegalArgumentException,
											   IllegalAccessException 
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		MemberLink ml = new MemberLink();
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml);
		Deque<String> outboundQueue = new LinkedList<>();
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);
		
		Socket sOut = mock(Socket.class);
		cr.registerOutbound("member1", sOut);
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 1),
				() -> assertEquals(registry.get("member1").getInboundConnection(), null),
				() -> assertEquals(registry.get("member1").getOutboundConnection(), sOut));
		
		assertAll("outboundQueue",
				() -> assertEquals(1, outboundQueue.size()),
				() -> assertEquals("member1", outboundQueue.getFirst()));
	}
	
	@Test
	public void testRegisterOutbound2() throws NoSuchFieldException,
											   SecurityException, 
											   IllegalArgumentException,
											   IllegalAccessException 
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		MemberLink ml = new MemberLink();
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml);
		Deque<String> outboundQueue = new LinkedList<>();
		outboundQueue.add("member1");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);
		
		Socket sOut = mock(Socket.class);
		cr.registerOutbound("member2", sOut);
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 2),
				() -> assertEquals(registry.get("member1").getInboundConnection(), null),
				() -> assertEquals(registry.get("member1").getOutboundConnection(), null),
				() -> assertEquals(registry.get("member2").getInboundConnection(), null),
				() -> assertEquals(registry.get("member2").getOutboundConnection(), sOut));
		
		assertAll("outboundQueue",
				() -> assertEquals(2, outboundQueue.size()),
				() -> assertEquals("member1", outboundQueue.getFirst()),
				() -> assertEquals("member2", outboundQueue.getLast()));
	}
	
	@Test
	public void testRemoveConnection1() throws NoSuchFieldException,
	 									   	   SecurityException, 
	 									   	   IllegalArgumentException,
	 									   	   IllegalAccessException 
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Map<String, MemberLink> registry = mock(MockRegistry.class);
		
		setPrivateFieldValue(cr, "registry", registry);
		
		when(registry.get("member2")).thenReturn(null);
		
		cr.removeConnection("member2");
		verify(registry, times(0)).remove("member2");
	}
	
	@Test
	public void testRemoveConnection2() throws NoSuchFieldException,
	 									   	   SecurityException, 
	 									   	   IllegalArgumentException,
	 									   	   IllegalAccessException, 
	 									   	   IOException 
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sIn = mock(Socket.class);
		Socket sOut = mock(Socket.class);
		doNothing().when(sIn).close();
		doNothing().when(sOut).close();
		MemberLink ml = new MemberLink();
		ml.setInboundConnection(sIn);
		ml.setOutboundConnection(sOut);
		
		MemberLink mlMock = mock(MemberLink.class);
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", mlMock);
		registry.put("member2", ml);
		Deque<String> outboundQueue = new LinkedList<>();
		outboundQueue.add("member1");
		outboundQueue.add("member2");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);
		
		cr.removeConnection("member2");
		
		verify(mlMock, times(0)).getInboundConnection();
		verify(mlMock, times(0)).getOutboundConnection();
		verify(sIn, times(1)).close();
		verify(sOut, times(1)).close();
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 1),
				() -> assertEquals(registry.get("member1"), mlMock));
		
		assertAll("outboundQueue",
				() -> assertEquals(2, outboundQueue.size()),
				() -> assertEquals("member1", outboundQueue.getFirst()),
				() -> assertEquals("member2", outboundQueue.getLast()));
	}
	
	@Test
	public void testNextInbound1() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sIn1 = mock(Socket.class);
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink();
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		Deque<String> inboundQueue  = new LinkedList<>();
		inboundQueue.add("member1");
		inboundQueue.add("member2");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "inboundQueue", inboundQueue);
		
		Member m1 = cr.nextInbound();
		Member m2 = cr.nextInbound();
		Member m3 = cr.nextInbound();
		
		assertAll("members",
				() -> assertEquals("member1", m1.getHostPort()),
				() -> assertEquals(sIn1, m1.getSocket()),
				() -> assertEquals("member2", m2.getHostPort()),
				() -> assertEquals(sIn2, m2.getSocket()),
				() -> assertEquals("member1", m3.getHostPort()),
				() -> assertEquals(sIn1, m3.getSocket()));
	}
	
	@Test
	public void testNextInbound2() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sIn1 = mock(Socket.class);
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink();
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(null);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		Deque<String> inboundQueue  = new LinkedList<>();
		inboundQueue.add("member1");
		inboundQueue.add("member2");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "inboundQueue", inboundQueue);
		
		Member m1 = cr.nextInbound();
		Member m2 = cr.nextInbound();
		Member m3 = cr.nextInbound();
		
		assertAll("members",
				() -> assertEquals("member1", m1.getHostPort()),
				() -> assertEquals(sIn1, m1.getSocket()),
				() -> assertEquals("member1", m2.getHostPort()),
				() -> assertEquals(sIn1, m2.getSocket()),
				() -> assertEquals("member1", m3.getHostPort()),
				() -> assertEquals(sIn1, m3.getSocket()));
	}
	
	@Test
	public void testNextInbound3() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sIn1 = mock(Socket.class);
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink();
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(null);
		
		Socket sIn3 = mock(Socket.class);
		Socket sOut3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink();
		ml3.setInboundConnection(sIn3);
		ml3.setOutboundConnection(sOut3);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		Deque<String> inboundQueue  = new LinkedList<>();
		inboundQueue.add("member1");
		inboundQueue.add("member2");
		inboundQueue.add("member3");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "inboundQueue", inboundQueue);
		
		Member m1 = cr.nextInbound();
		Member m2 = cr.nextInbound();
		Member m3 = cr.nextInbound();
		
		assertAll("members",
				() -> assertEquals("member1", m1.getHostPort()),
				() -> assertEquals(sIn1, m1.getSocket()),
				() -> assertEquals("member3", m2.getHostPort()),
				() -> assertEquals(sIn3, m2.getSocket()),
				() -> assertEquals("member1", m3.getHostPort()),
				() -> assertEquals(sIn1, m3.getSocket()));
	}
	
	@Test
	public void testNextInbound4() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sIn1 = mock(Socket.class);
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink();
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Socket sIn3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink();
		ml3.setInboundConnection(sIn3);
		ml3.setOutboundConnection(null);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		Deque<String> inboundQueue  = new LinkedList<>();
		inboundQueue.add("member1");
		inboundQueue.add("member2");
		inboundQueue.add("member3");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "inboundQueue", inboundQueue);
		
		Member m1 = cr.nextInbound();
		Member m2 = cr.nextInbound();
		Member m3 = cr.nextInbound();
		
		assertAll("members",
				() -> assertEquals("member1", m1.getHostPort()),
				() -> assertEquals(sIn1, m1.getSocket()),
				() -> assertEquals("member2", m2.getHostPort()),
				() -> assertEquals(sIn2, m2.getSocket()),
				() -> assertEquals("member1", m3.getHostPort()),
				() -> assertEquals(sIn1, m3.getSocket()));
	}
	
	@Test
	public void testNextInbound5() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(null);
		ml1.setOutboundConnection(null);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink();
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Socket sIn3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink();
		ml3.setInboundConnection(sIn3);
		ml3.setOutboundConnection(null);
		
		Socket sIn4 = mock(Socket.class);
		MemberLink ml4 = new MemberLink();
		ml4.setInboundConnection(null);
		ml4.setOutboundConnection(sIn4);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		registry.put("member4", ml4);
		Deque<String> inboundQueue  = new LinkedList<>();
		inboundQueue.add("member1");
		inboundQueue.add("member2");
		inboundQueue.add("member3");
		inboundQueue.add("member4");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "inboundQueue", inboundQueue);
		
		Member m1 = cr.nextInbound();
		Member m2 = cr.nextInbound();
		Member m3 = cr.nextInbound();
		Member m4 = cr.nextInbound();
		
		assertAll("members",
				() -> assertEquals("member2", m1.getHostPort()),
				() -> assertEquals(sIn2, m1.getSocket()),
				() -> assertEquals("member2", m2.getHostPort()),
				() -> assertEquals(sIn2, m2.getSocket()),
				() -> assertEquals("member2", m3.getHostPort()),
				() -> assertEquals(sIn2, m3.getSocket()),
				() -> assertEquals("member2", m4.getHostPort()),
				() -> assertEquals(sIn2, m4.getSocket()));
	}
	
	@Test
	public void testNextInbound6() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sIn1 = mock(Socket.class);
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn3 = mock(Socket.class);
		Socket sOut3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink();
		ml3.setInboundConnection(sIn3);
		ml3.setOutboundConnection(sOut3);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member3", ml3);
		Deque<String> inboundQueue  = new LinkedList<>();
		inboundQueue.add("member1");
		inboundQueue.add("member2");
		inboundQueue.add("member3");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "inboundQueue", inboundQueue);
		
		Member m1 = cr.nextInbound();
		Member m2 = cr.nextInbound();
		Member m3 = cr.nextInbound();
		
		assertAll("members",
				() -> assertEquals("member1", m1.getHostPort()),
				() -> assertEquals(sIn1, m1.getSocket()),
				() -> assertEquals("member3", m2.getHostPort()),
				() -> assertEquals(sIn3, m2.getSocket()),
				() -> assertEquals("member1", m3.getHostPort()),
				() -> assertEquals(sIn1, m3.getSocket()));
		
		assertAll("inboundQueue",
				() -> assertEquals(2, inboundQueue.size()),
				() -> assertEquals("member3", inboundQueue.getFirst()),
				() -> assertEquals("member1", inboundQueue.getLast()));
	}
	
	@Test
	public void testNextInbound7() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sIn1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(null);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		Deque<String> inboundQueue = new LinkedList<>();
		inboundQueue.add("member1");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "inboundQueue", inboundQueue);
		
		Member m1 = cr.nextInbound();
		Member m2 = cr.nextInbound();
		Member m3 = cr.nextInbound();
		
		assertAll("members",
				() -> assertEquals(null, m1),
				() -> assertEquals(null, m2),
				() -> assertEquals(null, m3));
	}
	
	@Test
	public void testNextInbound8() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Map<String, MemberLink> registry = new HashMap<>();
		Deque<String> inboundQueue = new LinkedList<>();
		inboundQueue.add("member1");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "inboundQueue", inboundQueue);
		
		Member m1 = cr.nextInbound();
		Member m2 = cr.nextInbound();
		Member m3 = cr.nextInbound();
		
		assertAll("members",
				() -> assertEquals(null, m1),
				() -> assertEquals(null, m2),
				() -> assertEquals(null, m3));
		
		assertAll("inboundQueue",
				() -> assertEquals(0, inboundQueue.size()));
	}
	
	@Test
	public void testNextInbound9() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Map<String, MemberLink> registry = new HashMap<>();
		Deque<String> inboundQueue = new LinkedList<>();
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "inboundQueue", inboundQueue);
		
		Member m1 = cr.nextInbound();
		Member m2 = cr.nextInbound();
		Member m3 = cr.nextInbound();
		
		assertAll("members",
				() -> assertEquals(null, m1),
				() -> assertEquals(null, m2),
				() -> assertEquals(null, m3));
		
		assertAll("inboundQueue",
				() -> assertEquals(0, inboundQueue.size()));
	}
	
	@Test
	public void testNextOutbound1() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sIn1 = mock(Socket.class);
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink();
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		Deque<String> outboundQueue  = new LinkedList<>();
		outboundQueue.add("member1");
		outboundQueue.add("member2");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);
		
		Member m1 = cr.nextOutbound();
		Member m2 = cr.nextOutbound();
		Member m3 = cr.nextOutbound();
		
		assertAll("members",
				() -> assertEquals("member1", m1.getHostPort()),
				() -> assertEquals(sOut1, m1.getSocket()),
				() -> assertEquals("member2", m2.getHostPort()),
				() -> assertEquals(sOut2, m2.getSocket()),
				() -> assertEquals("member1", m3.getHostPort()),
				() -> assertEquals(sOut1, m3.getSocket()));
	}
	
	@Test
	public void testNextOutbound2() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sIn1 = mock(Socket.class);
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink();
		ml2.setInboundConnection(null);
		ml2.setOutboundConnection(sOut2);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		Deque<String> outboundQueue  = new LinkedList<>();
		outboundQueue.add("member1");
		outboundQueue.add("member2");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);
		
		Member m1 = cr.nextOutbound();
		Member m2 = cr.nextOutbound();
		Member m3 = cr.nextOutbound();
		
		assertAll("members",
				() -> assertEquals("member1", m1.getHostPort()),
				() -> assertEquals(sOut1, m1.getSocket()),
				() -> assertEquals("member1", m2.getHostPort()),
				() -> assertEquals(sOut1, m2.getSocket()),
				() -> assertEquals("member1", m3.getHostPort()),
				() -> assertEquals(sOut1, m3.getSocket()));
	}
	
	@Test
	public void testNextOutbound3() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sIn1 = mock(Socket.class);
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink();
		ml2.setInboundConnection(null);
		ml2.setOutboundConnection(sOut2);
		
		Socket sIn3 = mock(Socket.class);
		Socket sOut3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink();
		ml3.setInboundConnection(sIn3);
		ml3.setOutboundConnection(sOut3);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		Deque<String> outboundQueue  = new LinkedList<>();
		outboundQueue.add("member1");
		outboundQueue.add("member2");
		outboundQueue.add("member3");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);
		
		Member m1 = cr.nextOutbound();
		Member m2 = cr.nextOutbound();
		Member m3 = cr.nextOutbound();
		
		assertAll("members",
				() -> assertEquals("member1", m1.getHostPort()),
				() -> assertEquals(sOut1, m1.getSocket()),
				() -> assertEquals("member3", m2.getHostPort()),
				() -> assertEquals(sOut3, m2.getSocket()),
				() -> assertEquals("member1", m3.getHostPort()),
				() -> assertEquals(sOut1, m3.getSocket()));
	}
	
	@Test
	public void testNextOutbound4() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sIn1 = mock(Socket.class);
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink();
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Socket sOut3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink();
		ml3.setInboundConnection(null);
		ml3.setOutboundConnection(sOut3);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		Deque<String> outboundQueue  = new LinkedList<>();
		outboundQueue.add("member1");
		outboundQueue.add("member2");
		outboundQueue.add("member3");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);
		
		Member m1 = cr.nextOutbound();
		Member m2 = cr.nextOutbound();
		Member m3 = cr.nextOutbound();
		
		assertAll("members",
				() -> assertEquals("member1", m1.getHostPort()),
				() -> assertEquals(sOut1, m1.getSocket()),
				() -> assertEquals("member2", m2.getHostPort()),
				() -> assertEquals(sOut2, m2.getSocket()),
				() -> assertEquals("member1", m3.getHostPort()),
				() -> assertEquals(sOut1, m3.getSocket()));
	}
	
	@Test
	public void testNextOutbound5() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(null);
		ml1.setOutboundConnection(null);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink();
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Socket sOut3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink();
		ml3.setInboundConnection(null);
		ml3.setOutboundConnection(sOut3);
		
		Socket sOut4 = mock(Socket.class);
		MemberLink ml4 = new MemberLink();
		ml4.setInboundConnection(null);
		ml4.setOutboundConnection(sOut4);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		registry.put("member4", ml4);
		Deque<String> outboundQueue  = new LinkedList<>();
		outboundQueue.add("member1");
		outboundQueue.add("member2");
		outboundQueue.add("member3");
		outboundQueue.add("member4");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);
		
		Member m1 = cr.nextOutbound();
		Member m2 = cr.nextOutbound();
		Member m3 = cr.nextOutbound();
		Member m4 = cr.nextOutbound();
		
		assertAll("members",
				() -> assertEquals("member2", m1.getHostPort()),
				() -> assertEquals(sOut2, m1.getSocket()),
				() -> assertEquals("member2", m2.getHostPort()),
				() -> assertEquals(sOut2, m2.getSocket()),
				() -> assertEquals("member2", m3.getHostPort()),
				() -> assertEquals(sOut2, m3.getSocket()),
				() -> assertEquals("member2", m4.getHostPort()),
				() -> assertEquals(sOut2, m4.getSocket()));
	}
	
	@Test
	public void testNextOutbound6() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();

		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(null);
		ml1.setOutboundConnection(sOut1);

		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		Deque<String> outboundQueue = new LinkedList<>();
		outboundQueue.add("member1");

		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);

		Member m1 = cr.nextOutbound();
		Member m2 = cr.nextOutbound();
		Member m3 = cr.nextOutbound();
		
		assertAll("members",
				() -> assertEquals(null, m1),
				() -> assertEquals(null, m2),
				() -> assertEquals(null, m3));
	}
	
	@Test
	public void testNextOutbound7() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(null);
		ml1.setOutboundConnection(sOut1);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		Deque<String> outboundQueue = new LinkedList<>();
		outboundQueue.add("member1");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);
		
		Member m1 = cr.nextOutbound();
		Member m2 = cr.nextOutbound();
		Member m3 = cr.nextOutbound();
		
		assertAll("members",
				() -> assertEquals(null, m1),
				() -> assertEquals(null, m2),
				() -> assertEquals(null, m3));
	}
	
	@Test
	public void testNextOutbound8() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Map<String, MemberLink> registry = new HashMap<>();
		Deque<String> outboundQueue = new LinkedList<>();
		outboundQueue.add("member1");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);
		
		Member m1 = cr.nextOutbound();
		Member m2 = cr.nextOutbound();
		Member m3 = cr.nextOutbound();
		
		assertAll("members",
				() -> assertEquals(null, m1),
				() -> assertEquals(null, m2),
				() -> assertEquals(null, m3));
		
		assertAll("inboundQueue",
				() -> assertEquals(0, outboundQueue.size()));
	}
	
	@Test
	public void testNextOutbound9() throws NoSuchFieldException,
 	   									  SecurityException, 
 	   									  IllegalArgumentException,
 	   									  IllegalAccessException, 
 	   									  IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Map<String, MemberLink> registry = new HashMap<>();
		Deque<String> outboundQueue = new LinkedList<>();
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "outboundQueue", outboundQueue);
		
		Member m1 = cr.nextInbound();
		Member m2 = cr.nextInbound();
		Member m3 = cr.nextInbound();
		
		assertAll("members",
				() -> assertEquals(null, m1),
				() -> assertEquals(null, m2),
				() -> assertEquals(null, m3));
		
		assertAll("inboundQueue",
				() -> assertEquals(0, outboundQueue.size()));
	}
	
	@Test
	public void testExistOutbound1() throws NoSuchFieldException,
										   SecurityException, 
										   IllegalArgumentException,
										   IllegalAccessException, 
										   IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(null);
		ml1.setOutboundConnection(sOut1);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		
		setPrivateFieldValue(cr, "registry", registry);
		
		assertTrue(cr.existOutbound("member1"));
	}
	
	@Test
	public void testExistOutbound2() throws NoSuchFieldException,
										   SecurityException, 
										   IllegalArgumentException,
										   IllegalAccessException, 
										   IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Socket sOut1 = mock(Socket.class);
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(null);
		ml1.setOutboundConnection(sOut1);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		
		setPrivateFieldValue(cr, "registry", registry);
		
		assertFalse(cr.existOutbound("member2"));
	}
	
	@Test
	public void testExistOutbound3() throws NoSuchFieldException,
										   SecurityException, 
										   IllegalArgumentException,
										   IllegalAccessException, 
										   IOException
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		MemberLink ml1 = new MemberLink();
		ml1.setInboundConnection(null);
		ml1.setOutboundConnection(null);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		
		setPrivateFieldValue(cr, "registry", registry);
		
		assertFalse(cr.existOutbound("member1"));
	}
	
	private void setPrivateFieldValue(Object o, String fName, Object v) 
			throws IllegalArgumentException, 
				   IllegalAccessException, 
				   NoSuchFieldException, 
				   SecurityException 
	{
		Field f = o.getClass().getDeclaredField(fName);
		f.setAccessible(true);
		f.set(o, v);
		f.setAccessible(true);
	}
	
	private Object getPrivateFieldValue(Object o, String fName) 
			throws IllegalArgumentException, 
			   	   IllegalAccessException, 
			   	   NoSuchFieldException, 
			   	   SecurityException 
	{
		Field f = o.getClass().getDeclaredField(fName);
		f.setAccessible(true);
		Object v = f.get(o);
		f.setAccessible(true);
		return v;
	}
	
	private static class MockRegistry extends HashMap<String, MemberLink> {
		private static final long serialVersionUID = 1L;
	}
	
	private static class MockRegistryIndex extends HashMap<String, MemberLink> {
		private static final long serialVersionUID = 1L;
	}
}
