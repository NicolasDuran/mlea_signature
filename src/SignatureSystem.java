import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.linear.RealVector;

import plot.PlotChart;
import preprocessing.Preprocessor;
import train.Classifier;

import common.LabeledSignature;
import common.Signature;
import common.SignatureException;

import distance.Comparator;
import distance.CompareResult;
import features.FeatureExtractor;
import features.GlobalFeatureVector;
import features.PCA;


public class SignatureSystem {
	private static final boolean FEATURE_TEST = false;
	final int numberOfUsers = 5;
	final int trainIteration = 1;

	double threshold;
	double thresholds[] = new double[5];
	boolean plotMode;

	private ArrayList<ArrayList<LabeledSignature>> userGenuineTrainSignatures;
	private ArrayList<ArrayList<LabeledSignature>> userForgeryTrainSignatures;
	private ArrayList<LabeledSignature> testSignatures;

	public SignatureSystem() {
		plotMode = false;
	}

	/**
	 * Train the program and measure performances
	 * @param database File containing all the signatures path of all users
	 */
	public void measurePerformances(String database)
	{
		boolean bestVarFeatures[] = null;
		double bestVar = Double.MAX_VALUE;

		for (int step = (int) Math.pow(2, 0); step < Math.pow(2, 11); step++) {

			int binary = step;
			boolean selector[] = new boolean[16];
			for (int i = 0; i < 16; i++) {
				selector[i] = binary % 2 == 1;
				binary /= 2;
			}
			FeatureExtractor.selector = selector;


			for (int i = 0; i < this.trainIteration; i++)
			{
				// Train
				chooseTrainAndTestSignatures(database);
				this.threshold = trainUnversalForgeryThreshold();
			}

			double var = 0;
			for (double threshold : thresholds) {
				var += (threshold - this.threshold) * (threshold - this.threshold);
			}

			var /= 5;
			if (var < bestVar) {
				bestVar = var;
				bestVarFeatures = selector;
			}

		}
		System.out.println("Variance: " + bestVar + " " + selectorToString(bestVarFeatures));
	}

	private String selectorToString(boolean selector[]) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (boolean b : selector) {
			builder.append(b ? 1 : 0);
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Randomly select train and test signatures
	 * @param database File containing all the signatures path of all users
	 */
	public void chooseTrainAndTestSignatures(String database)
	{
		ArrayList<LabeledSignature> signatures = parseDatabaseFile(database);
		if (database == null) return;

		ArrayList<ArrayList<LabeledSignature>> userGenuineSignatures = new ArrayList<ArrayList<LabeledSignature>>(numberOfUsers);
		ArrayList<ArrayList<LabeledSignature>> userForgerySignatures = new ArrayList<ArrayList<LabeledSignature>>(numberOfUsers);
		this.userGenuineTrainSignatures = new ArrayList<ArrayList<LabeledSignature>>(numberOfUsers);
		this.userForgeryTrainSignatures = new ArrayList<ArrayList<LabeledSignature>>(numberOfUsers);
		this.testSignatures = new ArrayList<LabeledSignature>();

		// Initialize lists
		for (int i = 0; i < numberOfUsers; i++) {
			userGenuineSignatures.add(new ArrayList<LabeledSignature>());
			userForgerySignatures.add(new ArrayList<LabeledSignature>());
		}

		// Class signatures according to their userID
		for (LabeledSignature s : signatures) {
			if (s.isGenuine())
				userGenuineSignatures.get(s.getUserID() - 1).add(s);
			else
				userForgerySignatures.get(s.getUserID() - 1).add(s);
		}

		// Select random train signatures for each user
		int genuineTrainSize = (int)(6.0 * userGenuineSignatures.get(0).size() / 10.0);
		int forgeryTrainSize = (int)(6.0 * userGenuineSignatures.get(0).size() / 10.0);

		for (int i = 0; i < numberOfUsers; i++)
		{
			boolean[] checkGenuine = new boolean[userGenuineSignatures.get(i).size()];
			this.userGenuineTrainSignatures.add(chooseRandomSignature(userGenuineSignatures.get(i), genuineTrainSize, checkGenuine));
			// Store test signatures
			for (int k = 0; k < checkGenuine.length; k++) {
				if (!checkGenuine[k]) {
					this.testSignatures.add(userGenuineSignatures.get(i).get(k));
				}
			}

			boolean[] checkForgery = new boolean[userForgerySignatures.get(i).size()];
			this.userForgeryTrainSignatures.add(chooseRandomSignature(userForgerySignatures.get(i), forgeryTrainSize, checkForgery));
			// Store test signatures
			for (int k = 0; k < checkForgery.length; k++) {
				if (!checkForgery[k]) {
					this.testSignatures.add(userForgerySignatures.get(i).get(k));
				}
			}
		}
	}

	/**
	 * Choose random signatures within the database
	 * @param database The list of signatures
	 * @param k The number of random signatures to choose
	 * @param check A boolean array that indicates the signatures that were choosen (referenced by their index)
	 * @return A list of k randomly choosen signatures among database
	 */
	private ArrayList<LabeledSignature> chooseRandomSignature(ArrayList<LabeledSignature> database, int k, boolean[] check)
	{
		ArrayList<LabeledSignature> choosenSignatures = new ArrayList<LabeledSignature>();
		Random rand = new Random();

		Arrays.fill(check, false);

		if (database.size() < k) {
			return null;
		}

		for (int i = 0; i < k; i++)
		{
			int index = rand.nextInt(check.length);

			if (check[index]) // try again
				i--;
			else {
				check[index] = true;
				choosenSignatures.add(database.get(i));
			}
		}

		return choosenSignatures;
	}

	/**
	 * Compute the identity threshold, by comparing each user to other users
	 * Need to chooseTrainAndTest Signatures first
	 */
	public double trainUnversalIdentityThreshold()
	{
		double thresholdMean = 0;

		for (int i = 0; i < numberOfUsers; i++)
		{
			// Combine all user's signatures different from user i
			ArrayList<LabeledSignature> trainExtra = new ArrayList<LabeledSignature>();
			for (int j = 0; j < numberOfUsers; j++) {
				if (j != i) {
					trainExtra.addAll(this.userGenuineTrainSignatures.get(j));
				}
			}

			thresholdMean += trainPersonalThreshold(this.userGenuineTrainSignatures.get(i), trainExtra);
		}

		thresholdMean /= numberOfUsers;

		return thresholdMean;
	}

	/**
	 * Compute the forgery threshold, by comparing each user to his forgery signatures
	 * Need to chooseTrainAndTest Signatures first
	 */
	public double trainUnversalForgeryThreshold()
	{
		double thresholdMean = 0;

		//		System.out.println("[Threshold]: Compute forgery threshold for each user");
		for (int i = 0; i < numberOfUsers; i++) {
			double threshold =trainPersonalThreshold(this.userGenuineTrainSignatures.get(i), this.userForgeryTrainSignatures.get(i));
			thresholds[i] = threshold;
			thresholdMean += threshold;
		}

		thresholdMean /= numberOfUsers;

		return thresholdMean;
	}

	/**
	 * Classify user's signatures with an other train signatures
	 * @param userTrain Genuine signatures of a user (intra)
	 * @param signatureTrain Train of signatures to compare to the user (extra)
	 * @return The threshold that best separates user intra class with the user extra class
	 */
	private double trainPersonalThreshold(ArrayList<LabeledSignature> userTrain, ArrayList<LabeledSignature> signatureTrain)
	{
		ArrayList<Double> intraDistances = new ArrayList<Double>();
		ArrayList<Double> extraDistances = new ArrayList<Double>();

		// Compute intra distances by comparing the user to himself
		for (Signature gs : userTrain) {
			for (Signature fs : signatureTrain) {
				double d = Comparator.compareSignatures(gs, fs);
				extraDistances.add(d);
			}
		}

		// Compute extra distances by comparaing the user to other signatures
		for (int i = 0; i < userTrain.size(); i++) {
			for (int j = i; j < userTrain.size(); j++) {
				double d = Comparator.compareSignatures(userTrain.get(i), userTrain.get(j));
				intraDistances.add(d);
			}
		}

		double threshold = Classifier.computeThreshold(intraDistances, extraDistances);

		if (plotMode) {
			PlotChart.Plot("User " + userTrain.get(0).getUserID(), intraDistances, extraDistances, threshold);
		}

		return threshold;
	}



	/**
	 * Compare a list of signature and store the results
	 * @param inputfile A file containing on each line two signature paths to compare.
	 * @param outputfile The file in which we store the comparison results
	 */
	public void compareSignaturesFromFile(String inputfile, String outputfile)
	{
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputfile));
			File output = new File(outputfile);
			FileWriter writer = new FileWriter(output);

			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");
				if (tokens.length != 2) {
					System.err.println("Malformed input file.");
					return;
				}

				// Create signatures
				Signature s1 = new Signature(tokens[0]);
				Signature s2 = new Signature(tokens[1]);

				// Preprocess signatures
				Preprocessor.normalizeAndReduce(s1);
				Preprocessor.normalizeAndReduce(s2);

				// Compare signatures
				CompareResult res = Comparator.compareSignatures(s1, s2, this.threshold);

				// Write result
				writer.write(line + " " + res.distance + " " + res.getDecision() + System.getProperty("line.separator"));
			}

			br.close();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param filename The database file containing all signature file paths
	 * @return The list of all extracted and preprocessed signatures
	 */
	private ArrayList<LabeledSignature> parseDatabaseFile(String filename)
	{
		ArrayList<LabeledSignature> database = new ArrayList<LabeledSignature>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;

			while ((line = br.readLine()) != null) {
				LabeledSignature s = new LabeledSignature(line);
				Preprocessor.normalizeAndReduce(s);
				database.add(s);
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (SignatureException e) {
			e.printStackTrace();
			return null;
		}

		return database;
	}

	/**
	 * @param database The database file containing all signature file paths
	 * @return The eigen vectors to convert global features into a new space with less dimension
	 * @see PCA.computeNewCoordinates
	 */
	public ArrayList<RealVector> applyPCA(String database)
	{
		ArrayList<LabeledSignature> signatures = parseDatabaseFile(database);
		ArrayList<ArrayList<Double>> featureVectors = new ArrayList<ArrayList<Double>>();

		for (LabeledSignature s : signatures) {
			GlobalFeatureVector v = FeatureExtractor.extractGlobalFeature(s);
			featureVectors.add(v);
		}

		return PCA.compute(featureVectors);
	}

	/**
	 * @param database The database file containing all signature file paths
	 * @return The list of each feature normalizing weights, i.e the maximum value find
	 * over the whole database for each of them
	 */
	public double[] findGlobalFeaturesWeightsUsingMax(String database)
	{
		ArrayList<LabeledSignature> signatures = parseDatabaseFile(database);
		double[] weights = new double[FeatureExtractor.numberOfGlobalFeatures];
		Arrays.fill(weights, Double.MIN_VALUE);

		for (LabeledSignature s : signatures)
		{
			GlobalFeatureVector v = FeatureExtractor.computeGlobalFeatures(s);
			for (int i = 0; i < v.size(); i++)
			{
				if (v.get(i) > weights[i]) {
					weights[i] = v.get(i);
				}
			}
		}

		return weights;
	}

	/**
	 * @param database The database file containing all signature file paths
	 * @return The list of each feature normalizing weights, i.e the maximum value find
	 * over the whole database for each of them
	 */
	public double[] findGlobalFeaturesWeightsUsingMean(String database)
	{
		ArrayList<LabeledSignature> signatures = parseDatabaseFile(database);
		double[] weights = new double[FeatureExtractor.numberOfGlobalFeatures];
		Arrays.fill(weights, Double.MIN_VALUE);

		for (LabeledSignature s : signatures)
		{
			GlobalFeatureVector v = FeatureExtractor.computeGlobalFeatures(s);
			for (int i = 0; i < v.size(); i++) {
				weights[i] += v.get(i);
			}
		}

		for (int i = 0; i < weights.length; i++) {
			weights[i] /= signatures.size();
		}

		return weights;
	}
}
