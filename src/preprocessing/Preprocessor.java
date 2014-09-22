package preprocessing;

import common.Signature;
import common.SignatureException;

public class Preprocessor {
	public static void main(String[] args) {
		try {
			Signature signature = new Signature("sample/USER2_1.txt");

			Preprocessor.normalizeAndReduce(signature);
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void normalizeAndReduce(Signature signature) {
		new Normalizer().normalize(signature);
		new Reducer().keepSlowestPoints(signature);
		signature.saveImage("signature.png");
		//new Reducer().distances(signature);
	}
}
