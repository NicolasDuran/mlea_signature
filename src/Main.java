import preprocessing.Normalizer;

import common.Signature;
import common.SignatureException;

public class Main {

	public static void main(String[] args) {
		try {
			Signature signature = new Signature("sample/USER1_1.txt");

			new Normalizer().normalize(signature);
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}