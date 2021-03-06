package main.view.dto;

import lombok.Getter;

@Getter
public enum Color {

    YELLOW(java.awt.Color.YELLOW),
    RED(java.awt.Color.RED),
    GREEN(java.awt.Color.GREEN),
    PINK(java.awt.Color.PINK),
    CYAN(java.awt.Color.CYAN),
    DARK_GRAY(java.awt.Color.DARK_GRAY),
    BLUE(java.awt.Color.BLUE);


    private java.awt.Color color;

    Color(java.awt.Color color){
        this.color = color;
    }
}
