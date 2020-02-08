package main.view.frames;

import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import main.dto.GeneratedReportData;
import main.utils.ColorUtils;
import main.view.AbstractFrame;
import main.view.dto.Chart2DConfigData;
import main.view.dto.Color;
import main.view.dto.RegressionData;
import main.view.frames.listeners.TextRegressionListener;
import main.view.service.Chart2DRenderer;
import main.view.service.dto.SeriesData;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.omg.PortableServer.POA;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

public class Chart2DFrame extends AbstractFrame {

    private GeneratedReportData report;
    private Chart2DConfigData configData;
    private GenerateReportFrame back;

    private RegressionData regression;
    private ChartPanel chartPanel;

    public Chart2DFrame(GeneratedReportData report, Chart2DConfigData configData, GenerateReportFrame back){
        this.report = report;
        this.configData = configData;
        this.back = back;
    }

    @Override
    public void createPanel(JPanel panel) {
        addReturnButton("Cofnij", this.back);
        SeriesData seriesData = createDataSet();
        calculateRegression(seriesData);

        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(this.regression.getRegressionSeries());
        collection.addSeries(seriesData.getSeries());

        JFreeChart chart = ChartFactory.createScatterPlot("Chart",
                configData.getXColumn(),
                configData.getYColumn(),
                collection,
                PlotOrientation.VERTICAL,
                true, true, false);


        if(this.configData.getZColumn() != null)
            ((XYPlot) chart.getPlot()).setRenderer(new Chart2DRenderer(
                    this.configData.getMinColor().getColor(),
                    this.configData.getMaxColor().getColor(),
                    seriesData));

        ((XYPlot) chart.getPlot()).getRenderer().setSeriesShape(1, getPointShape());
        ((XYPlot) chart.getPlot()).getRenderer().setSeriesShape(0, getRegressionShape());
        this.chartPanel = new ChartPanel(chart);
        setBounds(chartPanel, 0.25f, 0.04f, 900, 900);

        regressionInformations();
    }

    private void regressionInformations() {
        JTextField slope = new JTextField(Double.toString(this.regression.getRegression().getSlope()));
        JTextField intercept =  new JTextField(Double.toString(this.regression.getRegression().getIntercept()));

        JTextArea slopeText = new JTextArea("Ax");
        JTextArea interceptText = new JTextArea("B");
        interceptText.getDocument().addDocumentListener(new TextRegressionListener(this.regression));


        FramePosition position = setBounds(slope, 0.78f, 0.15f);
        position.right(slopeText, 30, 30);
        position.under(intercept).right(interceptText, 30, 30);

        ColorUtils.setTextBaldComponent(slopeText);
        ColorUtils.setTextBaldComponent(interceptText);
    }

    private void calculateRegression(SeriesData seriesData) {
        this.regression = new RegressionData();
        this.regression.getRegression().addData(seriesData.getData());
        this.regression.getRegression().regress();
        XYSeries regressionSeries = new XYSeries("Regression");

        double leftX = seriesData.getSeries().getMinX();
        double leftY = this.regression.getRegression().predict(seriesData.getSeries().getMinX());
        double rightX = seriesData.getSeries().getMaxX();
        double rightY = this.regression.getRegression().predict(seriesData.getSeries().getMaxX());

        int number = 250;
        for(int i = 0; i<number; i++){
            double x = (rightX - leftX)*((double)i/number) + leftX;
            double y = this.regression.getRegression().predict(x);
            regressionSeries.add(x, y);
        }

        regressionSeries.add(leftX, leftY);
        regressionSeries.add(rightX, rightY);

        this.regression.setLeft(new Point2D(leftX, leftY));
        this.regression.setRight(new Point2D(rightX, rightY));
        this.regression.setRegressionSeries(regressionSeries);
    }

    private Shape getRegressionShape() {
        return new Ellipse2D.Double(0,0,4,4);
    }

    private Shape getPointShape() {
        return new Ellipse2D.Double(0,0,2,2);
    }

    private SeriesData createDataSet(){
        double maxZ = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE;
        double[][] data = new double[this.report.getReport().size()][2];

        XYSeries series = new XYSeries(this.configData.getZColumn() != null ? this.configData.getZColumn() : "Values");
        HashMap<Point2D, Double> zMap = new HashMap<>();

        int i = 0;
        for (Map<String, String> rows : this.report.getReport()){
            double x = Double.valueOf(rows.get(this.configData.getXColumn()));
            double y = Double.valueOf(rows.get(this.configData.getYColumn()));
            series.add(x, y);
            data[i][0] = x;
            data[i][1] = y;

            if(this.configData.getZColumn() != null) {
                double z = Double.valueOf(rows.get(this.configData.getZColumn()));
                zMap.put(new Point2D(x, y), z);

                if (z > maxZ)
                    maxZ = z;
                if (z < minZ)
                    minZ = z;
            }
            i++;
        }

        if(configData.getZColumn() != null) {
            minZ = this.configData.getMinValue() != -1 ? this.configData.getMinValue() : minZ;
            maxZ = this.configData.getMaxValue() != -1 ? this.configData.getMaxValue() : maxZ;
        }
        return new SeriesData(series, minZ, maxZ, data, zMap);
    }
}
