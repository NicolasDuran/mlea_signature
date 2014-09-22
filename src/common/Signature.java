package common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Signature {
	protected ArrayList<Point> points = new ArrayList<Point>();

	public Signature(ArrayList<Point> points) {
		this.points = points;
	}

	public Signature(String path) throws SignatureException {
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(path);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String line = br.readLine();

			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");

				if (tokens.length != 7) {
					throw new SignatureException("Incorrect format");
				} else {
					points.add(new Point(Double.parseDouble(tokens[0]),
							Double.parseDouble(tokens[1]),
							Long.parseLong(tokens[2]),
							Boolean.parseBoolean(tokens[3]),
							Integer.parseInt(tokens[4]),
							Integer.parseInt(tokens[5]),
							Integer.parseInt(tokens[6])));
				}
			}
			in.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public BufferedImage toImage() {
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;

		for (Point point : points) {
			if (point.getX() < minX)
				minX = point.getX();
			if (point.getX() > maxX)
				maxX = point.getX();
			if (point.getY() < minY)
				minY = point.getY();
			if (point.getY() > maxY)
				maxY = point.getY();
		}

		int width = (int) (maxX - minX) + 1;
		int height = (int) (maxY - minY) + 1;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				image.setRGB(w, h, Color.black.getRGB());
			}
		}
		for (Point point : points)
			image.setRGB((int)(point.getX() - minX), (int)(point.getY() - minY), Color.white.getRGB());

		return image;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Signature{");
		builder.append(System.getProperty("line.separator"));
		for (Point point : points) {
			builder.append("\t");
			builder.append(point.toString());
			builder.append(System.getProperty("line.separator"));
		}
		builder.append("}");

		return builder.toString();
	}
}
