package main.utils;

import javax.swing.text.JTextComponent;
import java.awt.*;

public class ColorUtils {

    public static void setTextBaldComponent(JTextComponent component){
        ColorUtils.setTextComponent(component);
        component.setFont(new Font("SansSerif", Font.BOLD, Math.round(component.getHeight()*0.7f)));
    }

    public static void setTextComponent(JTextComponent component){
        component.setBackground(new Color(238,238, 238));
        component.setFont(new Font("SansSerif", Font.PLAIN, Math.round(component.getHeight()*0.7f)));
        component.setEditable(false);
    }
}
