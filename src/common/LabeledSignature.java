package common;

public class LabeledSignature extends Signature
{
	private int userID;
	private boolean genuine;

	public LabeledSignature(Signature s, int userID, boolean genuine) {
		super(s);
		this.userID = userID;
		this.genuine = genuine;
	}

	public LabeledSignature(String path, int userID, boolean genuine) throws SignatureException {
		super(path);
		this.userID = userID;
		this.genuine = genuine;
	}

	private String getFilename(String path)
	{
		String filename = "";

		for (int i = path.length() - 1; i >= 0; i++) {
			if (path.charAt(i) == '/') {
				break;
			}

			filename = path.charAt(i) + filename;
		}

		return filename;
	}

	/**
	 * Label automatically the signature according to the file name : USERX_N.txt
	 * @param path Path of the signature file
	 * @throws SignatureException
	 */
	public LabeledSignature(String path) throws SignatureException {
		super(path);

		// Get user ID
		String filename = getFilename(path);
		this.userID = filename.charAt(4) - 48;

		if (this.userID < 0 || this.userID > 5) {
			throw new SignatureException("Malformed signature filename (UserID)");
		}

		// Get sample ID
		String sampleStringID = "" + filename.charAt(6);
		if (filename.charAt(7) != '.') {
			sampleStringID += filename.charAt(7);
		}

		int sampleID;
		try {
			sampleID = Integer.parseInt(sampleStringID);
		} catch (NumberFormatException e) {
			throw new SignatureException("Malformed signature filename (SampleID)");
		}

		this.genuine = sampleID <= 20;
	}

	public int getUserID() {
		return userID;
	}

	public boolean isGenuine() {
		return genuine;
	}
}
