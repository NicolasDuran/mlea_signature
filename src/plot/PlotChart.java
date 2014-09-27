package plot;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Shape;
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
import org.jfree.util.ShapeUtilities;


public class PlotChart {


	private static XYSeriesCollection createDataset(ArrayList<Double> tab, XYSeriesCollection entry, String name) {
		XYSeriesCollection result = entry;
	    XYSeries series = new XYSeries(name);
	    for (int i = 0; i < tab.size(); i++) {
	        series.add(tab.get(i), new Double(0));
	    }
	    result.addSeries(series);
	    return result;
	}

	private static XYSeriesCollection createDataSet(double seuil, XYSeriesCollection entry, String name)
	{
		XYSeriesCollection result = entry;
	    XYSeries series = new XYSeries(name);
	    series.add(seuil, new Double(-1));
	    series.add(seuil, new Double(1));

	    result.addSeries(series);
	    return result;
	}

	//Plot les valeurs de 2 classes, sans seuil
	public static void Plot(String figureName, ArrayList<Double> tab1, ArrayList<Double> tab2)
	{
		XYSeriesCollection class1 = new XYSeriesCollection();
		XYSeriesCollection class2 = new XYSeriesCollection();

		class1 = createDataset(tab1, class1, "Intra");
		class2 = createDataset(tab2, class2, "Inter");

		JFreeChart chart = ChartFactory.createXYLineChart(
				figureName,
				"Distance",
				"",
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

		BufferedImage bImage1 = chart.createBufferedImage(800, 400);
		JLabel label1 = new JLabel();
		ImageIcon imageIcon1 = new ImageIcon(bImage1);
		label1.setIcon(imageIcon1);

		JFrame frame = new JFrame("Graph");
		frame.add(label1,BorderLayout.NORTH);

		frame.setSize(1000,700);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter()
		{
		@Override
		public void windowClosing(WindowEvent e)
		{
		System.exit(0);
		}
		});
	}


	//Plot les valeurs de deux classes, avec seuil
	public static void Plot(String figureName, ArrayList<Double> tab1, ArrayList<Double> tab2, double threshold) {

		XYSeriesCollection class1 = new XYSeriesCollection();
		XYSeriesCollection class2 = new XYSeriesCollection();
		XYSeriesCollection thresholdset = new XYSeriesCollection();

		class1 = createDataset(tab1, class1, "Intra");
		class2 = createDataset(tab2, class2, "Inter");
		thresholdset = createDataSet(threshold, thresholdset, "Threshold");

		JFreeChart chart = ChartFactory.createXYLineChart(
				figureName,
				"Distance",
				"",
				class1,
				PlotOrientation.VERTICAL,
				true,
				true,
				false
				);

		XYPlot xyplot = chart.getXYPlot();
		xyplot.setDataset(0, class1);
		xyplot.setDataset(1, class2);
		xyplot.setDataset(2,thresholdset);

		chart.setBackgroundPaint(Color.WHITE);

		Shape cross = ShapeUtilities.createDiagonalCross(2, 0);


		XYLineAndShapeRenderer rr = new XYLineAndShapeRenderer();
		rr.setSeriesLinesVisible(0, false);
		rr.setPaint(Color.YELLOW);
		rr.setSeriesShape(0, cross);
		chart.getXYPlot().setRenderer(0,rr);


		XYLineAndShapeRenderer rr2 = new XYLineAndShapeRenderer();
		rr2.setSeriesLinesVisible(0, false);
		rr2.setPaint(Color.BLUE);
		rr2.setSeriesShape(0, cross);
		chart.getXYPlot().setRenderer(1,rr2);


		XYLineAndShapeRenderer rr3 = new XYLineAndShapeRenderer();
		rr3.setSeriesLinesVisible(0, true);
		rr3.setPaint(Color.RED);
		chart.getXYPlot().setRenderer(2,rr3);

		BufferedImage bImage1 = chart.createBufferedImage(800, 400);
		JLabel label1 = new JLabel();
		ImageIcon imageIcon1 = new ImageIcon(bImage1);
		label1.setIcon(imageIcon1);

		JFrame frame = new JFrame("Graph");
		frame.add(label1,BorderLayout.NORTH);

		frame.setSize(1000,700);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter()
		{
		@Override
		public void windowClosing(WindowEvent e)
		{
		System.exit(0);
		}
		});
	}


}
