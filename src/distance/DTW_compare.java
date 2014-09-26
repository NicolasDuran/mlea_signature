package distance;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;



import com.AbstractionTest;
import com.BandTest;
import com.DtwTest;
import com.FastDtwTest;

import features.LocalFeatureVector;



public class DTW_compare {

	static String DTW_RADIUS = "10";
	
	/**
	 * Format LocalFeatureVector to FastDTW data format & call the 4 FastDTW algo
	 * @param LocalFeatureVectors to compare
	 */
	public static void DTWDistance(LocalFeatureVector v1, LocalFeatureVector v2)
	{
		if (v1 == null || v2 == null || v1.size() == 0|| v2.size() == 0)
			return;
		int nbfeatures = Math.min(v1.size(), v2.size());
		int v1_size = v1.get(0).size();
		int v2_size = v2.get(0).size();

		//Not needed ?
		for (int i = 1; i < nbfeatures; i++)
			v1_size = Math.min(v1_size, v1.get(i).size());
		for (int i = 1; i < nbfeatures; i++)
			v2_size = Math.min(v2_size, v2.get(i).size());
		
		try {
			PrintWriter v1out = new PrintWriter("log/v1.txt", "UTF-8");
			PrintWriter v2out = new PrintWriter("log/v2.txt", "UTF-8");
			

			for (int i = 0; i < v1_size; i++)
			{
				String s = String.valueOf(v1.get(0).get(i));
				
				for (int k = 1; k < nbfeatures; k++)
				{
					s += ",";
					s += String.valueOf(v1.get(k).get(i));
				}
				v1out.println(s);
			}
			for (int i = 0; i < v2_size; i++)
			{
				String s = String.valueOf(v2.get(0).get(i));
				
				for (int k = 1; k < nbfeatures; k++)
				{
					s += ",";
					s += String.valueOf(v2.get(k).get(i));
				}
				v2out.println(s);
			}
			v1out.close();
			v2out.close();
			
			AbstractionTest.main(new String[]{"log/v1.txt", "log/v2.txt", DTW_RADIUS});
			BandTest.main(new String[]{"log/v1.txt", "log/v2.txt", DTW_RADIUS});
			DtwTest.main(new String[]{"log/v1.txt", "log/v2.txt"});
			FastDtwTest.main(new String[]{"log/v1.txt", "log/v2.txt", DTW_RADIUS});
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ;
	}
}
