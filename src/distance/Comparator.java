package distance;

import java.util.ArrayList;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import common.Signature;

import features.FeatureExtractor;
import features.GlobalFeatureVector;
import features.LocalFeatureVector;
import features.PCA;

public class Comparator {

	//Based on pseudo code found here :
	//http://en.wikipedia.org/w/index.php?title=Dynamic_time_warping&oldid=623046081
	public static double DTW(LocalFeatureVector v1, LocalFeatureVector v2)
	{
		//System.out.println("\n===== DTW =====");

		if (v1 == null || v2 == null || v1.size() == 0|| v2.size() == 0)
			return -1;

		int nbfeatures = Math.min(v1.size(), v2.size());
		int v1_size = v1.get(0).size();
		int v2_size = v2.get(0).size();
		EuclideanDistance edc = new EuclideanDistance();

		// In case features have not the same number of points
		for (int i = 1; i < nbfeatures; i++)
			v1_size = Math.min(v1_size, v1.get(i).size());
		for (int i = 1; i < nbfeatures; i++)
			v2_size = Math.min(v2_size, v2.get(i).size());

		double DTW[][] = new double[v1_size+1][v2_size+1];

	    for (int i = 1; i <= v1_size;  i++)
	        DTW[i][0] = Double.MAX_VALUE;
	    for (int i = 1; i <= v2_size; i++)
	        DTW[0][i] = Double.MAX_VALUE;
	    DTW[0][0] = 0;

	    for (int i = 1; i <= v1_size; i++) {
	    	for (int j = 1; j <= v2_size; j++)
	    	{
	    		double[] v1tab = new double[nbfeatures];
	    		double[] v2tab = new double[nbfeatures];
	    		for (int k = 0; k < nbfeatures; k++)
	    		{
	    			v1tab[k] = v1.get(k).get(i-1);
	    			v2tab[k] = v2.get(k).get(j-1);
	    		}

	    		double dist = edc.compute(v1tab, v2tab);

	            DTW[i][j] = dist + Math.min(
		            		Math.min(DTW[i-1][j], // insertion
		            				 DTW[i][j-1]), // deletion
		            				 DTW[i-1][j-1]); // match
	    	}
	    }

	    //System.out.println("\n     DTW_NAIVE : " + DTW[v1_size][v2_size]);
	    //System.out.println("===============");

	    return DTW[v1_size][v2_size];
	}

	public static double compareGlobalFeature(ArrayList<Double> v1, ArrayList<Double> v2)
	{
		int size = Math.min(v1.size(), v2.size()); // just in case
		double dist = 0.0;

		for (int i = 0; i < size; i++) {
			dist += Math.pow(v1.get(i) - v2.get(i), 2);
		}

		return Math.sqrt(dist);
	}


	/**
	 * Compare two preprocessed signatures
	 * @param s1 First signature
	 * @param s2 Second signature
	 * @return Returns the measured distance between those signature
	 */
	public static double compareSignatures(Signature s1, Signature s2)
	{
		LocalFeatureVector lv1 = FeatureExtractor.extractLocalFeature(s1);
		GlobalFeatureVector gv1 = FeatureExtractor.extractGlobalFeature(s1);
		LocalFeatureVector lv2 = FeatureExtractor.extractLocalFeature(s2);
		GlobalFeatureVector gv2 = FeatureExtractor.extractGlobalFeature(s2);

		double localDistance = Comparator.DTW(lv1, lv2);
		double globalDistance = Comparator.compareGlobalFeature(gv1, gv2);

		return  globalDistance;
	}

	/**
	 * Compare two preprocessed signatures
	 * @param s1 First signature
	 * @param s2 Second signature
	 * @param pca_vectors Vectors for a new space coordinates, computed using PCA, that will be
	 * applied on global features
	 * @return Returns the measured distance between those signature
	 */
	public static double compareSignatures(Signature s1, Signature s2, ArrayList<RealVector> pca_vectors)
	{
		LocalFeatureVector lv1 = FeatureExtractor.extractLocalFeature(s1);
		GlobalFeatureVector gv1 = FeatureExtractor.extractGlobalFeature(s1);
		LocalFeatureVector lv2 = FeatureExtractor.extractLocalFeature(s2);
		GlobalFeatureVector gv2 = FeatureExtractor.extractGlobalFeature(s2);

		// Change space coordinates
		ArrayList<Double> new_gv1 = PCA.computeNewCoordinates(gv1, pca_vectors);
		ArrayList<Double> new_gv2 = PCA.computeNewCoordinates(gv2, pca_vectors);

		double localDistance = Comparator.DTW(lv1, lv2);
		double globalDistance = Comparator.compareGlobalFeature(new_gv1, new_gv2);

		return  globalDistance;
	}

	/**
	 * Compare two preprocessed signatures and decide if they belong to the same user
	 * @param s1 First signature
	 * @param s2 Second signature
	 * @param localThreshold The threshold to use for local features to take decision
	 * @param globalThreshold The threshold to use for global features to take decision
	 * @return Returns the measured distance between those signature
	 * and wether they are from the same person or not
	 */
	public static CompareResult compareSignatures(Signature s1, Signature s2, double localThreshold, double globalThreshold)
	{
		CompareResult res = new CompareResult();
		res.distance = compareSignatures(s1, s2);
		res.decision = res.distance < localThreshold;

		return res;
	}

	/**
	 * Compare two preprocessed signatures and decide if they belong to the same user
	 * @param s1 First signature
	 * @param s2 Second signature
	 * @param pca_vectors Vectors for a new space coordinates, computed using PCA, that will be
	 * applied on global features
	 * @param localThreshold The threshold to use for local features to take decision
	 * @param globalThreshold The threshold to use for global features to take decision
	 * @return Returns the measured distance between those signature
	 * and wether they are from the same person or not
	 */
	public static CompareResult compareSignatures(Signature s1, Signature s2, ArrayList<RealVector> pca_vectors, double localThreshold, double globalThreshold)
	{
		CompareResult res = new CompareResult();
		res.distance = compareSignatures(s1, s2, pca_vectors);
		res.decision = res.distance < localThreshold;

		return res;
	}
}