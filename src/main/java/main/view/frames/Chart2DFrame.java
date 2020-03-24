package main.view.frames;

import javafx.geometry.Point2D;
import main.dto.GeneratedReportData;
import main.utils.ColorUtils;
import main.view.AbstractFrame;
import main.view.MainFrame;
import main.view.dto.*;
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
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.*;
import java.util.List;

public class Chart2DFrame extends AbstractFrame {

    private GeneratedReportData report;
    private Chart2DConfigData configData;
    private GenerateReportFrame back;

    private HashMap<Range,RegressionData> regression = new HashMap<>();
    private ChartPanel chartPanel;

    private List<Range> rangeList;
    private static RegressionData currentRegression;
    private static Integer currentIndex;

    public Chart2DFrame(GeneratedReportData report, Chart2DConfigData configData, GenerateReportFrame back){
        this.report = report;
        this.configData = configData;
        this.back = back;
        currentIndex = 0;
        rangeList = new LinkedList<>();
        rangeList.add(new Range(4000d, 4750d, "X"));
        rangeList.add(new Range(2750d, 4000d, "X"));
        rangeList.add(new Range(1000d, 2000d, "X"));
    }

    @Override
    public void createPanel(JPanel panel) {

        addReturnButton("Cofnij", this.back);
        SeriesData seriesData = createDataSet();
        calculateRegression(seriesData, rangeList);

        XYSeriesCollection collection = new XYSeriesCollection();
        if(configData.isCalculateRegression()) {
            for(RegressionData regressionData : regression.values())
                collection.addSeries(regressionData.getRegressionSeries());
        }
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
                    seriesData,
                    regression.size()));

        if(regression.size() != 0) {
            ((XYPlot) chart.getPlot()).getRenderer().setSeriesShape(regression.size(), getPointShape());
            ((XYPlot) chart.getPlot()).getRenderer().setSeriesPaint(regression.size(), getPointPaint());
            List<RegressionData> dataIterator = new LinkedList<>(this.regression.values());
            for(int i=0; i<regression.size(); i++) {
                main.view.dto.Color c = main.view.dto.Color.values()[i%main.view.dto.Color.values().length];
                ((XYPlot) chart.getPlot()).getRenderer().setSeriesPaint(i, c.getColor());
                dataIterator.get(i).setColor(c.getColor());
                ((XYPlot) chart.getPlot()).getRenderer().setSeriesShape(i, getRegressionShape());
            }
        } else {
            ((XYPlot) chart.getPlot()).getRenderer().setSeriesShape(0, getPointShape());
            ((XYPlot) chart.getPlot()).getRenderer().setSeriesPaint(0, getPointPaint());
        }
        this.chartPanel = new ChartPanel(chart);
        setBounds(chartPanel, 0.25f, 0.04f, 900, 900);

        regressionInformations();
    }

    private void regressionInformations() {

        List<RegressionData> dataIterator = new LinkedList<>(this.regression.values());
        if(dataIterator.isEmpty())
            return;

        currentRegression = dataIterator.get(currentIndex);
        JTextField slope = new JTextField(Double.toString(currentRegression.getRegression().getSlope()));
        JTextField intercept =  new JTextField(Double.toString(currentRegression.getRegression().getIntercept()));

        JButton left = new JButton("<");
        JButton right = new JButton(">");
        JTextArea description = new JTextArea("Regresja");
        description.setForeground((Color) currentRegression.getColor());

        JTextArea slopeText = new JTextArea("Ax");
        JButton leftSlope = new JButton("<");
        JButton rightSlope = new JButton(">");
        JTextField slopeJump = new JTextField("0");

        JTextArea interceptText = new JTextArea("B");
        JButton leftIntercept = new JButton("<");
        JButton rightIntercept = new JButton(">");
        JTextField interceptJump = new JTextField("0");
        JButton changeRegression = new JButton("Zmień");

        FramePosition position = setBounds(description, 0.74f, 0.15f);
        position = position.under(left,70,30);
        position.right(right, 70, 30);
        position = position.under(slope, 150, 30);
        position.right(slopeText, 30, 30).right(leftSlope,50, 30)
                .right(rightSlope).right(slopeJump);
        position = position.under(intercept);
        position.right(interceptText, 30, 30).right(leftIntercept,50, 30)
                .right(rightIntercept).right(interceptJump);
        position.under(changeRegression);

        changeRegression.addActionListener(e -> {
            recalculateRegression(slope, intercept);
        });

        leftSlope.addActionListener(e -> {
            Double difference = Double.valueOf(slopeJump.getText());
            slope.setText(Double.toString(Double.valueOf(slope.getText()) - difference));
        });
        rightSlope.addActionListener(e -> {
            Double difference = Double.valueOf(slopeJump.getText());
            slope.setText(Double.toString(Double.valueOf(slope.getText()) + difference));
        });
        leftIntercept.addActionListener(e -> {
            Double difference = Double.valueOf(interceptJump.getText());
            intercept.setText(Double.toString(Double.valueOf(intercept.getText()) - difference));
        });
        rightIntercept.addActionListener(e -> {
            Double difference = Double.valueOf(interceptJump.getText());
            intercept.setText(Double.toString(Double.valueOf(intercept.getText()) + difference));
        });

        right.addActionListener(e -> {
            if(regression.size() > currentIndex+1)
                currentIndex++;
            setRegressionValues(dataIterator, slope, intercept, description);
        });
        left.addActionListener(e -> {
            if(currentIndex > 0)
                currentIndex--;
            setRegressionValues(dataIterator, slope, intercept, description);
        });

        ColorUtils.setTextBaldComponent(slopeText);
        ColorUtils.setTextBaldComponent(interceptText);
        ColorUtils.setTextBaldComponent(description);
    }

    private void recalculateRegression(JTextField slope, JTextField intercept) {
        currentRegression.setCustomSlope(Double.valueOf(slope.getText()));
        currentRegression.setCustomIntercept(Double.valueOf(intercept.getText()));
        double leftX = currentRegression.getRange().getFrom();
        double rightX = currentRegression.getRange().getTo();
        deleteRegressionSeries(currentRegression);
        currentRegression.setRegression(new CustomRegression(
                currentRegression.getCustomSlope(),
                currentRegression.getCustomIntercept()
        ));
        createRegressionLine(currentRegression, currentRegression.getRegressionSeries(), leftX, rightX);
    }

    private void setRegressionValues(List<RegressionData> data, JTextField slope, JTextField intercept, JTextArea description){
        currentRegression = data.get(currentIndex);
        description.setForeground((Color) currentRegression.getColor());
        slope.setText(Double.toString(currentRegression.getRegression().getSlope()));
        intercept.setText(Double.toString(currentRegression.getRegression().getIntercept()));
    }

    private void deleteRegressionSeries(RegressionData regressionData) {
        int size = regressionData.getRegressionSeries().getItemCount();
        for(int i=0; i<size; i++) {
            regressionData.getRegressionSeries().remove(0);
        }
    }

    private void calculateRegression(SeriesData seriesData, List<Range> rangeList) {
        for(Range range : rangeList) {
            RegressionData regressionData = new RegressionData(range);
            this.regression.put(range, regressionData);
            regressionData.getRegression().addData(preparePartialData(range, seriesData.getData()));
            regressionData.getRegression().regress();
            XYSeries regressionSeries = new XYSeries("Regression");

            double leftX = range.getFrom();
            double rightX = range.getTo();
            createRegressionLine(regressionData, regressionSeries, leftX, rightX);
            regressionData.setRegressionSeries(regressionSeries);

            regressionData.setCustomSlope(regressionData.getRegression().getSlope());
            regressionData.setCustomIntercept(regressionData.getRegression().getIntercept());
        }
    }

    private double[][] preparePartialData(Range range, double[][] data) {
        int size = 0;
        int index = range.getOrientation().equals("X") ? 0 : 1;
        for (double[] datum : data) {
            if (datum[index] > range.getFrom() && datum[index] < range.getTo())
                size++;
        }
        double[][] result = new double[size][2];
        size = 0;
        for (double[] datum : data) {
            if (datum[index] > range.getFrom() && datum[index] < range.getTo()) {
                result[size] = datum;
                size++;
            }
        }
        return result;
    }

    private void createRegressionLine(RegressionData regressionData, XYSeries series,double leftX, double rightX){
        int number = 250;
        for(int i = 0; i<number; i++){
            double x = (rightX - leftX)*((double)i/number) + leftX;
            double y = regressionData.getRegression().predict(x);
            series.add(x, y);
        }
    }

    private Shape getRegressionShape() {
        return new Ellipse2D.Double(0,0,4,4);
    }

    private Shape getPointShape() {
        return new Ellipse2D.Double(0,0,2,2);
    }

    private Paint getPointPaint(){
        return Color.BLUE;
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
