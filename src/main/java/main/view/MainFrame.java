package main.view;

import main.Main;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private AbstractFrame view = null;
    private static MainFrame frame = null;  //Singleton

    private MainFrame() { setSettings(); }

    private MainFrame(AbstractFrame view) {
        this.view = view;
        setSettings();
    }

    public static MainFrame getMainFrame() {
        if(frame == null){
            frame = new MainFrame();
            frame.setBackground(Color.LIGHT_GRAY);
        }
        return frame;
    }

    public static MainFrame getMainFrame(AbstractFrame view) {
        if(frame == null) {
            frame = new MainFrame(view);
            frame.setBackground(Color.LIGHT_GRAY);
        }
        if(frame.view == null) frame.setView(view);
        return frame;
    }

    void refresh(){
        this.revalidate();
        this.repaint();
    }

    private void setSettings() {
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Main.toEnd = true));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void setScreen() {
        addComponents();
    }

    private void addComponents() {
        if(view == null) throw new IllegalArgumentException("No view panel in MainFrame");
        getContentPane().add(view.getPanel());
        this.revalidate();
    }

    public void changeView(AbstractFrame newView) {
        JPanel panel = newView.recreatePanel();
        if(newView.hasError())
           return;
        getContentPane().remove(this.view.getPanel());
        getContentPane().add(panel);
        this.revalidate();
        this.view = newView;
    }

    public void exit() {
        Main.toEnd = true;
        this.dispose();
    }

    private void setView(AbstractFrame view) {
        this.view = view;
    }


}
