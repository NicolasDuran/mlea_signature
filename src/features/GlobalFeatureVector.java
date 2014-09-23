package features;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
/**
 *	List data type that corresponds to the chosen global features for a signature
 */
public class GlobalFeatureVector extends ArrayList<Double> {

	public GlobalFeatureVector() {
		super();
	}

	public GlobalFeatureVector(Collection<? extends Double> c) {
		super(c);
	}

	public GlobalFeatureVector(int initialCapacity) {
		super(initialCapacity);
	}

}
