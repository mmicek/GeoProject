package main.view.service;

import javafx.geometry.Point2D;
import lombok.AllArgsConstructor;
import main.view.service.dto.SeriesData;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

import java.awt.*;

public class Chart2DRenderer extends XYLineAndShapeRenderer {

    private int lowColorRGB;
    private int highColorRGB;
    private SeriesData series;

    public Chart2DRenderer(Color low, Color high, SeriesData series){
        super(false, true);
        this.lowColorRGB = low.getRGB();
        this.highColorRGB = high.getRGB();
        this.series = series;
    }

    @Override
    public Paint getItemPaint(int row, int column) {
//        return super.getItemPaint(row, column);
        if(column > this.series.getSeries().getItemCount())
            return Color.BLACK;
        double x = (double) series.getSeries().getX(column);
        double y = (double) series.getSeries().getY(column);
        return calculateColor(series.getZMap().get(new Point2D(x, y)));
    }

    private Color calculateColor(double z){
        double percent = (z - series.getMinZ())/(series.getMaxZ() - series.getMinZ());
        int rgb = (int) ((highColorRGB - lowColorRGB) * percent + lowColorRGB);
        System.out.println(rgb);
        return new Color(rgb, false);
    }
}
