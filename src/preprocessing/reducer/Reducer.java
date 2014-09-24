package preprocessing.reducer;

import common.Signature;

public class Reducer {
	public void reduce(Signature signature) {
		System.out.println("=== Reducer ===");

		new SpeedReducer().apply(signature);
		new BraultReducer().apply(signature);

		signature.getPoints().get(0).setCritical(true);
		signature.getPoints().get(signature.getPoints().size() - 1).setCritical(true);

		int nTotal = signature.getPoints().size();
		int nCritical = signature.getCriticalPoints().size();
		signature.saveImage("signature.png");

		signature.deleteNotCritical();

		System.out.println("Total: " + nCritical
				+ " critical points out of " + nTotal + " points");
		System.out.println("===============");
	}
}
