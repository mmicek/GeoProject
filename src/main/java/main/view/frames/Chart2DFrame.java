package main.view.frames;

import javafx.geometry.Point2D;
import main.dto.GeneratedReportData;
import main.utils.ColorUtils;
import main.view.AbstractFrame;
import main.view.dto.Chart2DConfigData;
import main.view.dto.CustomRegression;
import main.view.dto.RegressionData;
import main.view.service.Chart2DRenderer;
import main.view.service.dto.SeriesData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
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
        if(configData.isCalculateRegression())
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
                    configData.isCalculateRegression(),
                    this.configData.getMinColor().getColor(),
                    this.configData.getMaxColor().getColor(),
                    seriesData));

        if(configData.isCalculateRegression()) {
            ((XYPlot) chart.getPlot()).getRenderer().setSeriesShape(1, getPointShape());
            ((XYPlot) chart.getPlot()).getRenderer().setSeriesShape(0, getRegressionShape());
        } else
            ((XYPlot) chart.getPlot()).getRenderer().setSeriesShape(0, getPointShape());
        this.chartPanel = new ChartPanel(chart);
        setBounds(chartPanel, 0.25f, 0.04f, 900, 900);

        if(configData.isCalculateRegression())
            regressionInformations(seriesData);
    }

    private void regressionInformations(SeriesData seriesData) {
        JTextField slope = new JTextField(Double.toString(this.regression.getRegression().getSlope()));
        JTextField intercept =  new JTextField(Double.toString(this.regression.getRegression().getIntercept()));

        JTextArea slopeText = new JTextArea("Ax");
        JTextArea interceptText = new JTextArea("B");
        JButton changeRegression = new JButton("Zmień");

        FramePosition position = setBounds(slope, 0.78f, 0.15f);
        position.right(slopeText, 30, 30);
        position = position.under(intercept);
        position.right(interceptText, 30, 30);
        position = position.under(changeRegression);

        changeRegression.addActionListener(e -> {
            regression.setCustomSlope(Double.valueOf(slope.getText()));
            regression.setCustomIntercept(Double.valueOf(intercept.getText()));
            double leftX = seriesData.getSeries().getMinX();
            double rightX = seriesData.getSeries().getMaxX();
            deleteRegressionSeries();
            this.regression.setRegression(new CustomRegression(
                    this.regression.getCustomSlope(),
                    this.regression.getCustomIntercept()
            ));
            createRegressionLine(this.regression.getRegressionSeries(),leftX, rightX);
        });

        ColorUtils.setTextBaldComponent(slopeText);
        ColorUtils.setTextBaldComponent(interceptText);
    }

    private void deleteRegressionSeries() {
        int size = this.regression.getRegressionSeries().getItemCount();
        for(int i=0; i<size; i++) {
            this.regression.getRegressionSeries().remove(0);
        }
    }

    private void calculateRegression(SeriesData seriesData) {
        this.regression = new RegressionData();
        this.regression.getRegression().addData(seriesData.getData());
        this.regression.getRegression().regress();
        XYSeries regressionSeries = new XYSeries("Regression");

        double leftX = seriesData.getSeries().getMinX();
        double rightX = seriesData.getSeries().getMaxX();
        createRegressionLine(regressionSeries, leftX, rightX);
        this.regression.setRegressionSeries(regressionSeries);

        this.regression.setCustomSlope(this.regression.getRegression().getSlope());
        this.regression.setCustomIntercept(this.regression.getRegression().getIntercept());
    }

    private void createRegressionLine(XYSeries series,double leftX, double rightX){
        int number = 250;
        for(int i = 0; i<number; i++){
            double x = (rightX - leftX)*((double)i/number) + leftX;
            double y = this.regression.getRegression().predict(x);
            series.add(x, y);
        }
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
