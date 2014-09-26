
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
		// Compare signatures provided in the given file, and write the result in the given output filename
		else {
			syst.forgeryThreshold = 389.64616403731014;
			syst.compareSignaturesFromFile(args[0], args[1]);
		}

		return;
	}
}
