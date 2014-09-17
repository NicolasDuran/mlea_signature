package common;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Signature {
	private ArrayList<Point> points = new ArrayList<Point>();

	public Signature(String filename) throws SignatureException {
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(filename);

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
}
