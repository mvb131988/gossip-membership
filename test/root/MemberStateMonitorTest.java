package root;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class MemberStateMonitorTest {

	@Test
	public void testUpdateMemberStateAndGetVectorClockTable1() throws NoSuchFieldException,
							  										 SecurityException, 
							  										 IllegalArgumentException,
							  										 IllegalAccessException 
	{
		MemberStateTable mst = new MemberStateTable();
		mst.add(new MemberState("member1", 1, 0, "ACTIVE"));
		mst.add(new MemberState("member2", 5, 0, "ACTIVE"));
		mst.add(new MemberState("member3", 7, 0, "ACTIVE"));
		
		MemberStateMonitor msm = new MemberStateMonitor();
		
		Field f1 = msm.getClass().getDeclaredField("memberId");
		Field f2 = msm.getClass().getDeclaredField("table");
		
		f1.setAccessible(true);
		f2.setAccessible(true);
		f1.set(msm, "member1");
		f2.set(msm, mst);
		f1.setAccessible(false);
		f2.setAccessible(false);
		
		long timestamp = System.currentTimeMillis();
		VectorClockTable vct = msm.updateMemberStateAndGetVectorClockTable(timestamp);
		
		assertAll("vct",
				() -> assertEquals(vct.getTable().get(0).getMemberId(), "member1"),
				() -> assertEquals(vct.getTable().get(0).getLamportTimestamp(), 2),
				() -> assertEquals(vct.getTable().get(1).getMemberId(), "member2"),
				() -> assertEquals(vct.getTable().get(1).getLamportTimestamp(), 5),
				() -> assertEquals(vct.getTable().get(2).getMemberId(), "member3"),
				() -> assertEquals(vct.getTable().get(2).getLamportTimestamp(), 7));
		
		assertAll("mst",
				() -> assertEquals(mst.getTable().get(0).getMemberId(), "member1"),
				() -> assertEquals(mst.getTable().get(0).getLamportTimestamp(), 2),
				() -> assertEquals(mst.getTable().get(0).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(0).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(1).getMemberId(), "member2"),
				() -> assertEquals(mst.getTable().get(1).getLamportTimestamp(), 5),
				() -> assertEquals(mst.getTable().get(1).getLocalTimestamp(), 0),
				() -> assertEquals(mst.getTable().get(1).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(2).getMemberId(), "member3"),
				() -> assertEquals(mst.getTable().get(2).getLamportTimestamp(), 7),
				() -> assertEquals(mst.getTable().get(2).getLocalTimestamp(), 0),
				() -> assertEquals(mst.getTable().get(2).getState(), "ACTIVE"));
	}
	
	@Test
	public void updateMembersState1() throws NoSuchFieldException, 
											SecurityException, 
											IllegalArgumentException, 
											IllegalAccessException 
	{
		MemberStateTable mst = new MemberStateTable();
		mst.add(new MemberState("member1", 10, 0, "ACTIVE"));
		mst.add(new MemberState("member2", 5, 0, "ACTIVE"));
		mst.add(new MemberState("member3", 7, 0, "ACTIVE"));
		mst.addSeenBy("member1");
		
		MemberStateMonitor msm = new MemberStateMonitor();
		
		Field f1 = msm.getClass().getDeclaredField("memberId");
		Field f2 = msm.getClass().getDeclaredField("table");
		
		f1.setAccessible(true);
		f2.setAccessible(true);
		f1.set(msm, "member1");
		f2.set(msm, mst);
		f1.setAccessible(false);
		f2.setAccessible(false);
		
		long timestamp = System.currentTimeMillis();
		
		VectorClockTable vct = new VectorClockTable();
		vct.add(new VectorClock("member1", 1));
		vct.add(new VectorClock("member2", 13));
		vct.add(new VectorClock("member3", 7));
		vct.addSeenBy("member2");
		
		String senderMember = "member2";

		msm.updateMembersState(vct, senderMember, timestamp);
		
		assertAll("mst",
				() -> assertEquals(mst.getTable().get(0).getMemberId(), "member1"),
				() -> assertEquals(mst.getTable().get(0).getLamportTimestamp(), 11),
				() -> assertEquals(mst.getTable().get(0).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(0).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(1).getMemberId(), "member2"),
				() -> assertEquals(mst.getTable().get(1).getLamportTimestamp(), 13),
				() -> assertEquals(mst.getTable().get(1).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(1).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(2).getMemberId(), "member3"),
				() -> assertEquals(mst.getTable().get(2).getLamportTimestamp(), 7),
				() -> assertEquals(mst.getTable().get(2).getLocalTimestamp(), 0),
				() -> assertEquals(mst.getTable().get(2).getState(), "ACTIVE"));
		
		Set<String> targetSeenBy = Set.of("member1", "member2");
		
		assertTrue(mst.getSeenByMembers().containsAll(targetSeenBy));
	}
	
	@Test
	public void updateMembersState2() throws NoSuchFieldException, 
											 SecurityException, 
											 IllegalArgumentException, 
											 IllegalAccessException 
	{
		MemberStateTable mst = new MemberStateTable();
		mst.add(new MemberState("member1", 10, 0, "ACTIVE"));
		mst.add(new MemberState("member2", 5, 0, "ACTIVE"));
		mst.addSeenBy("member1");
		
		MemberStateMonitor msm = new MemberStateMonitor();
		
		Field f1 = msm.getClass().getDeclaredField("memberId");
		Field f2 = msm.getClass().getDeclaredField("table");
		
		f1.setAccessible(true);
		f2.setAccessible(true);
		f1.set(msm, "member1");
		f2.set(msm, mst);
		f1.setAccessible(false);
		f2.setAccessible(false);
		
		long timestamp = System.currentTimeMillis();
		
		VectorClockTable vct = new VectorClockTable();
		vct.add(new VectorClock("member1", 1));
		vct.add(new VectorClock("member2", 13));
		vct.add(new VectorClock("member3", 7));
		vct.addSeenBy("member2");
		
		String senderMember = "member2";

		msm.updateMembersState(vct, senderMember, timestamp);
		
		assertAll("mst",
				() -> assertEquals(mst.getTable().get(0).getMemberId(), "member1"),
				() -> assertEquals(mst.getTable().get(0).getLamportTimestamp(), 11),
				() -> assertEquals(mst.getTable().get(0).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(0).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(1).getMemberId(), "member2"),
				() -> assertEquals(mst.getTable().get(1).getLamportTimestamp(), 13),
				() -> assertEquals(mst.getTable().get(1).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(1).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(2).getMemberId(), "member3"),
				() -> assertEquals(mst.getTable().get(2).getLamportTimestamp(), 7),
				() -> assertEquals(mst.getTable().get(2).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(2).getState(), "ACTIVE"));
		
		Set<String> targetSeenBy = Set.of("member1");
		
		assertTrue(mst.getSeenByMembers().containsAll(targetSeenBy));
		assertTrue(targetSeenBy.containsAll(mst.getSeenByMembers()));
	}
	
	@Test
	public void updateMembersState3() throws NoSuchFieldException, 
											 SecurityException, 
											 IllegalArgumentException, 
											 IllegalAccessException 
	{
		MemberStateTable mst = new MemberStateTable();
		mst.add(new MemberState("member1", 10, 0, "ACTIVE"));
		mst.add(new MemberState("member2", 5, 0, "ACTIVE"));
		mst.addSeenBy("member1");
		
		MemberStateMonitor msm = new MemberStateMonitor();
		
		Field f1 = msm.getClass().getDeclaredField("memberId");
		Field f2 = msm.getClass().getDeclaredField("table");
		
		f1.setAccessible(true);
		f2.setAccessible(true);
		f1.set(msm, "member1");
		f2.set(msm, mst);
		f1.setAccessible(false);
		f2.setAccessible(false);
		
		long timestamp = System.currentTimeMillis();
		
		VectorClockTable vct = new VectorClockTable();
		vct.add(new VectorClock("member2", 13));
		vct.addSeenBy("member2");
		
		String senderMember = "member2";

		msm.updateMembersState(vct, senderMember, timestamp);
		
		assertAll("mst",
				() -> assertEquals(mst.getTable().get(0).getMemberId(), "member1"),
				() -> assertEquals(mst.getTable().get(0).getLamportTimestamp(), 11),
				() -> assertEquals(mst.getTable().get(0).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(0).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(1).getMemberId(), "member2"),
				() -> assertEquals(mst.getTable().get(1).getLamportTimestamp(), 13),
				() -> assertEquals(mst.getTable().get(1).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(1).getState(), "ACTIVE"));
		
		Set<String> targetSeenBy = Set.of("member1");
		
		assertTrue(mst.getSeenByMembers().containsAll(targetSeenBy));
		assertTrue(targetSeenBy.containsAll(mst.getSeenByMembers()));
	}
	
	@Test
	public void updateMembersState4() throws NoSuchFieldException, 
											 SecurityException, 
											 IllegalArgumentException, 
											 IllegalAccessException 
	{
		MemberStateTable mst = new MemberStateTable();
		mst.add(new MemberState("member1", 10, 0, "ACTIVE"));
		mst.add(new MemberState("member2", 5, 0, "ACTIVE"));
		mst.add(new MemberState("member3", 5, 0, "INACTIVE"));
		mst.addSeenBy("member1");
		
		MemberStateMonitor msm = new MemberStateMonitor();
		
		Field f1 = msm.getClass().getDeclaredField("memberId");
		Field f2 = msm.getClass().getDeclaredField("table");
		
		f1.setAccessible(true);
		f2.setAccessible(true);
		f1.set(msm, "member1");
		f2.set(msm, mst);
		f1.setAccessible(false);
		f2.setAccessible(false);
		
		long timestamp = System.currentTimeMillis();
		
		VectorClockTable vct = new VectorClockTable();
		vct.add(new VectorClock("member1", 9));
		vct.add(new VectorClock("member2", 13));
		vct.addSeenBy("member2");
		
		String senderMember = "member2";

		msm.updateMembersState(vct, senderMember, timestamp);
		
		assertAll("mst",
				() -> assertEquals(mst.getTable().get(0).getMemberId(), "member1"),
				() -> assertEquals(mst.getTable().get(0).getLamportTimestamp(), 11),
				() -> assertEquals(mst.getTable().get(0).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(0).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(1).getMemberId(), "member2"),
				() -> assertEquals(mst.getTable().get(1).getLamportTimestamp(), 13),
				() -> assertEquals(mst.getTable().get(1).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(1).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(2).getMemberId(), "member3"),
				() -> assertEquals(mst.getTable().get(2).getLamportTimestamp(), 5),
				() -> assertEquals(mst.getTable().get(2).getLocalTimestamp(), 0),
				() -> assertEquals(mst.getTable().get(2).getState(), "INACTIVE"));
		
		Set<String> targetSeenBy = Set.of("member1", "member2");
		
		assertTrue(mst.getSeenByMembers().containsAll(targetSeenBy));
		assertTrue(targetSeenBy.containsAll(mst.getSeenByMembers()));
	}
	
	@Test
	public void updateMembersState5() throws NoSuchFieldException, 
											 SecurityException, 
											 IllegalArgumentException, 
											 IllegalAccessException 
	{
		MemberStateTable mst = new MemberStateTable();
		mst.add(new MemberState("member1", 15, 0, "INACTIVE"));
		mst.add(new MemberState("member2", 11, 0, "ACTIVE"));
		mst.add(new MemberState("member3", 17, 0, "ACTIVE"));
		mst.addSeenBy("member2");
		mst.addSeenBy("member3");
		
		MemberStateMonitor msm = new MemberStateMonitor();
		
		Field f1 = msm.getClass().getDeclaredField("memberId");
		Field f2 = msm.getClass().getDeclaredField("table");
		
		f1.setAccessible(true);
		f2.setAccessible(true);
		f1.set(msm, "member2");
		f2.set(msm, mst);
		f1.setAccessible(false);
		f2.setAccessible(false);
		
		long timestamp = System.currentTimeMillis();
		
		VectorClockTable vct = new VectorClockTable();
		vct.add(new VectorClock("member1", 3));
		vct.add(new VectorClock("member2", 23));
		vct.add(new VectorClock("member3", 20));
		vct.addSeenBy("member2");
		
		String senderMember = "member1";

		msm.updateMembersState(vct, senderMember, timestamp);
		
		assertAll("mst",
				() -> assertEquals(mst.getTable().get(0).getMemberId(), "member1"),
				() -> assertEquals(mst.getTable().get(0).getLamportTimestamp(), 15),
				() -> assertEquals(mst.getTable().get(0).getLocalTimestamp(), 0),
				() -> assertEquals(mst.getTable().get(0).getState(), "INACTIVE"),
				() -> assertEquals(mst.getTable().get(1).getMemberId(), "member2"),
				() -> assertEquals(mst.getTable().get(1).getLamportTimestamp(), 23),
				() -> assertEquals(mst.getTable().get(1).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(1).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(2).getMemberId(), "member3"),
				() -> assertEquals(mst.getTable().get(2).getLamportTimestamp(), 20),
				() -> assertEquals(mst.getTable().get(2).getLocalTimestamp(), timestamp),
				() -> assertEquals(mst.getTable().get(2).getState(), "ACTIVE"));
		
		Set<String> targetSeenBy = Set.of("member2");
		
		assertTrue(mst.getSeenByMembers().containsAll(targetSeenBy));
		assertTrue(targetSeenBy.containsAll(mst.getSeenByMembers()));
	}
	
	@Test
	public void testInactivateMember1() throws NoSuchFieldException, 
											  SecurityException, 
											  IllegalArgumentException, 
											  IllegalAccessException 
	{
		long timestamp = System.currentTimeMillis();
		long timeout = 20_000;
		
		MemberStateTable mst = new MemberStateTable();
		mst.add(new MemberState("member1", 10, timestamp - 5_000, "ACTIVE"));
		mst.add(new MemberState("member2", 5, timestamp - 25_000, "ACTIVE"));
		mst.add(new MemberState("member3", 7, timestamp - 60_000, "INACTIVE"));
		mst.addSeenBy("member1");
		mst.addSeenBy("member2");
		
		MemberStateMonitor msm = new MemberStateMonitor();
		
		Field f1 = msm.getClass().getDeclaredField("memberId");
		Field f2 = msm.getClass().getDeclaredField("table");
		
		f1.setAccessible(true);
		f2.setAccessible(true);
		f1.set(msm, "member1");
		f2.set(msm, mst);
		f1.setAccessible(false);
		f2.setAccessible(false);
		
		msm.inactivateMember(timestamp, timeout);
		
		assertAll("mst",
				() -> assertEquals(mst.getTable().get(0).getMemberId(), "member1"),
				() -> assertEquals(mst.getTable().get(0).getLamportTimestamp(), 10),
				() -> assertEquals(mst.getTable().get(0).getLocalTimestamp(), timestamp - 5_000),
				() -> assertEquals(mst.getTable().get(0).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(1).getMemberId(), "member2"),
				() -> assertEquals(mst.getTable().get(1).getLamportTimestamp(), 5),
				() -> assertEquals(mst.getTable().get(1).getLocalTimestamp(), timestamp - 25_000),
				() -> assertEquals(mst.getTable().get(1).getState(), "INACTIVE"),
				() -> assertEquals(mst.getTable().get(2).getMemberId(), "member3"),
				() -> assertEquals(mst.getTable().get(2).getLamportTimestamp(), 7),
				() -> assertEquals(mst.getTable().get(2).getLocalTimestamp(), timestamp - 60_000),
				() -> assertEquals(mst.getTable().get(2).getState(), "INACTIVE"));
		
		Set<String> targetSeenBy = Set.of("member1");
		
		assertTrue(mst.getSeenByMembers().containsAll(targetSeenBy));
		assertTrue(targetSeenBy.containsAll(mst.getSeenByMembers()));
	}
	
	@Test
	public void testInactivateMember2() throws NoSuchFieldException, 
											  SecurityException, 
											  IllegalArgumentException, 
											  IllegalAccessException 
	{
		long timestamp = System.currentTimeMillis();
		long timeout = 20_000;
		
		MemberStateTable mst = new MemberStateTable();
		mst.add(new MemberState("member1", 10, timestamp - 5_000, "ACTIVE"));
		mst.add(new MemberState("member2", 5, timestamp - 15_000, "ACTIVE"));
		mst.add(new MemberState("member3", 7, timestamp - 60_000, "INACTIVE"));
		mst.addSeenBy("member1");
		mst.addSeenBy("member2");
		
		MemberStateMonitor msm = new MemberStateMonitor();
		
		Field f1 = msm.getClass().getDeclaredField("memberId");
		Field f2 = msm.getClass().getDeclaredField("table");
		
		f1.setAccessible(true);
		f2.setAccessible(true);
		f1.set(msm, "member1");
		f2.set(msm, mst);
		f1.setAccessible(false);
		f2.setAccessible(false);
		
		msm.inactivateMember(timestamp, timeout);
		
		assertAll("mst",
				() -> assertEquals(mst.getTable().get(0).getMemberId(), "member1"),
				() -> assertEquals(mst.getTable().get(0).getLamportTimestamp(), 10),
				() -> assertEquals(mst.getTable().get(0).getLocalTimestamp(), timestamp - 5_000),
				() -> assertEquals(mst.getTable().get(0).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(1).getMemberId(), "member2"),
				() -> assertEquals(mst.getTable().get(1).getLamportTimestamp(), 5),
				() -> assertEquals(mst.getTable().get(1).getLocalTimestamp(), timestamp - 15_000),
				() -> assertEquals(mst.getTable().get(1).getState(), "ACTIVE"),
				() -> assertEquals(mst.getTable().get(2).getMemberId(), "member3"),
				() -> assertEquals(mst.getTable().get(2).getLamportTimestamp(), 7),
				() -> assertEquals(mst.getTable().get(2).getLocalTimestamp(), timestamp - 60_000),
				() -> assertEquals(mst.getTable().get(2).getState(), "INACTIVE"));
		
		Set<String> targetSeenBy = Set.of("member1", "member2");
		
		assertTrue(mst.getSeenByMembers().containsAll(targetSeenBy));
		assertTrue(targetSeenBy.containsAll(mst.getSeenByMembers()));
	}
	
	@Test
	public void copyMemberStateTable() throws NoSuchFieldException, 
	  										 SecurityException, 
	  										 IllegalArgumentException, 
	  										 IllegalAccessException
	{
		long timestamp = System.currentTimeMillis();
		
		MemberStateTable mst = new MemberStateTable();
		mst.add(new MemberState("member1", 10, timestamp - 5_000, "ACTIVE"));
		mst.add(new MemberState("member2", 5, timestamp - 15_000, "ACTIVE"));
		mst.add(new MemberState("member3", 7, timestamp - 60_000, "INACTIVE"));
		mst.addSeenBy("member1");
		mst.addSeenBy("member2");
		
		MemberStateMonitor msm = new MemberStateMonitor();
		
		Field f1 = msm.getClass().getDeclaredField("memberId");
		Field f2 = msm.getClass().getDeclaredField("table");
		
		f1.setAccessible(true);
		f2.setAccessible(true);
		f1.set(msm, "member1");
		f2.set(msm, mst);
		f1.setAccessible(false);
		f2.setAccessible(false);
		
		MemberStateTable mstCopy = msm.copyMemberStateTable();
		
		assertAll("mst",
				() -> assertEquals(mstCopy.getTable().get(0).getMemberId(), "member1"),
				() -> assertEquals(mstCopy.getTable().get(0).getLamportTimestamp(), 10),
				() -> assertEquals(mstCopy.getTable().get(0).getLocalTimestamp(), 
						timestamp - 5_000),
				() -> assertEquals(mstCopy.getTable().get(0).getState(), "ACTIVE"),
				() -> assertEquals(mstCopy.getTable().get(1).getMemberId(), "member2"),
				() -> assertEquals(mstCopy.getTable().get(1).getLamportTimestamp(), 5),
				() -> assertEquals(mstCopy.getTable().get(1).getLocalTimestamp(), 
						timestamp - 15_000),
				() -> assertEquals(mstCopy.getTable().get(1).getState(), "ACTIVE"),
				() -> assertEquals(mstCopy.getTable().get(2).getMemberId(), "member3"),
				() -> assertEquals(mstCopy.getTable().get(2).getLamportTimestamp(), 7),
				() -> assertEquals(mstCopy.getTable().get(2).getLocalTimestamp(), 
						timestamp - 60_000),
				() -> assertEquals(mstCopy.getTable().get(2).getState(), "INACTIVE"));
		
		Set<String> targetSeenBy = Set.of("member1", "member2");
		
		assertTrue(mstCopy.getSeenByMembers().containsAll(targetSeenBy));
		assertTrue(targetSeenBy.containsAll(mstCopy.getSeenByMembers()));
	}
}
