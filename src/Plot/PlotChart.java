package Plot;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class PlotChart {


	private static XYSeriesCollection createDataset(ArrayList<Double> tab, XYSeriesCollection entry, String name) {
		XYSeriesCollection result = entry;
	    XYSeries series = new XYSeries(name);
	    for (int i = 0; i < tab.size(); i++) {
	        series.add(tab.get(i),tab.get(i));
	    }
	    result.addSeries(series);
	    return result;
	}
	
	private static XYSeriesCollection createDataSet(double seuil, XYSeriesCollection entry, String name)
	{
		XYSeriesCollection result = entry;
	    XYSeries series = new XYSeries(name);
	    series.add(seuil,seuil);
	    result.addSeries(series);
	    return result;
	}
	
	//Plot les valeurs de 2 classes, sans seuil
	public static void Plot(ArrayList<Double> tab1, ArrayList<Double> tab2)
	{
		XYSeriesCollection class1 = new XYSeriesCollection();
		XYSeriesCollection class2 = new XYSeriesCollection();
		
		class1 = createDataset(tab1, class1, "classe 1");
		class2 = createDataset(tab2, class2, "classe 2");
		
		JFreeChart chart = ChartFactory.createXYLineChart( 
				"DTW chart",
				"value",
				"value", 
				class1, 
				PlotOrientation.VERTICAL, 
				true, 
				true,
				false 
				); 
		
		XYPlot xyplot = chart.getXYPlot(); 
		xyplot.setDataset(0, class1);
		xyplot.setDataset(1, class2);
		chart.setBackgroundPaint(Color.white);
		
		XYLineAndShapeRenderer rr = new XYLineAndShapeRenderer(); 
		rr.setSeriesLinesVisible(0, false); 
		rr.setPaint(Color.GREEN); 
		chart.getXYPlot().setRenderer(0,rr); 
		
		XYLineAndShapeRenderer rr2 = new XYLineAndShapeRenderer(); 
		rr2.setSeriesLinesVisible(0, false);
		rr2.setPaint(Color.BLUE); 
		chart.getXYPlot().setRenderer(1,rr2); 
		
		BufferedImage bImage1 = (BufferedImage) chart.createBufferedImage(800, 400); 
		JLabel label1 = new JLabel(); 
		ImageIcon imageIcon1 = new ImageIcon(bImage1); 
		label1.setIcon(imageIcon1); 

		JFrame frame = new JFrame(" Graph"); 
		frame.add(label1,BorderLayout.NORTH); 

		frame.setSize(1000,700); 
		frame.setVisible(true); 
		frame.addWindowListener(new WindowAdapter() 
		{ 
		public void windowClosing(WindowEvent e) 
		{ 
		System.exit(0); 
		} 
		}); 
	}
	
	
	//Plot les valeurs de deux classes, avec seuil
	public static void Plot(ArrayList<Double> tab1, ArrayList<Double> tab2, double seuil) {
		 
		XYSeriesCollection class1 = new XYSeriesCollection();
		XYSeriesCollection class2 = new XYSeriesCollection();
		XYSeriesCollection seuilset = new XYSeriesCollection();
		
		class1 = createDataset(tab1, class1, "classe 1");
		class2 = createDataset(tab2, class2, "classe 2");
		seuilset = createDataSet(seuil, seuilset, "seuil");
		
		JFreeChart chart = ChartFactory.createXYLineChart( 
				"DTW chart",
				"value",
				"value", 
				class1, 
				PlotOrientation.VERTICAL, 
				true, 
				true,
				false 
				); 
		
		XYPlot xyplot = chart.getXYPlot(); 
		xyplot.setDataset(0, class1);
		xyplot.setDataset(1, class2);
		xyplot.setDataset(2, seuilset);
		
		chart.setBackgroundPaint(Color.white);
		
		XYLineAndShapeRenderer rr = new XYLineAndShapeRenderer(); 
		rr.setSeriesLinesVisible(0, false); 
		rr.setPaint(Color.GREEN); 
		chart.getXYPlot().setRenderer(0,rr); 
		
		XYLineAndShapeRenderer rr2 = new XYLineAndShapeRenderer(); 
		rr2.setSeriesLinesVisible(0, false);
		rr2.setPaint(Color.BLUE); 
		chart.getXYPlot().setRenderer(1,rr2); 
		
		
		XYLineAndShapeRenderer rr3 = new XYLineAndShapeRenderer();
		rr3.setSeriesLinesVisible(0, false); 
		rr3.setPaint(Color.RED); 
		chart.getXYPlot().setRenderer(2,rr3);
		
		BufferedImage bImage1 = (BufferedImage) chart.createBufferedImage(800, 400); 
		JLabel label1 = new JLabel(); 
		ImageIcon imageIcon1 = new ImageIcon(bImage1); 
		label1.setIcon(imageIcon1); 

		JFrame frame = new JFrame(" Graph"); 
		frame.add(label1,BorderLayout.NORTH); 

		frame.setSize(1000,700); 
		frame.setVisible(true); 
		frame.addWindowListener(new WindowAdapter() 
		{ 
		public void windowClosing(WindowEvent e) 
		{ 
		System.exit(0); 
		} 
		}); 
	}
	

}
