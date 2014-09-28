package features;

/* Pour plus d'information sur les features, voir le GoogleDoc MLEA_Signature */
public enum LocalFeature
{
	TIME_INDEX(0), // size points
	POS_X(1), // size points
	POS_Y(2), // size points
	POS_DX(3), // size - 1 points
	POS_DY(4), // size - 1 points
	ABS_POS_DX(5), // size - 1 points
	ABS_POS_DY(6), // size - 1 points
	COS_ALPHA(7), // size - 1 points
	SIN_ALPHA(8), // size - 1 points
	CURVATURE(9), // size - 2 points
	VX(10), // size - 1 points
	VY(11), // size - 1 points
	AX(12), // size - 1 points
	AY(13), // size - 1 points
	//	CRITICAL_VX(14), // Pour l'instant c'est la meme de VX
	//	CRITICAL_VY(15), // Pour l'instant c'est la meme de VY
	//	CRITICAL_AX(16), // Pour l'instant c'est la meme de AX
	//	CRITICAL_AY(17), // Pour l'instant c'est la meme de AY
	PRESSURE(14); // size points

	/**
	 * Index in local feature vector
	 */
	int index;

	private LocalFeature(int i) {
		this.index = i;
	}
}
