package features;

public enum LocalFeature
{
	TIME_INDEX(0),
	POS_X(1),
	POS_Y(2),
	POS_DX(3),
	POS_DY(4),
	ABS_POS_DX(5),
	ABS_POS_DY(6),
	COS_ALPHA(7),
	SIN_ALPHA(8),
	CURVATURE(9),
	VX(10),
	VY(11),
	AX(12),
	AY(13),
	CRITICAL_VX(14),
	CRITICAL_VY(15),
	CRITICAL_AX(16),
	CRITICAL_AY(17),
	PRESSURE(18);

	/**
	 * Index in local feature vector
	 */
	int index;

	private LocalFeature(int i) {
		this.index = i;
	}
}
