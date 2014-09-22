import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import preprocessing.Normalizer;
import preprocessing.Reducer;

import common.Signature;
import common.SignatureException;


public class SignatureSystem {

	public SignatureSystem() {
	}

	public void train(ArrayList<Signature> trainSignatures)
	{

	}

	public boolean compareSignatures(Signature s1, Signature s2)
	{
		return false;
	}

	/**
	 * Measure system performances by training and testing
	 * using the Cross Validation algorithm
	 * @param databaseFilename File containing all the signature paths available as a database
	 */
	private void mesurePerformances(String databaseFilename)
	{
		ArrayList<Signature> databaseSignatures = new ArrayList<Signature>();

		try {
			// Fill database with preprocessed signatures
			BufferedReader br = new BufferedReader(new FileReader(databaseFilename));
			String line;

			while ((line = br.readLine()) != null) {
				Signature s = new Signature(line);
				Normalizer.normalize(s);
				Reducer.keepSlowestPoints(s);
				databaseSignatures.add(s);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
	}
}
