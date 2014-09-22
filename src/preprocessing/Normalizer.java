package preprocessing;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import common.Point;
import common.Signature;

public class Normalizer {

	/**
	 * Compute main signature specifications for debugging.
	 * @param signature The signature to study.
	 * @return A string containing the minimum, maximum an mean values for X and Y
	 * and the mean values.
	 */
	private String infos(Signature signature) {
		int n = signature.getPoints().size();
		double meanX = 0;
		double meanY = 0;
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;

		for (Point point : signature.getPoints()) {
			meanX += point.getX();
			meanY += point.getY();
			if (point.getX() < minX)
				minX = point.getX();
			if (point.getX() > maxX)
				maxX = point.getX();
			if (point.getY() < minY)
				minY = point.getY();
			if (point.getY() > maxY)
				maxY = point.getY();
		}

		meanX /= n;
		meanY /= n;

		return "Signature infos{" + "meanX=" + meanX + ", meanY=" + meanY
				+ ", minX=" + minX + ", maxX=" + maxX + ", minY=" + minY
				+ "maxY=" + maxY + "}";
	}

	/**
	 * Normalize the signature using rotation, homothetie and translation.
	 * Also reduce the number of points by keeping speed local minimum.
	 * @param signature The signature to normalize.
	 */
	public void normalize(Signature signature) {
		rotate(signature);
		resize(signature, 100);
		translateToCenter(signature);
	}

	/**
	 * Resize the signature. The largest side will be reduced to the size
	 * specified.
	 * @param signature The signature to resize.
	 * @param size New signature size.
	 */
	private void resize(Signature signature, int size) {
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;

		for (Point point : signature.getPoints()) {
			if (point.getX() < minX)
				minX = point.getX();
			if (point.getX() > maxX)
				maxX = point.getX();
			if (point.getY() < minY)
				minY = point.getY();
			if (point.getY() > maxY)
				maxY = point.getY();
		}

		double sizeX = maxX - minX;
		double sizeY = maxY - minY;
		double ratio = sizeX > sizeY ? size / sizeX : size / sizeY;

		for (Point point : signature.getPoints())
			point.homothety(ratio);
	}

	/**
	 * Rotate the signature to flatten the main axis, computed using the least
	 * squares method.
	 * @param signature The signature to rotate.
	 */
	private void rotate(Signature signature) {
		SimpleRegression regression = new SimpleRegression(true);

		for (Point point : signature.getPoints()) {
			regression.addData(point.getX(), point.getY());
		}

		double slope = regression.getSlope();

		double hypothenus = Math.sqrt(slope * slope + 1);
		double cos = 1 / hypothenus;
		double sin = slope / hypothenus;

		for (Point point : signature.getPoints()) {
			point.rotate(cos, sin);
		}
	}

	/**
	 * Translate the signature to minimize coordinates keeping them positive.
	 * @param signature The signature to translate.
	 */
	private void translateToCenter(Signature signature) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MIN_VALUE;

		for (Point point : signature.getPoints()) {
			if (point.getX() < minX)
				minX = point.getX();
			if (point.getY() < minY)
				minY = point.getY();
		}

		for (Point point : signature.getPoints())
			point.translate(-minX, -minY);
	}
}
