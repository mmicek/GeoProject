package main.view.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Range {

    private Double from;
    private Double to;
    private String orientation;
}
