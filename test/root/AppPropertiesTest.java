package root;

import java.util.ResourceBundle;

public class AppPropertiesTest {
	
	public static Long connectionInboundFrequency() {
		return Long.parseLong(
				ResourceBundle.getBundle("app-test").getString("connection.inbound.frequency"));
	}
	
	public static Long connectionOutboundFrequency() {
		return Long.parseLong(
				ResourceBundle.getBundle("app-test").getString("connection.outbound.frequency"));
	}
	
	public static Long gossipSendFrequency() {
		return Long.parseLong(
				ResourceBundle.getBundle("app-test").getString("gossip.send.frequency"));
	}

	public static Long gossipReceiveFrequency() {
		return Long.parseLong(
				ResourceBundle.getBundle("app-test").getString("gossip.receive.frequency"));
	}
	
	public static Long memberstatetableChangeFrequency() {
		return Long.parseLong(
				   ResourceBundle.getBundle("app-test")
								 .getString("memberstatetable.change.frequency")
			   );
	}
	
	public static Long memberstatetablePollFrequency() {
		return Long.parseLong(
				ResourceBundle.getBundle("app-test").getString("memberstatetable.poll.frequency"));
	}
	
}
