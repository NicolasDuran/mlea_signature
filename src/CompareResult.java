
public class CompareResult {

	double distance;
	boolean decision;

	public CompareResult() {

	}

	public CompareResult(double distance, boolean decision) {
		this.distance = distance;
		this.decision = decision;
	}

	public String getDecision() {
		return (this.decision ? "T" : "F");
	}
}
