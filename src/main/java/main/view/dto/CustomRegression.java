package main.view.dto;

import lombok.AllArgsConstructor;
import org.apache.commons.math3.stat.regression.SimpleRegression;

@AllArgsConstructor
public class CustomRegression extends SimpleRegression {

    private double slope;
    private double intercept;

    @Override
    public double predict(double x) {
        return slope*x + intercept;
    }
}
