package main.view.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jfree.data.xy.XYSeries;

import javafx.geometry.Point2D;
import java.util.HashMap;

@Getter
@AllArgsConstructor
public class SeriesData {

    private XYSeries series;
    private double minZ;
    private double maxZ;
    private HashMap<Point2D, Double> zMap;
}
