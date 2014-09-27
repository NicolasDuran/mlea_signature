package common;

public class Point {
	public static double distance(Point p1, Point p2) {
		return Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
	}

	public static double slope(Point p1, Point p2) {
		return (p1.y - p2.y) / (p1.x - p2.x);
	}
	private double x;
	private double y;
	private long time;
	private boolean button;
	private int azimuth;
	private int altitude;
	private int pressure;
	private boolean isCritical;
	private double angle;

	public static boolean samePosition(Point p1, Point p2) {
		return p1.x == p2.x && p1.y == p2.y;
	}

	public Point(double x, double y, long time, boolean button, int azimuth,
			int altitude, int pressure) {
		super();
		this.x = x;
		this.y = y;
		this.time = time;
		this.button = button;
		this.azimuth = azimuth;
		this.altitude = altitude;
		this.pressure = pressure;
		this.isCritical = false;
		this.angle = 0.;
	}

	public int getAltitude() {
		return altitude;
	}

	public double getAngle() {
		return this.angle;
	}

	public int getAzimuth() {
		return azimuth;
	}

	public int getPressure() {
		return pressure;
	}

	public long getTime() {
		return time;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void homothety(double ratio) {
		this.x *= ratio;
		this.y *= ratio;
	}

	public boolean isButton() {
		return button;
	}

	public boolean isCritical() {
		return isCritical;
	}

	public void rotate(double cos, double sin) {
		this.x = this.x * cos - this.y * sin;
		this.y = this.x * sin + this.y * cos;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public void setButton(boolean isButton) {
		this.button = isButton;
	}

	public void setCritical(boolean isCritical) {
		this.isCritical = isCritical;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Point{" + "x=" + x + ", y=" + y + ", time=" + time
				+ ", button=" + button + ", azimuth=" + azimuth+ ", altitude="
				+ altitude + ", pressure=" + pressure + "}";
	}

	public void translate(double meanX, double meanY) {
		this.x += meanX;
		this.y += meanY;
	}
}
