package preprocessing.reducer;

import java.util.ArrayList;

import common.Point;
import common.Signature;

public class BraultReducer {
	/* Parameters */
	private static double ARX_MAX = Math.PI / 32;	/* Maximum angle (0 < ARX_MAX < Pi/2) */
	private static double DISS_MAX = Math.PI / 4;	/* Maximum disymmetry */
	private static int K = 2; /* (K > 2) */

	private int nTotal; /* Total number of points */
	private double tetaRX; /* (i - n) angle */
	private double tetaAX; /* (i + n) angle */


	public void apply(Signature signature) {
		this.nTotal = signature.getPoints().size();

		ArrayList<Double> FI = perceptualImportance(signature);

		for (int i = 2; i < nTotal - 2; i++) {
			signature.getPoints().get(i).setAngle(FI.get(i - 2));
		}

		automaticSegmentation(signature, FI);
	}

	private void automaticSegmentation(Signature signature, ArrayList<Double> FI) {
		int nbCritical = 0;

		double FILocalMax = 0.;

		for (int i = 2; i < nTotal - 2; i++) {
			// TODO if i init at 2
			if (FI.get(i - 2) > FILocalMax) {
				FILocalMax = FI.get(i - 2);
			}
			if (FI.get(i - 2) == 0 && FILocalMax > 0) {
				signature.getPoints().get(i - 2).setCritical(true);
				FILocalMax = 0;
				nbCritical++;
			}
		}
		//System.out.println("Brault: " + nbCritical + " critical points founds");
	}

	private void computeAngles(ArrayList<Point> points, int i, int n) {

		Point top = points.get(i);
		Point r = points.get(i - n);
		Point rNext = points.get(i - n - 1);
		Point a = points.get(i + n);
		Point aNext = points.get(i + n + 1);

		double midX = (r.getX() + a.getX()) / 2;
		double midY = (r.getY() + a.getY()) / 2;

		double median = (top.getY() - midY) / (top.getX() - midX);
		double rSlope = Point.slope(r, rNext);
		double aSlope = Point.slope(a, aNext);

		tetaRX = Math.atan((rSlope - median) / (1 + rSlope * median));
		tetaAX = Math.atan((aSlope - median) / (1 + aSlope * median));
	}

	/**
	 * Signature coordinates perceptual importance estimation.
	 * @param signature
	 * @return
	 */
	private ArrayList<Double> perceptualImportance(Signature signature) {
		/* Angles sums */
		ArrayList<Double> FI = new ArrayList<Double>();

		/* Main loop */
		for (int i = 2; i < nTotal - 2; i++) {

			int M = 0;				/* Number of points to inhibit */
			boolean first = true;	/* Begin sum */

			double FIi = 0.;

			/* First loop */
			for (int n = 1; (n < i) && (n < nTotal - i - 1); n++) {

				/* Do not compute when button == false */
				if (!signature.getPoints().get(i - n + 1).isButton() || !signature.getPoints().get(i + n).isButton())
					break;

				computeAngles(signature.getPoints(), i, n);

				if (Math.abs(tetaRX) < ARX_MAX && Math.abs(tetaAX) < ARX_MAX
						&& Math.abs(Math.abs(tetaRX) - Math.abs(tetaAX)) < DISS_MAX) {
					if (n > K * M) {
						FIi += Math.cos(tetaRX) * Math.cos(tetaAX);
						first = false;
					}

					if ((Math.abs(tetaRX) > ARX_MAX || Math.abs(tetaAX) > ARX_MAX) && first) {
						M++;
					} else {
						break;
					}
				}
			}
			FI.add(FIi);
		}
		return FI;
	}
}
