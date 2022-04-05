package root;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppProperties {
	
	public static List<InetSocketAddress> getNodes() {
		String[] nodes = ResourceBundle.getBundle("app").getString("nodes").split(";");
		List<InetSocketAddress> l = 
				Stream.of(nodes)
					  .map(n -> new InetSocketAddress(n.split(":")[0], 
							  						  Integer.parseInt(n.split(":")[1])
							  						 )
						  )
					  .collect(Collectors.toList());
		return l;
	}

}
