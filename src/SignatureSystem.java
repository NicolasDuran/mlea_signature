import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import plot.PlotChart;
import preprocessing.Preprocessor;
import train.Classifier;

import common.LabeledSignature;
import common.Signature;
import common.SignatureException;

import distance.DTWNaive;
import features.FeatureExtractor;
import features.LocalFeatureVector;


public class SignatureSystem
{
	final int numberOfUsers = 5;
	final int trainIteration = 3;

	double forgeryThreshold;
	double identityThreshold;

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
		try {
			File output = new File("log/perfs.log");
			FileWriter writer = new FileWriter(output);

			double globalSuccess = 0.0;
			double globalForgerySuccess = 0.0;
			double globalIdentitySuccess = 0.0;
			double thresholdMean = 0.0;

			for (int i = 0; i < this.trainIteration; i++)
			{
				writer.write("=== Iteration " + i + " ===" + System.getProperty("line.separator"));

				// Train
				System.out.println("================ Train ================");
				chooseTrainAndTestSignatures(database);
				this.forgeryThreshold = trainUnversalForgeryThreshold();
				thresholdMean += this.forgeryThreshold;

				writer.write("Chosen LocalThreshold : " + this.forgeryThreshold + System.getProperty("line.separator"));
				System.out.println("Chosen LocalThreshold : " + this.forgeryThreshold);

				//this.identityThreshold = trainUnversalIdentityThreshold();
				//writer.write("GlobalThreshold : " + this.identityThreshold + System.getProperty("line.separator"));
				//System.out.println("GlobalThreshold : " + this.identityThreshold);

				// Test
				System.out.println("================ Test ================");

				double success = 0.0;
				double forgerySuccess = 0.0;
				double identitySuccess = 0.0;
				int numberOfForgeryTests = 0;
				int numberOfIdentityTests = 0;

				for (int j = 0; j < this.testSignatures.size(); j++) {
					for (int k = j + 1; k < this.testSignatures.size(); k++) {
						if (j != k)
						{
							// Don't compare forgery with forgery
							if (!this.testSignatures.get(j).isGenuine() && !this.testSignatures.get(k).isGenuine())
								continue;

							// Same user = same ID + genuine
							boolean realDecision = this.testSignatures.get(j).getUserID() == this.testSignatures.get(k).getUserID() &&
									this.testSignatures.get(j).isGenuine() == this.testSignatures.get(k).isGenuine();
							// Compare
							CompareResult res = compareSignatures(this.testSignatures.get(j), this.testSignatures.get(k),
																this.forgeryThreshold, this.identityThreshold);

							// Write log
							writer.write(this.testSignatures.get(j).getName() + (this.testSignatures.get(j).isGenuine() ? " (genuine)" : " (forgery)"));
							writer.write(" - " + this.testSignatures.get(k).getName() + (this.testSignatures.get(k).isGenuine() ? " (genuine)" : " (forgery)"));
							writer.write(" : dist = " + res.distance + ", decision = " + res.decision + ", reality = " + realDecision + System.getProperty("line.separator"));

							if (res.decision == realDecision) {
								success += 1.0;
							}

							if (this.testSignatures.get(j).getUserID() == this.testSignatures.get(k).getUserID() &&
								this.testSignatures.get(j).isGenuine() != this.testSignatures.get(k).isGenuine())
							{
								if (res.decision == realDecision)
									forgerySuccess += 1.0;
								numberOfForgeryTests++;
							}
							else {
								if (res.decision == realDecision)
									identitySuccess += 1.0;
								numberOfIdentityTests++;
							}
						}
					}
				}

				success = 100.0 * success / (numberOfForgeryTests + numberOfIdentityTests);
				forgerySuccess = 100.0 * forgerySuccess / numberOfForgeryTests;
				identitySuccess = 100.0 * identitySuccess / numberOfIdentityTests;
				globalSuccess += success;
				globalForgerySuccess += forgerySuccess;
				globalIdentitySuccess += identitySuccess;

				System.out.println("[" + i + "]: " + success + "% success over " +
						numberOfForgeryTests + " forgery tests and " + numberOfIdentityTests + " identity tests.");
				System.out.println("     " + forgerySuccess + "% factory success");
				System.out.println("     " + identitySuccess + "% identity success");

				// Writer log result
				writer.write("=== Result ===" + System.getProperty("line.separator"));
				writer.write("=== " + success + "% success ===" + System.getProperty("line.separator"));
				writer.write("=== " + forgerySuccess + "% forgery success ===" + System.getProperty("line.separator"));
				writer.write("=== " + identitySuccess + "% identity success ===" + System.getProperty("line.separator"));
			}

			thresholdMean /= trainIteration;
			globalSuccess /= trainIteration;
			globalForgerySuccess /= trainIteration;
			globalIdentitySuccess /= trainIteration;

			System.out.println("=================================================");
			System.out.println("[Threshold]: " + thresholdMean);
			System.out.println("[Performances]: " + globalSuccess + "% success");
			System.out.println("                " + globalForgerySuccess + "% forgery success");
			System.out.println("                " + globalIdentitySuccess + "% identity success");

			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
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

		System.out.println("[Threshold]: Compute forgery threshold for each user");
		for (int i = 0; i < numberOfUsers; i++) {
			thresholdMean += trainPersonalThreshold(this.userGenuineTrainSignatures.get(i), this.userForgeryTrainSignatures.get(i));
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
				double d = compareSignatures(gs, fs);
				extraDistances.add(d);
			}
		}

		// Compute extra distances by comparaing the user to other signatures
		for (int i = 0; i < userTrain.size(); i++) {
			for (int j = i; j < userTrain.size(); j++) {
				double d = compareSignatures(userTrain.get(i), userTrain.get(j));
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
	 * Compare two preprocessed signatures
	 * @param s1 First signature
	 * @param s2 Second signature
	 * @return Returns the measured distance between those signature
	 */
	public double compareSignatures(Signature s1, Signature s2)
	{
		LocalFeatureVector v1 = FeatureExtractor.extractLocalFeature(s1);
		LocalFeatureVector v2 = FeatureExtractor.extractLocalFeature(s2);

		double distance = DTWNaive.DTWDistance(v1, v2);
		return distance;
	}

	/**
	 * Compare two preprocessed signatures and decide if they belong to the same user
	 * @param s1 First signature
	 * @param s2 Second signature
	 * @param localThreshold The threshold to use for local features to take decision
	 * @param globalThreshold The threshold to use for global features to take decision
	 * @return Returns the measured distance between those signature
	 * and wether they are from the same person or not
	 */
	public CompareResult compareSignatures(Signature s1, Signature s2, double localThreshold, double globalThreshold)
	{
		CompareResult res = new CompareResult();
		LocalFeatureVector v1 = FeatureExtractor.extractLocalFeature(s1);
		LocalFeatureVector v2 = FeatureExtractor.extractLocalFeature(s2);

		res.distance = DTWNaive.DTWDistance(v1, v2);
		res.decision = res.distance < localThreshold;

		return res;
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
				CompareResult res = compareSignatures(s1, s2, this.forgeryThreshold, this.identityThreshold);

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
}
