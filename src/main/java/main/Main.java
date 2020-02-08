package main;

import main.repository.ErrorRepository;
import main.view.AbstractFrame;
import main.view.MainFrame;
import main.view.frames.StartFrame;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Main {

    public static volatile boolean toEnd = false;

    public static void main(String []args) {
        System.out.println(Main.class.getProtectionDomain().getCodeSource().getLocation());
        AbstractFrame startFrame = getFrame();
        startFrame.recreatePanel();
        MainFrame.getMainFrame(startFrame).setScreen();
        while (!toEnd) {}
    }

    private static AbstractFrame getFrame() {
        return new StartFrame();
    }

}
