package main.view.frames.listeners;

import main.view.dto.RegressionData;
import org.jfree.chart.ChartPanel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextRegressionListener implements DocumentListener {

    private RegressionData regression;

    public TextRegressionListener(RegressionData regression) {
        this.regression = regression;
    }

    private void executeChange(DocumentEvent e) {
        System.out.println("HI");
        this.regression.getRegressionSeries().add(0,0);
        this.regression.getRegressionSeries().add(100,100);
    }

    @Override
    public void insertUpdate(DocumentEvent e) { executeChange(e); }

    @Override
    public void removeUpdate(DocumentEvent e) { executeChange(e); }

    @Override
    public void changedUpdate(DocumentEvent e) { executeChange(e); }

}
