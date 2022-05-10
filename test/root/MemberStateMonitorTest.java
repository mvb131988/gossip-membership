package root;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class MemberStateMonitorTest {

	@Test
	public void testUpdateMemberStateAndGetVectorClockTable() throws NoSuchFieldException,
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
}
