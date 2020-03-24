package main.view.service;

import javafx.geometry.Point2D;
import lombok.AllArgsConstructor;
import main.view.service.dto.ColorData;
import main.view.service.dto.SeriesData;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

import java.awt.*;

public class Chart2DRenderer extends XYLineAndShapeRenderer {

    private ColorData lowColorRGB;
    private ColorData highColorRGB;
    private SeriesData series;
    private Integer numberOfRegressions;

    public Chart2DRenderer(boolean calculateRegression,Color low, Color high, SeriesData series, Integer numberOfRegressions){
        super(false, true);
        this.lowColorRGB = new ColorData(low.getRed(), low.getGreen(), low.getBlue());
        this.highColorRGB = new ColorData(high.getRed(), high.getGreen(), high.getBlue());
        this.series = series;
        this.numberOfRegressions = numberOfRegressions;
    }

    @Override
    public Paint getItemPaint(int row, int column) {
        if(row != this.numberOfRegressions)
            return getSeriesPaint(row);
        if(column > this.series.getSeries().getItemCount())
            return Color.WHITE;
        double x = (double) series.getSeries().getX(column);
        double y = (double) series.getSeries().getY(column);
        return calculateColor(series.getZMap().get(new Point2D(x, y)));
    }

    private Color calculateColor(double z){
        if(z > series.getMaxZ())
            z = series.getMaxZ();
        if(z < series.getMinZ())
            z = series.getMinZ();
        double percent = (z - series.getMinZ())/(series.getMaxZ() - series.getMinZ());
        int red = (int) ((highColorRGB.getRed() - lowColorRGB.getRed()) * percent + lowColorRGB.getRed());
        int green = (int) ((highColorRGB.getGreen() - lowColorRGB.getGreen()) * percent + lowColorRGB.getGreen());
        int blue = (int) ((highColorRGB.getBlue() - lowColorRGB.getBlue()) * percent + lowColorRGB.getBlue());
        return new Color(red, green, blue);
    }
}
