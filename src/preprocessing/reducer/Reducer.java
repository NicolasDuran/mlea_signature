package preprocessing.reducer;

import common.Signature;

public class Reducer {
	public void reduce(Signature signature) {

		new SpeedReducer().apply(signature);
		//new BraultReducer().apply(signature);

		signature.getPoints().get(0).setCritical(true);
		signature.getPoints().get(signature.getPoints().size() - 1).setCritical(true);
		signature.deleteNotCritical();
	}
}
