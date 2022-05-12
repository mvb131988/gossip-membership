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
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		Deque<Integer> freeIndexes = new LinkedList<>();
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "freeIndexes", freeIndexes);
		setPrivateFieldValue(cr, "maxIndex", 1);
		
		Socket sIn = mock(Socket.class);
		cr.registerInbound("member1", sIn);
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 1),
				() -> assertEquals(registry.get("member1").getInboundConnection(), sIn),
				() -> assertEquals(registry.get("member1").getOutboundConnection(), null));
		
		assertAll("registryIndex",
				() -> assertEquals(registryIndex.size(), 1),
				() -> assertEquals(registryIndex.get(1), "member1"));
		
		assertAll("freeIndexes",
				() -> assertEquals(freeIndexes.size(), 0));
		
		int maxSize = (Integer) getPrivateFieldValue(cr, "maxIndex");
		assertEquals(1, maxSize);
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
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		Deque<Integer> freeIndexes = new LinkedList<>();
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "freeIndexes", freeIndexes);
		setPrivateFieldValue(cr, "maxIndex", 1);
		
		Socket sIn = mock(Socket.class);
		cr.registerInbound("member2", sIn);
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 2),
				() -> assertEquals(registry.get("member1").getInboundConnection(), null),
				() -> assertEquals(registry.get("member1").getOutboundConnection(), null),
				() -> assertEquals(registry.get("member2").getInboundConnection(), sIn),
				() -> assertEquals(registry.get("member2").getOutboundConnection(), null));
		
		assertAll("registryIndex",
				() -> assertEquals(registryIndex.size(), 2),
				() -> assertEquals(registryIndex.get(1), "member1"),
				() -> assertEquals(registryIndex.get(2), "member2"));
		
		assertAll("freeIndexes",
				() -> assertEquals(freeIndexes.size(), 0));
		
		int maxSize = (Integer) getPrivateFieldValue(cr, "maxIndex");
		assertEquals(2, maxSize);
	}
	
	@Test
	public void testRegisterInbound3() throws NoSuchFieldException,
											  SecurityException, 
											  IllegalArgumentException,
											  IllegalAccessException 
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		MemberLink ml = new MemberLink();
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		// assuming member2 failed and then reconnecting
		Deque<Integer> freeIndexes = new LinkedList<>();
		freeIndexes.addFirst(2);
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "freeIndexes", freeIndexes);
		setPrivateFieldValue(cr, "maxIndex", 2);
		
		Socket sIn = mock(Socket.class);
		cr.registerInbound("member2", sIn);
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 2),
				() -> assertEquals(registry.get("member1").getInboundConnection(), null),
				() -> assertEquals(registry.get("member1").getOutboundConnection(), null),
				() -> assertEquals(registry.get("member2").getInboundConnection(), sIn),
				() -> assertEquals(registry.get("member2").getOutboundConnection(), null));
		
		assertAll("registryIndex",
				() -> assertEquals(registryIndex.size(), 2),
				() -> assertEquals(registryIndex.get(1), "member1"),
				() -> assertEquals(registryIndex.get(2), "member2"));
		
		assertAll("freeIndexes",
				() -> assertEquals(freeIndexes.size(), 0));
		
		int maxSize = (Integer) getPrivateFieldValue(cr, "maxIndex");
		assertEquals(2, maxSize);
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
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		Deque<Integer> freeIndexes = new LinkedList<>();
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "freeIndexes", freeIndexes);
		setPrivateFieldValue(cr, "maxIndex", 1);
		
		Socket sOut = mock(Socket.class);
		cr.registerOutbound("member1", sOut);
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 1),
				() -> assertEquals(registry.get("member1").getInboundConnection(), null),
				() -> assertEquals(registry.get("member1").getOutboundConnection(), sOut));
		
		assertAll("registryIndex",
				() -> assertEquals(registryIndex.size(), 1),
				() -> assertEquals(registryIndex.get(1), "member1"));
		
		assertAll("freeIndexes",
				() -> assertEquals(freeIndexes.size(), 0));
		
		int maxSize = (Integer) getPrivateFieldValue(cr, "maxIndex");
		assertEquals(1, maxSize);
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
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		Deque<Integer> freeIndexes = new LinkedList<>();
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "freeIndexes", freeIndexes);
		setPrivateFieldValue(cr, "maxIndex", 1);
		
		Socket sOut = mock(Socket.class);
		cr.registerOutbound("member2", sOut);
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 2),
				() -> assertEquals(registry.get("member1").getInboundConnection(), null),
				() -> assertEquals(registry.get("member1").getOutboundConnection(), null),
				() -> assertEquals(registry.get("member2").getInboundConnection(), null),
				() -> assertEquals(registry.get("member2").getOutboundConnection(), sOut));
		
		assertAll("registryIndex",
				() -> assertEquals(registryIndex.size(), 2),
				() -> assertEquals(registryIndex.get(1), "member1"),
				() -> assertEquals(registryIndex.get(2), "member2"));
		
		assertAll("freeIndexes",
				() -> assertEquals(freeIndexes.size(), 0));
		
		int maxSize = (Integer) getPrivateFieldValue(cr, "maxIndex");
		assertEquals(2, maxSize);
	}
	
	@Test
	public void testRegisterOutbound3() throws NoSuchFieldException,
											   SecurityException, 
											   IllegalArgumentException,
											   IllegalAccessException 
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		MemberLink ml = new MemberLink();
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		// assuming member2 failed and then reconnecting
		Deque<Integer> freeIndexes = new LinkedList<>();
		freeIndexes.addFirst(2);
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "freeIndexes", freeIndexes);
		setPrivateFieldValue(cr, "maxIndex", 2);
		
		Socket sOut = mock(Socket.class);
		cr.registerOutbound("member2", sOut);
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 2),
				() -> assertEquals(registry.get("member1").getInboundConnection(), null),
				() -> assertEquals(registry.get("member1").getOutboundConnection(), null),
				() -> assertEquals(registry.get("member2").getInboundConnection(), null),
				() -> assertEquals(registry.get("member2").getOutboundConnection(), sOut));
		
		assertAll("registryIndex",
				() -> assertEquals(registryIndex.size(), 2),
				() -> assertEquals(registryIndex.get(1), "member1"),
				() -> assertEquals(registryIndex.get(2), "member2"));
		
		assertAll("freeIndexes",
				() -> assertEquals(freeIndexes.size(), 0));
		
		int maxSize = (Integer) getPrivateFieldValue(cr, "maxIndex");
		assertEquals(2, maxSize);
	}
	
	@Test
	public void testRemoveConnection1() throws NoSuchFieldException,
	 									   	   SecurityException, 
	 									   	   IllegalArgumentException,
	 									   	   IllegalAccessException 
	{
		ConnectionRegistry cr = new ConnectionRegistry();
		
		Map<String, MemberLink> registry = mock(MockRegistry.class);
		Map<String, MemberLink> registryIndex = mock(MockRegistryIndex.class);
		Deque<Integer> freeIndexes = new LinkedList<>();
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "freeIndexes", freeIndexes);
		setPrivateFieldValue(cr, "maxIndex", 0);
		
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
		MemberLink ml = new MemberLink(2);
		ml.setInboundConnection(sIn);
		ml.setOutboundConnection(sOut);
		
		MemberLink mlMock = mock(MemberLink.class);
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", mlMock);
		registry.put("member2", ml);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		registryIndex.put(2, "member2");
		Deque<Integer> freeIndexes = new LinkedList<>();
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "freeIndexes", freeIndexes);
		setPrivateFieldValue(cr, "maxIndex", 2);
		
		cr.removeConnection("member2");
		
		verify(mlMock, times(0)).getInboundConnection();
		verify(mlMock, times(0)).getOutboundConnection();
		verify(sIn, times(1)).close();
		verify(sOut, times(1)).close();
		
		assertAll("registry",
				() -> assertEquals(registry.size(), 1),
				() -> assertEquals(registry.get("member1"), mlMock));
		
		assertAll("registryIndex",
				() -> assertEquals(registryIndex.size(), 1),
				() -> assertEquals(registryIndex.get(1), "member1"));
		
		assertAll("freeIndexes",
				() -> assertEquals(freeIndexes.size(), 1),
				() -> assertEquals(freeIndexes.getFirst(), 2));
		
		int maxSize = (Integer) getPrivateFieldValue(cr, "maxIndex");
		assertEquals(2, maxSize);
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
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink(2);
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		registryIndex.put(2, "member2");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 2);
		
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
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink(2);
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(null);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		registryIndex.put(2, "member2");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 2);
		
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
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink(2);
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(null);
		
		Socket sIn3 = mock(Socket.class);
		Socket sOut3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink(3);
		ml3.setInboundConnection(sIn3);
		ml3.setOutboundConnection(sOut3);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		registryIndex.put(2, "member2");
		registryIndex.put(3, "member3");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 3);
		
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
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink(2);
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Socket sIn3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink(3);
		ml3.setInboundConnection(sIn3);
		ml3.setOutboundConnection(null);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		registryIndex.put(2, "member2");
		registryIndex.put(3, "member3");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 3);
		
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
		
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(null);
		ml1.setOutboundConnection(null);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink(2);
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Socket sIn3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink(3);
		ml3.setInboundConnection(sIn3);
		ml3.setOutboundConnection(null);
		
		Socket sIn4 = mock(Socket.class);
		MemberLink ml4 = new MemberLink(4);
		ml4.setInboundConnection(null);
		ml4.setOutboundConnection(sIn4);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		registry.put("member4", ml4);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		registryIndex.put(2, "member2");
		registryIndex.put(3, "member3");
		registryIndex.put(4, "member4");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 3);
		
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
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(null);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 1);
		
		Member m1 = cr.nextInbound();
		Member m2 = cr.nextInbound();
		Member m3 = cr.nextInbound();
		
		assertAll("members",
				() -> assertEquals(null, m1),
				() -> assertEquals(null, m2),
				() -> assertEquals(null, m3));
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
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink(2);
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		registryIndex.put(2, "member2");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 2);
		
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
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink(2);
		ml2.setInboundConnection(null);
		ml2.setOutboundConnection(sOut2);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		registryIndex.put(2, "member2");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 2);
		
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
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink(2);
		ml2.setInboundConnection(null);
		ml2.setOutboundConnection(sOut2);
		
		Socket sIn3 = mock(Socket.class);
		Socket sOut3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink(3);
		ml3.setInboundConnection(sIn3);
		ml3.setOutboundConnection(sOut3);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		registryIndex.put(2, "member2");
		registryIndex.put(3, "member3");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 3);
		
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
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(sIn1);
		ml1.setOutboundConnection(sOut1);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink(2);
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Socket sOut3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink(3);
		ml3.setInboundConnection(null);
		ml3.setOutboundConnection(sOut3);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		registryIndex.put(2, "member2");
		registryIndex.put(3, "member3");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 3);
		
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
		
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(null);
		ml1.setOutboundConnection(null);
		
		Socket sIn2 = mock(Socket.class);
		Socket sOut2 = mock(Socket.class);
		MemberLink ml2 = new MemberLink(2);
		ml2.setInboundConnection(sIn2);
		ml2.setOutboundConnection(sOut2);
		
		Socket sOut3 = mock(Socket.class);
		MemberLink ml3 = new MemberLink(3);
		ml3.setInboundConnection(null);
		ml3.setOutboundConnection(sOut3);
		
		Socket sOut4 = mock(Socket.class);
		MemberLink ml4 = new MemberLink(4);
		ml4.setInboundConnection(null);
		ml4.setOutboundConnection(sOut4);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		registry.put("member2", ml2);
		registry.put("member3", ml3);
		registry.put("member4", ml4);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		registryIndex.put(2, "member2");
		registryIndex.put(3, "member3");
		registryIndex.put(4, "member4");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 3);
		
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
		MemberLink ml1 = new MemberLink(1);
		ml1.setInboundConnection(null);
		ml1.setOutboundConnection(sOut1);
		
		Map<String, MemberLink> registry = new HashMap<>();
		registry.put("member1", ml1);
		Map<Integer, String> registryIndex = new HashMap<>();
		registryIndex.put(1, "member1");
		
		setPrivateFieldValue(cr, "registry", registry);
		setPrivateFieldValue(cr, "registryIndex", registryIndex);
		setPrivateFieldValue(cr, "maxIndex", 1);
		
		Member m1 = cr.nextOutbound();
		Member m2 = cr.nextOutbound();
		Member m3 = cr.nextOutbound();
		
		assertAll("members",
				() -> assertEquals(null, m1),
				() -> assertEquals(null, m2),
				() -> assertEquals(null, m3));
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
		MemberLink ml1 = new MemberLink(1);
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
		MemberLink ml1 = new MemberLink(1);
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
		
		MemberLink ml1 = new MemberLink(1);
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
