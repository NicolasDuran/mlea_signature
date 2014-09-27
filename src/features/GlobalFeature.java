package features;

/* La majorite de ces features proviennent de l'Etat de l'Art */
public enum GlobalFeature
{
	TOTAL_LENGTH(0),
	DURATION(1),
	START_END_DISTANCE(2),
	VX_MEAN(3),
	VY_MEAN(4),
	V_MEAN(5),
	V_MAX(6),
	AX_MEAN(7),
	AY_MEAN(8),
	A_MEAN(9),
	A_MAX(10),
	ANGLE_SUM(11),
	XY_RELATION(12);

	/**
	 * Index in local feature vector
	 */
	int index;

	private GlobalFeature(int i) {
		this.index = i;
	}
}
