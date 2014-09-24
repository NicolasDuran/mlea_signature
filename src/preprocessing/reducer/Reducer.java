package preprocessing.reducer;

import common.Signature;

public class Reducer {
	public void reduce(Signature signature) {
		System.out.println("=== Reducer ===");

		new SpeedReducer().apply(signature);
		new BraultReducer().apply(signature);
		signature.getPoints().get(0).setCritical(true);
		signature.getPoints().get(signature.getPoints().size() - 1).setCritical(true);

		System.out.println("Total: " + signature.getCriticalPoints().size()
				+ " critical points out of " + signature.getPoints().size() + " points");
		System.out.println("===============");
	}
}
