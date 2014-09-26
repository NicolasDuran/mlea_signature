package train;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import Plot.PlotChart;

public class Classifier {

	/**
	 * Find the optimal threshold with a dichotomic algorithm
	 * @param leftClass The most left-located class
	 * @param rightClass The most right-located class
	 * @param lowerBound Lower bound of the dichotomic search
	 * @param upperBound Upper bound of the dichotomic search
	 * @return Optimal threshold
	 */
	private static double dichotomyComputeBestThreshold(ArrayList<Double> leftClass, ArrayList<Double> rightClass, double lowerBound, double upperBound)
	{
		//System.out.println("=== Compute Optimal Threshold ===");
		ArrayList<Double> leftClassBadPlaced = new ArrayList<Double>();
		ArrayList<Double> rightClassBadPlaced = new ArrayList<Double>();
		int numberOfSamples = leftClass.size() + rightClass.size();
		double lb = lowerBound;
		double ub = upperBound;
		double threshold = 0;

		int i = 0;
		while (ub - lb > 0.1)
		{
			leftClassBadPlaced.clear();
			rightClassBadPlaced.clear();

			// Compute threshold
			threshold = lb + (ub - lb) / 2.0;
			//System.out.print("[" + i + "] : threshold = " + threshold);

			// Count well placed and bad placed samples
			int wellPlaced = 0;
			for (Double v : leftClass) {
				if (v > threshold)
					leftClassBadPlaced.add(v);
				else wellPlaced++;
			}
			for (Double v : rightClass) {
				if (v < threshold)
					rightClassBadPlaced.add(v);
				else wellPlaced++;
			}

			double wellPlacedPercent = 100.0 * wellPlaced / numberOfSamples;
			//System.out.print(", Well placed = " + wellPlacedPercent + "%");
			//System.out.println();

			// We can't find better
			if (leftClassBadPlaced.size() == rightClassBadPlaced.size()) {
				break;
			}

			// Prepare next step
			if (leftClassBadPlaced.size() > rightClassBadPlaced.size())
				lb = threshold;
			else
				ub = threshold;
			i++;
		}

		//System.out.println("=================================");
		return threshold;
	}

	/**
	 * Classify two one dimensional classes
	 * @param c1 First class of elements
	 * @param c2 Second class of elements
	 * @return The threshold that better separates given classes
	 */
	public static double computeThreshold(ArrayList<Double> class1, ArrayList<Double> class2)
	{
		double threshold = 0;
		Collections.sort(class1);
		Collections.sort(class2);

		// Choose c1 as the most left-located class and c2 as the most right-located
		ArrayList<Double> c1, c2;
		if (class1.get(0) < class2.get(0)) {
			c1 = class1;
			c2 = class2;
		}
		else {
			c1 = class2;
			c2 = class1;
		}

		double lowerBound, upperBound;
		// Ideal case : data is well separated
		if (c1.get(c1.size() - 1) <= c2.get(0))
		{
			lowerBound = c1.get(c1.size() - 1);
			upperBound = c2.get(0);
			double marge = upperBound - lowerBound;
			threshold = lowerBound + marge / 2.0;
		}
		// Practical case : some data is mixed up
		else {
			ArrayList<Double> c1_marge_samples = new ArrayList<Double>();
			ArrayList<Double> c2_marge_samples = new ArrayList<Double>();
			lowerBound = c2.get(0);
			upperBound = c1.get(c1.size() - 1);

			// Get all data that is mixed up, between lower and upper boundaries
			for (Double v : c1) {
				if (v >= lowerBound && v <= upperBound)
					c1_marge_samples.add(v);
			}
			for (Double v : c2) {
				if (v >= lowerBound && v <= upperBound)
					c2_marge_samples.add(v);
			}

			threshold = dichotomyComputeBestThreshold(c1_marge_samples, c2_marge_samples, lowerBound, upperBound);
		}

		measureThresholdPerformances(c1, c2, threshold);
		return threshold;
	}

	private static void measureThresholdPerformances(ArrayList<Double> leftClass, ArrayList<Double> rightClass, double threshold)
	{
		int wellPlaced = 0;
		for (Double v : leftClass) {
			if (v <= threshold)
				wellPlaced++;
		}
		for (Double v : rightClass) {
			if (v >= threshold)
				wellPlaced++;
		}

		double perfs = 100.0 * wellPlaced / (leftClass.size() + rightClass.size());
		System.out.println("Threshold " + threshold + " performances : " + perfs + "%");
	}

	/**
	 * Test method
	 */
	public static void main(String[] args)
	{
		ArrayList<Double> classA = new ArrayList<Double>(Arrays.asList(
			200.0, 205.0, 210.0, 220.0, 230.0, 235.0, 239.0, 246.0, 250.0, 260.0, // outside
			280.0, 285.0, 300.0, 307.0, 310.0, 330.0)); //inside

		ArrayList<Double> classB = new ArrayList<Double>(Arrays.asList(
			335.0, 340.0, 345.0, 349.0, 362.0, 380.0, 390.0, 395.0, 400.0, 405.0, // outside
			283.0, 290.0, 309.0, 315.0, 320.0, 327.0)); //inside

		Collections.shuffle(classA);
		Collections.shuffle(classB);

		double threshold = Classifier.computeThreshold(classB, classA);

		PlotChart.Plot(classA, classB, threshold);

		System.out.println("Threshold = " + threshold);
	}
}
