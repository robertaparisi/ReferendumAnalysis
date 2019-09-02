/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import indexing.TweetIndex;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Roberta
 */
public class TimeSeriesPlot extends JFrame{
    private static final long serialVersionUID = 1L;

    public TimeSeriesPlot(String title, String chartTitle, XYDataset dataset, String fileName, int n) throws IOException {
        super(title);

        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                chartTitle,
                "Dates (Grain = 12h)", "Tweets Frequencies",
                dataset,
                true, true, true);

        //Changes background color
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.DARK_GRAY);
        plot.getRenderer().setSeriesPaint(0, Color.ORANGE);
        plot.getRenderer().setSeriesPaint(1, Color.RED);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.black);
        plot.setRangeGridlinePaint(Color.black);
//        plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));

        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            plot.getRenderer().setSeriesStroke(i, new BasicStroke(2));
        }

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //rangeAxis.setTickUnit(new NumberTickUnit(n));

        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("Verdana", Font.BOLD, 14));
        domainAxis.setTickLabelFont(new Font("Verdana", Font.PLAIN, 12));
        domainAxis.setTickLabelsVisible(true);
        domainAxis.setTickMarksVisible(false);
        domainAxis.setLabelAngle(0);
        domainAxis.setVerticalTickLabels(false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1200, 900));
        setContentPane(chartPanel);

        ChartUtilities.saveChartAsPNG(new File(fileName), chart, 800, 600);
    
    }
     public static void create_timeseries_plot () throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {
//        output_data_directory
        String yes_index_path = TweetIndex.output_data_directory+ "yes_politicians_index";
        String no_index_path = TweetIndex.output_data_directory+ "no_politicians_index";
        
        long grain = TimeUnit.HOURS.toMillis(12); //12 hours of granularity in milliseconds
       
        TemporalAnalysis temp_anal_yes = new TemporalAnalysis(yes_index_path);
        TemporalAnalysis temp_anal_no = new TemporalAnalysis(no_index_path);
        
        ArrayList<Long> time_interval_yes = TemporalAnalysis.setGrain(grain);
        ArrayList<Long> time_interval_no = TemporalAnalysis.setGrain(grain);

        TimeSeriesCollection collection = new TimeSeriesCollection();
        collection = temp_anal_yes.mergeCoordinates(collection, temp_anal_yes.frequencies_value(time_interval_yes), "YES tweets", time_interval_yes);
        collection = temp_anal_no.mergeCoordinates(collection, temp_anal_no.frequencies_value(time_interval_no), "NO tweets", time_interval_no);

        TimeSeriesPlot plot = new TimeSeriesPlot("tweeting_frequencies", "Tweeting Frequencies \nTime Series", collection , TweetIndex.output_data_directory + "/TimeSeriesPlotYN.jpg", 200);
//        plot.pack();
//        plot.setLocation(800, 20);
//        plot.setVisible(true);// controlla
        
    }
}
