package preprocessing;

import java.util.ArrayList;
import java.util.Iterator;

import common.Point;
import common.Signature;

public class Reducer {

	/**
	 * Reduce the number of points by computing speed and keeping only local minimums.
	 * @param signature The signature to reduce.
	 */
	public static void keepSlowestPoints(Signature signature) {
		int n = signature.getPoints().size();

		ArrayList<Double> speeds = new ArrayList<Double>();
		for (int i = 0; i < n; i++) {
			double distance = 0;
			double time = 0;
			if (i > 0) {
				distance += Point.distance(signature.getPoints().get(i - 1), signature.getPoints().get(i));
				time += signature.getPoints().get(i).getTime() - signature.getPoints().get(i - 1).getTime();
			}
			if (i < n - 1) {
				distance += Point.distance(signature.getPoints().get(i), signature.getPoints().get(i + 1));
				time += signature.getPoints().get(i + 1).getTime() - signature.getPoints().get(i).getTime();
			}
			speeds.add(distance / time);
		}

		boolean incr = false;
		int i = 0;
		for (Iterator<Point> it = signature.getPoints().iterator(); it.hasNext();) {
			it.next();
			if (i < n - 1) {
				if (speeds.get(i) >= speeds.get(i + 1)) {
					incr = false;
					it.remove();
				} else if (incr) {
					it.remove();
				} else {
					incr = true;
				}
			}
			i++;
		}
	}

}
