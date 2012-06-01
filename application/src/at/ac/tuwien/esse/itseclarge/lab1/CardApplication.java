package at.ac.tuwien.esse.itseclarge.lab1;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class CardApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> rrcs = new HashSet<Class<?>>();
    rrcs.add(CardResource.class);
    return rrcs;
	}

}
