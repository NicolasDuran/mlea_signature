package features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class PCA
{
	private static RealMatrix normalize(ArrayList<ArrayList<Double>> samples)
	{
		// Convert to matrix
		RealMatrix coordinates;

		int width = samples.get(0).size();
		int height = samples.size();
		double[][] coords = new double[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				coords[i][j] = samples.get(i).get(j);
			}
		}

		coordinates = MatrixUtils.createRealMatrix(coords);

		// Center samples on each coordinate
		double[] means = new double[coordinates.getColumnDimension()];

		for (int j = 0; j < coordinates.getColumnDimension(); j++) {
			for (int i = 0; i < coordinates.getRowDimension(); i++) {
				means[j] += coordinates.getEntry(i, j);
			}

			means[j] /= coordinates.getRowDimension();
		}

		for (int j = 0; j < coordinates.getColumnDimension(); j++) {
			for (int i = 0; i < coordinates.getRowDimension(); i++) {
				coordinates.addToEntry(i, j, -means[j]);
			}
		}

		return coordinates;
	}

	private static RealMatrix computeCovarianceMatrix(RealMatrix m)
	{
		double weight = 1.0 / m.getRowDimension();
		RealMatrix t = m.transpose();
		RealMatrix prod = m.preMultiply(t);
		return prod.scalarMultiply(weight);
	}

	private static ArrayList<RealVector> computeAndSelectEigenVectors(RealMatrix covarianceMatrix, int numberOfDimensions)
	{
		EigenDecomposition eigenValuesSystem = new EigenDecomposition(covarianceMatrix);
		RealMatrix diagonalMatrix = eigenValuesSystem.getD();
		RealMatrix eigenVectors = eigenValuesSystem.getV();
		ArrayList<RealVector> goodEigenVectors = new ArrayList<RealVector>();

		double trace = diagonalMatrix.getTrace();

		double[] eigenValues = eigenValuesSystem.getRealEigenvalues(); // could be complex ?
		double[] sortedEigenValues = eigenValues.clone();

		if (eigenValuesSystem.hasComplexEigenvalues()) {
			System.err.println("COMPLEX VALUES WHAT SHOULD I DO ???");
		}

		// Keep original indexes for each value before sorting to retreive eigen vectors
		HashMap<Double, Integer> originalIndexes = new HashMap<Double, Integer>();
		for (int i = 0; i < eigenValues.length; i++) {
			originalIndexes.put(eigenValues[i], i);
		}

		Arrays.sort(sortedEigenValues);

		// Compute inertia percentage and extract corresponding eigen vectors
		double numerator = 0;
		for (int i = sortedEigenValues.length - 1; i >= 0; i--)
		{
			int vectorIndex = originalIndexes.get(sortedEigenValues[i]);
			goodEigenVectors.add(eigenVectors.getColumnVector(vectorIndex));

			numerator += sortedEigenValues[i];
			double inertia = numerator / trace;

			if (inertia >= 0.8 || sortedEigenValues.length - i == numberOfDimensions) { // Stop
				break;
			}
		}

		return goodEigenVectors;
	}

	private static ArrayList<ArrayList<Double>> computePrincipalComponents(RealMatrix m, ArrayList<RealVector> eigenVectors)
	{
		ArrayList<ArrayList<Double>> newCoords = new ArrayList<ArrayList<Double>>();
		ArrayList<RealVector> principalComponents = new ArrayList<RealVector>();

		for (RealVector u : eigenVectors) {
			RealVector c = m.operate(u);
			principalComponents.add(c);
		}

		for (int i = 0; i < principalComponents.get(0).getDimension(); i++) {
			ArrayList<Double> sample = new ArrayList<Double>();
			for (int j = 0; j < principalComponents.size(); j++) {
				sample.add(principalComponents.get(j).getEntry(i));
			}
			newCoords.add(sample);
		}

		return newCoords;
	}

	/**
	 * Principale Componant Analysis
	 * @param samplesCoordinates The coordinates of each sample in the original space
	 * @return The new coordinates of each sample in the new space
	 */
	public static ArrayList<ArrayList<Double>> compute(ArrayList<ArrayList<Double>> samplesCoordinates)
	{
		RealMatrix m = normalize(samplesCoordinates);
		RealMatrix covm = computeCovarianceMatrix(m);
		ArrayList<RealVector> goodEigenVectors = computeAndSelectEigenVectors(covm, Integer.MAX_VALUE);
		ArrayList<ArrayList<Double>> newCoords = computePrincipalComponents(m, goodEigenVectors);

		System.out.println("Vector size before = " + samplesCoordinates.get(0).size());
		System.out.println("Vector size after = " + newCoords.get(0).size());

		return newCoords;
	}

	/**
	 * Principale Componant Analysis
	 * @param samplesCoordinates The coordinates of each sample in the original space
	 * @param k The number of desired dimension of the new space
	 * @return The new coordinates of each sample in the new space
	 */
	public static ArrayList<ArrayList<Double>> compute(ArrayList<ArrayList<Double>> samplesCoordinates, int k)
	{
		RealMatrix m = normalize(samplesCoordinates);
		RealMatrix covm = computeCovarianceMatrix(m);
		ArrayList<RealVector> goodEigenVectors = computeAndSelectEigenVectors(covm, k);
		ArrayList<ArrayList<Double>> newCoords = computePrincipalComponents(m, goodEigenVectors);

		System.out.println("Vector size before = " + samplesCoordinates.get(0).size());
		System.out.println("Vector size after = " + newCoords.get(0).size());

		return newCoords;
	}

	public static void main(String[] args)
	{
		ArrayList<ArrayList<Double>> samples = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> sample1 = new ArrayList<Double>();
		ArrayList<Double> sample2 = new ArrayList<Double>();
		ArrayList<Double> sample3 = new ArrayList<Double>();
		ArrayList<Double> sample4 = new ArrayList<Double>();
		ArrayList<Double> sample5 = new ArrayList<Double>();
		ArrayList<Double> sample6 = new ArrayList<Double>();

		sample1.add(8.0);
		sample1.add(1.0);
		sample1.add(0.0);

		sample2.add(4.0);
		sample2.add(6.0);
		sample2.add(5.0);

		sample3.add(6.0);
		sample3.add(8.0);
		sample3.add(7.0);

		sample4.add(10.0);
		sample4.add(4.0);
		sample4.add(7.0);

		sample5.add(8.0);
		sample5.add(2.0);
		sample5.add(5.0);

		sample6.add(0.0);
		sample6.add(3.0);
		sample6.add(6.0);

		samples.add(sample1);
		samples.add(sample2);
		samples.add(sample3);
		samples.add(sample4);
		samples.add(sample5);
		samples.add(sample6);

		PCA.compute(samples);
	}
}
