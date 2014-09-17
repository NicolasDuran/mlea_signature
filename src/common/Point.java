package common;

public class Point {
	private double x;
	private double y;
	private long time;
	private boolean button;
	private int azimuth;
	private int altitude;
	private int pressure;

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
	}

	public int getAltitude() {
		return altitude;
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

	public boolean isButton() {
		return button;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}
}
