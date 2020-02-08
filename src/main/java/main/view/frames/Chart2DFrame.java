package main.view.frames;

import javafx.geometry.Point2D;
import main.dto.GeneratedReportData;
import main.view.AbstractFrame;
import main.view.dto.Chart2DConfigData;
import main.view.service.Chart2DRenderer;
import main.view.service.dto.SeriesData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Map;

public class Chart2DFrame extends AbstractFrame {

    private GeneratedReportData report;
    private Chart2DConfigData configData;
    private GenerateReportFrame back;

    public Chart2DFrame(GeneratedReportData report, Chart2DConfigData configData, GenerateReportFrame back){
        this.report = report;
        this.configData = configData;
        this.back = back;
    }

    @Override
    public void createPanel(JPanel panel) {
        addReturnButton("Cofnij", this.back);
        SeriesData seriesData = createDataSet();
        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(seriesData.getSeries());
        JFreeChart chart = ChartFactory.createScatterPlot("Chart",
                configData.getXColumn(),
                configData.getYColumn(),
                collection,
                PlotOrientation.VERTICAL,
                true, true, false);

        if(this.configData.getZColumn() != null)
            ((XYPlot) chart.getPlot()).setRenderer(new Chart2DRenderer(
                    this.configData.getMaxColor().getColor(),
                    this.configData.getMinColor().getColor(),
                    seriesData));

        ((XYPlot) chart.getPlot()).getRenderer().setSeriesShape(0, getPointShape());
        ChartPanel cp = new ChartPanel(chart);
        setBounds(cp, 0.25f, 0.04f, 900, 900);
    }

    private Shape getPointShape() {
        return new Ellipse2D.Double(0,0,2,2);
    }

    private SeriesData createDataSet(){
        double maxZ = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE;
        XYSeries series = new XYSeries(this.configData.getZColumn() != null ? this.configData.getZColumn() : "Values");
        HashMap<Point2D, Double> zMap = new HashMap<>();
        for (Map<String, String> rows : this.report.getReport()){
            double x = Double.valueOf(rows.get(this.configData.getXColumn()));
            double y = Double.valueOf(rows.get(this.configData.getYColumn()));
            series.add(x, y);

            if(this.configData.getZColumn() != null) {
                double z = Double.valueOf(rows.get(this.configData.getZColumn()));
                zMap.put(new Point2D(x, y), z);

                if (z > maxZ)
                    maxZ = z;
                if (z < minZ)
                    minZ = z;
            }
        }
        return new SeriesData(series, minZ, maxZ, zMap);
    }
}
