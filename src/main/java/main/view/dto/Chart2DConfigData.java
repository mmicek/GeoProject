package main.view.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Chart2DConfigData {

    private String xColumn;
    private String yColumn;
    private String zColumn;
    private Color minColor;
    private Color maxColor;
    private Double minValue;
    private Double maxValue;
    private boolean calculateRegression;
}
