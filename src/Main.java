
public class Main
{
	public static void main(String[] args)
	{
		SignatureSystem syst = new SignatureSystem();

		String usage = "Usage:\n" +
				"\tinputfile outputfile : Compare signatures provided and store the results, using model file\n" +
				"\t--train genuineDatabase forgeryDatabase : Train program and produce a model file.\n" +
				"\t--perfs signatureDatabase : Measure program performances and store results in log/perfs.log file\n";

		if (args.length < 2) {
			System.err.println(usage);
			return;
		}

		// Train program and produce a model file for further testing
		if (args[0].equals("--train ")) {
			// TODO
		}
		// Measure performances of the program by generate several random training and testing
		else if (args[0].equals("--perfs")) {
			if (args.length < 2) {
				System.err.println(usage);
				return;
			}

			syst.measurePerformances(args[1]);
		}
		// Measure performances of the program by generate several random training and testing and plot distance classes
		else if (args[0].equals("--plotperfs")) {
			if (args.length < 2) {
				System.err.println(usage);
				return;
			}

			syst.plotMode = true;
			syst.measurePerformances(args[1]);
		}
		else if (args[0].equals("--fweights")) {
			if (args.length < 2) {
				System.err.println(usage);
				return;
			}

			double[] w = syst.findGlobalFeaturesWeightsUsingMean(args[1]);
			for (int i = 0; i < w.length; i++) {
				System.out.println("[" + i + "] = " + w[i]);
			}
		}
		// Compare signatures provided in the given file, and write the result in the given output filename
		else {
			syst.threshold = -395.93222763396045;
			syst.compareSignaturesFromFile(args[0], args[1]);
		}

		return;
	}
}
