package preprocessing;

import java.util.ArrayList;

import common.Point;
import common.Signature;

public class Reducer {

	/**
	 * Reduce the number of points by computing speed and keeping only local minimums.
	 * @param signature The signature to reduce.
	 */
	public void keepSlowestPoints(Signature signature) {
		int n = signature.getPoints().size();

		/* Compute speed for each points except first and last one */
		ArrayList<Point> points = signature.getPoints();
		ArrayList<Double> speeds = new ArrayList<Double>();
		speeds.add(0.); // Add first speed to maintain indexes

		for (int i = 1; i < n - 1; i++) {
			double distance = Point.distance(points.get(i - 1), points.get(i))
					+ Point.distance(points.get(i), points.get(i + 1));
			double time = points.get(i).getTime() - points.get(i - 1).getTime()
					+ points.get(i + 1).getTime() - points.get(i).getTime();

			speeds.add(distance / time);
		}

		// Add last
		speeds.add(Point.distance(points.get(n - 2), points.get(n - 1))
				/ points.get(n - 1).getTime() - points.get(n - 2).getTime());

		boolean incr = false;
		for (int i = 1; i < n - 1; i++) {
			//Decreasing
			if (speeds.get(i) >= speeds.get(i + 1)) {
				incr = false;
				signature.getPoints().get(i).setCritical(false);
			}
			// Keep increasing
			else if (incr) {
				signature.getPoints().get(i).setCritical(false);
			}
			// First increase -> Local minimum
			else {
				incr = true;
			}
		}
	}
}
