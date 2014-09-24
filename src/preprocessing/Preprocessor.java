package preprocessing;

import preprocessing.reducer.Reducer;

import common.Signature;
import common.SignatureException;

public class Preprocessor {
	public static void main(String[] args) {
		try {
			Signature signature = new Signature("sample/USER1_1.txt");

			Preprocessor.normalizeAndReduce(signature);
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void normalizeAndReduce(Signature signature) {
		new Normalizer().normalize(signature);
		new Reducer().reduce(signature);
		signature.saveImage("signature.png");
	}
}
