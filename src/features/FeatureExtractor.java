package features;

import java.util.ArrayList;
import java.util.Collections;

import common.Point;
import common.Signature;

public class FeatureExtractor {

	public static final int numberOfGlobalFeatures = 13;
	public static final int numberOfLocalFeatures = 15;

	public static GlobalFeatureVector computeGlobalFeatures(Signature s)
	{
		GlobalFeatureVector globVect = new GlobalFeatureVector();
		double totalLength = 0;
		double vxmoy = 0;
		double vymoy = 0;
		double vmoy = 0;
		double axmoy = 0;
		double aymoy = 0;
		double amoy = 0;
		double vmax = 0;
		double amax = 0;
		double angleSum = 0;
		double dxSum = 0;
		double dySum = 0;

		Point startPoint = s.getPoints().get(0);
		Point endPoint = s.getPoints().get(s.getPoints().size() - 1);
		double startEndDist = Math.sqrt(Math.pow(startPoint.getX() - endPoint.getX(), 2) + Math.pow(startPoint.getY() - endPoint.getY(), 2));
		double totalTime = endPoint.getTime() - startPoint.getTime();

		int i = 0;
		for (Point p : s.getPoints())
		{
			if (i < s.getPoints().size() - 1)
			{
				double dx = s.getPoints().get(i + 1).getX() - p.getX();
				double dy = s.getPoints().get(i + 1).getY() - p.getY();
				double eucliDist = Math.sqrt(dx * dx + dy * dy);

				double dt = s.getPoints().get(i + 1).getTime() - p.getTime();
				double dvx = 0;
				double dvy = 0;
				double dv = 0;
				double dax = 0;
				double day = 0;
				double da = 0;

				// Just in case, but shouldn't happen
				if (dt != 0) {
					dvx = dx / dt;
					dvy = dy / dt;
					dv = eucliDist / dt;
					dax = dvx / dt;
					day = dvy / dt;
					da = dv / dt;
				}

				dxSum += dx;
				dySum += dy;

				vxmoy += dvx;
				vymoy += dvy;
				vmoy += dv;
				axmoy += dax;
				aymoy += day;
				amoy += da;

				if (dv > vmax) {
					vmax = dv;
				}
				if (da > amax) {
					amax = da;
				}

				if (dx != 0)
					angleSum += Math.atan(dy / dx);
				else
					angleSum += Math.PI / 2;

				totalLength += eucliDist;
			}

			i++;
		}

		vxmoy /= s.getPoints().size() - 1;
		vymoy /= s.getPoints().size() - 1;
		vmoy /= s.getPoints().size() - 1;
		axmoy /= s.getPoints().size() - 1;
		aymoy /= s.getPoints().size() - 1;
		amoy /= s.getPoints().size() - 1;

		globVect.add(totalLength);
		globVect.add(totalTime);
		globVect.add(startEndDist);
		globVect.add(vxmoy);
		globVect.add(vymoy);
		globVect.add(vmoy);
		globVect.add(vmax);
		globVect.add(axmoy);
		globVect.add(aymoy);
		globVect.add(amoy);
		globVect.add(amax);
		globVect.add(angleSum);
		globVect.add(dxSum / dySum);

		return globVect;
	}

	public static LocalFeatureVector computeLocalFeatures(Signature s)
	{
		ArrayList<Double> pressure = new ArrayList<Double>();
		ArrayList<Double> azimuth = new ArrayList<Double>();
		ArrayList<Double> times = new ArrayList<Double>();
		ArrayList<Double> posx = new ArrayList<Double>();
		ArrayList<Double> posy = new ArrayList<Double>();
		ArrayList<Double> posdx = new ArrayList<Double>();
		ArrayList<Double> posdy = new ArrayList<Double>();
		ArrayList<Double> posabsdx = new ArrayList<Double>();
		ArrayList<Double> posabsdy = new ArrayList<Double>();
		ArrayList<Double> alphacos = new ArrayList<Double>();
		ArrayList<Double> alphasin = new ArrayList<Double>();
		ArrayList<Double> curvature = new ArrayList<Double>();

		ArrayList<Double> vx = new ArrayList<Double>();
		ArrayList<Double> vy = new ArrayList<Double>();
		ArrayList<Double> ax = new ArrayList<Double>();
		ArrayList<Double> ay = new ArrayList<Double>();
		//		ArrayList<Double> criticalvx = new ArrayList<Double>();
		//		ArrayList<Double> criticalvy = new ArrayList<Double>();
		//		ArrayList<Double> criticalax = new ArrayList<Double>();
		//		ArrayList<Double> criticalay = new ArrayList<Double>();


		int i = 0;
		Point previousCriticalPoint = null;
		for (Point p : s.getPoints())
		{
			// Keep some basic features
			times.add((new Long(p.getTime())).doubleValue());
			posx.add(p.getX());
			posy.add(p.getY());
			pressure.add((new Integer(p.getPressure())).doubleValue());
			azimuth.add((new Integer(p.getAzimuth())).doubleValue());

			// Compute speed between critical points
			if (p.isCritical()) {
				if (previousCriticalPoint != null) {
					double dt = p.getTime() - previousCriticalPoint.getTime();
					double cvx = 0;
					double cvy = 0;
					double cax = 0;
					double cay = 0;

					// Just in case, but shouldn't happen
					if (dt != 0) {
						cvx = (p.getX() - previousCriticalPoint.getX()) / dt;
						cvy = (p.getY() - previousCriticalPoint.getY()) / dt;
						cax = cvx / dt;
						cay = cvy / dt;
					}

					//					criticalvx.add(cvx);
					//					criticalvy.add(cvy);
					//					criticalax.add(cax);
					//					criticalay.add(cay);
				}

				previousCriticalPoint = p;
			}

			// Compute spatial and dynamique features
			if (i < s.getPoints().size() - 1)
			{
				double dx = s.getPoints().get(i + 1).getX() - p.getX();
				double dy = s.getPoints().get(i + 1).getY() - p.getY();
				double dt = s.getPoints().get(i + 1).getTime() - p.getTime();
				double dvx = 0;
				double dvy = 0;
				double dax = 0;
				double day = 0;

				// Just in case, but shouldn't happen
				if (dt != 0) {
					dvx = dx / dt;
					dvy = dy / dt;
					dax = dvx / dt;
					day = dvy / dt;
				}

				posdx.add(dx);
				posdy.add(dy);
				posabsdx.add(Math.abs(dx));
				posabsdy.add(Math.abs(dy));
				vx.add(dvx);
				vy.add(dvy);
				ax.add(dax);
				ay.add(day);

				double cosa = 1.0;
				double sina = 1.0;
				// Just in case, but shouldn't happen
				if (dx != 0 && dy != 0) {
					cosa = Math.abs(dx) / Math.sqrt(dx * dx + dy * dy);
					sina = Math.abs(dy) / Math.sqrt(dx * dx + dy * dy);
				}
				else {
					//System.err.println("[" + i + "][" + (i+1) + "] same points !");
				}

				alphacos.add(cosa);
				alphasin.add(sina);
			}

			// Compute curvature
			if (i > 1 && i < s.getPoints().size() - 2)
			{
				// Al Kashi : a*a = b*b + c*c - 2bc*cos(BAC)
				double b = Math.sqrt(Math.pow(p.getX() - s.getPoints().get(i - 2).getX(), 2) + Math.pow(p.getY() - s.getPoints().get(i - 2).getY(), 2));
				double c = Math.sqrt(Math.pow(s.getPoints().get(i - 2).getX() - s.getPoints().get(i + 2).getX(), 2) + Math.pow(s.getPoints().get(i - 2).getY() - s.getPoints().get(i + 2).getY(), 2));
				double a = Math.sqrt(Math.pow(p.getX() - s.getPoints().get(i - 2).getX(), 2) + Math.pow(p.getY() - s.getPoints().get(i - 2).getY(), 2));
				double cosa = 0;
				// Just in case, but shouldn't happen
				if (b != 0 && c != 0) {
					cosa = (b*b + c*c - a*a) / (2*b*c);
				}

				curvature.add(cosa);
			}

			i++;
		}

		LocalFeatureVector locVect = new LocalFeatureVector();
		locVect.add(times);
		locVect.add(posx);
		locVect.add(posy);
		locVect.add(posdx);
		locVect.add(posdy);
		locVect.add(posabsdx);
		locVect.add(posabsdy);
		locVect.add(alphacos);
		locVect.add(alphasin);
		locVect.add(curvature);
		locVect.add(vx);
		locVect.add(vy);
		locVect.add(ax);
		locVect.add(ay);
		//		locVect.add(criticalvx);
		//		locVect.add(criticalvy);
		//		locVect.add(criticalax);
		//		locVect.add(criticalay);
		locVect.add(pressure);
		locVect.add(azimuth);

		return locVect;
	}

	public static boolean selector[] = new boolean[16];

	public static GlobalFeatureVector extractGlobalFeature(Signature s)
	{
		GlobalFeatureVector features = computeGlobalFeatures(s);
		GlobalFeatureVector v = new GlobalFeatureVector();

		if (selector[0]) v.add(features.get(GlobalFeature.TOTAL_LENGTH.index));
		if (selector[1]) v.add(features.get(GlobalFeature.DURATION.index));
		if (selector[2]) v.add(features.get(GlobalFeature.START_END_DISTANCE.index));
		if (selector[3]) v.add(features.get(GlobalFeature.VX_MEAN.index));
		if (selector[4]) v.add(features.get(GlobalFeature.VY_MEAN.index));
		if (selector[5]) v.add(features.get(GlobalFeature.V_MEAN.index));
		if (selector[6]) v.add(features.get(GlobalFeature.V_MAX.index));
		if (selector[7]) v.add(features.get(GlobalFeature.AX_MEAN.index));
		if (selector[8]) v.add(features.get(GlobalFeature.AY_MEAN.index));
		if (selector[9]) v.add(features.get(GlobalFeature.A_MEAN.index));
		if (selector[10]) v.add(features.get(GlobalFeature.A_MAX.index));
		if (selector[11]) v.add(features.get(GlobalFeature.ANGLE_SUM.index));
		if (selector[12]) v.add(features.get(GlobalFeature.XY_RELATION.index));

		v.add(features.get(GlobalFeature.TOTAL_LENGTH.index));
		v.add(features.get(GlobalFeature.DURATION.index));
		v.add(features.get(GlobalFeature.START_END_DISTANCE.index));
		v.add(features.get(GlobalFeature.VX_MEAN.index));
		v.add(features.get(GlobalFeature.VY_MEAN.index));
		v.add(features.get(GlobalFeature.V_MEAN.index));
		v.add(features.get(GlobalFeature.V_MAX.index));
		v.add(features.get(GlobalFeature.AX_MEAN.index));
		v.add(features.get(GlobalFeature.AY_MEAN.index));
		v.add(features.get(GlobalFeature.A_MEAN.index));
		v.add(features.get(GlobalFeature.A_MAX.index));
		v.add(features.get(GlobalFeature.ANGLE_SUM.index));
		v.add(features.get(GlobalFeature.XY_RELATION.index));

		return v;
	}

	public static LocalFeatureVector extractLocalFeature(Signature s)
	{
		LocalFeatureVector features = computeLocalFeatures(s);
		LocalFeatureVector v = new LocalFeatureVector();

		//		if (selector[12]) v.add(features.get(LocalFeature.TIME_INDEX.index));
		//		if (selector[1]) v.add(features.get(LocalFeature.POS_X.index));
		//		if (selector[2]) v.add(features.get(LocalFeature.POS_Y.index));
		//		if (selector[3]) v.add(features.get(LocalFeature.POS_DX.index));
		//		if (selector[4]) v.add(features.get(LocalFeature.POS_DY.index));
		//		if (selector[13]) v.add(features.get(LocalFeature.ABS_POS_DX.index));
		//		if (selector[14]) v.add(features.get(LocalFeature.ABS_POS_DY.index));
		//		if (selector[7]) v.add(features.get(LocalFeature.COS_ALPHA.index));
		//		if (selector[8]) v.add(features.get(LocalFeature.SIN_ALPHA.index));
		//		if (selector[9]) v.add(features.get(LocalFeature.CURVATURE.index));
		//		if (selector[10]) v.add(features.get(LocalFeature.VX.index));
		//		if (selector[11]) v.add(features.get(LocalFeature.VY.index));
		//		if (selector[0]) v.add(features.get(LocalFeature.AX.index));
		//		if (selector[5]) v.add(features.get(LocalFeature.AY.index));
		//		if (selector[6]) v.add(features.get(LocalFeature.PRESSURE.index));
		//		if (selector[15]) v.add(features.get(LocalFeature.AZIMUTH.index));

		//v.add(features.get(LocalFeature.TIME_INDEX.index));
		v.add(features.get(LocalFeature.POS_X.index));
		//v.add(features.get(LocalFeature.POS_Y.index));
		v.add(features.get(LocalFeature.POS_DX.index));
		//v.add(features.get(LocalFeature.POS_DY.index));
		//v.add(features.get(LocalFeature.ABS_POS_DX.index));
		//v.add(features.get(LocalFeature.ABS_POS_DY.index));
		//v.add(features.get(LocalFeature.COS_ALPHA.index));
		v.add(features.get(LocalFeature.SIN_ALPHA.index));
		//v.add(features.get(LocalFeature.CURVATURE.index));
		v.add(features.get(LocalFeature.VX.index));
		//v.add(features.get(LocalFeature.VY.index));
		//v.add(features.get(LocalFeature.AX.index));
		v.add(features.get(LocalFeature.AY.index));
		//v.add(features.get(LocalFeature.PRESSURE.index));
		//v.add(features.get(LocalFeature.AZIMUTH.index));

		//normalize(v);

		return v;
	}

	public static void normalize(LocalFeatureVector v)
	{
		for (ArrayList<Double> f : v) {
			double max = Collections.max(f);
			for (int i = 0; i < f.size(); i++) {
				f.set(i, f.get(i) / max);
			}
		}
	}
}
