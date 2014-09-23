package features;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
/**
 * List data type that corresponds to the chosen local features (x, y, ...) for a signature
 * in which each feature is a list of point value over time
 */
public class LocalFeatureVector extends ArrayList<ArrayList<Double>> {

	public LocalFeatureVector() {
		super();
	}

	public LocalFeatureVector(Collection<? extends ArrayList<Double>> c) {
		super(c);
	}

	public LocalFeatureVector(int initialCapacity) {
		super(initialCapacity);
	}

}
