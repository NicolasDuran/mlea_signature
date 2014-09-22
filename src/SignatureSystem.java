import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import preprocessing.Normalizer;

import common.LabeledSignature;
import common.Signature;
import common.SignatureException;


public class SignatureSystem {

	public SignatureSystem() {
	}

	public void train(ArrayList<LabeledSignature> trainSignatures)
	{
		// TODO
	}

	public boolean compareSignatures(Signature s1, Signature s2)
	{
		// TODO
		return true;
	}

	private ArrayList<LabeledSignature> parseDatabaseFile(String filename)
	{
		ArrayList<LabeledSignature> database = new ArrayList<LabeledSignature>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;

			while ((line = br.readLine()) != null) {
				LabeledSignature s = new LabeledSignature(line);
				Normalizer.normalize(s);
				database.add(s);
			}
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
	 * Measure system performances by training and testing
	 * using the Cross Validation algorithm
	 * @param genuinesDatabase File containing all the genuine signatures paths available as a database
	 * @param forgeriesDatabase File containing all the forgeries signatures paths available as a database
	 */
	public void mesurePerformances(String genuinesDatabase, String forgeriesDatabase)
	{
		ArrayList<LabeledSignature> genuines = parseDatabaseFile(genuinesDatabase);
		ArrayList<LabeledSignature> forgeries = parseDatabaseFile(forgeriesDatabase);
		if (genuines == null || forgeries == null) return;

		// Cross Validation Algorithm
		ArrayList<LabeledSignature> trainSignatures = new ArrayList<LabeledSignature>();
		ArrayList<LabeledSignature> testSignatures = new ArrayList<LabeledSignature>();
		int trainSize = (int)(6.0 * genuines.size() / 10.0); // 60% of the database size
		double globalPerfs = 0;

		for (int i = 0; i < genuines.size(); i++)
		{
			trainSignatures.clear();
			testSignatures.clear();

			// Select train and test signatures from genuine database
			int borneSup = (i + trainSize) % genuines.size();
			for (int k = 0; k < genuines.size(); k++)
			{
				if (borneSup > i) {
					if (k >= i && k < borneSup)
						trainSignatures.add(genuines.get(k));
					else
						testSignatures.add(genuines.get(k));
				}
				else if (borneSup < i) {
					if (k >= i && k < borneSup)
						testSignatures.add(genuines.get(k));
					else
						trainSignatures.add(genuines.get(k));
				}
			}

			testSignatures.addAll(forgeries);

			/* Compare two by two all test signatures, but avoid to compare two forgeries
			 * signatures, because we don't know in this case */
			int numberOfSuccess = 0;
			int numberOfTests = 0;
			for (int k = 0; k < testSignatures.size() - 1; k++)
			{
				LabeledSignature s1 = testSignatures.get(k);
				for (int j = k + 1; j < testSignatures.size(); j++)
				{
					LabeledSignature s2 = testSignatures.get(j);

					if (!s1.isGenuine() && !s2.isGenuine()) {
						continue;
					}

					boolean result = compareSignatures(s1, s2);
					boolean realResult = (s1.getUserID() == s2.getUserID()) && (s1.isGenuine() && s2.isGenuine());
					if (result == realResult) {
						numberOfSuccess++;
					}

					numberOfTests++;
				}
			}

			globalPerfs += (double)numberOfSuccess / (double)numberOfTests;
		}

		globalPerfs = 100.0 * globalPerfs / genuines.size();
	}
}
