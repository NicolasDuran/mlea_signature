package distance;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

import features.LocalFeatureVector;


//Based on pseudo code found here :
//http://en.wikipedia.org/w/index.php?title=Dynamic_time_warping&oldid=623046081
public class DTW_naive {

	public static double DTWDistance(LocalFeatureVector v1, LocalFeatureVector v2)
	{
		//System.out.println("\n===== DTW =====");
		if (v1 == null || v2 == null || v1.size() == 0|| v2.size() == 0)
			return -1;

		int nbfeatures = Math.min(v1.size(), v2.size());
		int v1_size = v1.get(0).size();
		int v2_size = v2.get(0).size();
		EuclideanDistance edc = new EuclideanDistance();

		//Not needed ?
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

}