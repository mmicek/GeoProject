package main.view.dto;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.data.xy.XYSeries;

import java.awt.*;

@Setter
@Getter
public class RegressionData {

    private SimpleRegression regression = new SimpleRegression();
    private XYSeries regressionSeries;
    private Double customSlope;
    private Double customIntercept;
    private Range range;
    private Paint color;

    public RegressionData(Range range){
        this.range = range;
    }
}
