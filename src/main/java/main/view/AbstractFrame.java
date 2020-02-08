package main.view;

import lombok.AllArgsConstructor;
import main.repository.ErrorRepository;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public abstract class AbstractFrame {
    protected int width;
    protected int height;
    private boolean wasError = false;
    private JPanel panel;

    public abstract void createPanel(JPanel panel) throws Throwable;

    public JPanel recreatePanel(){
        createPanel();
        return this.panel;
    }

    public AbstractFrame() {
        MainFrame m = MainFrame.getMainFrame();
        width = m.getWidth();
        height = m.getHeight();
    }

    private void createPanel(){
        this.panel = new JPanel();
        panel.setLayout(null);
        try {
            this.createPanel(panel);
        } catch (Throwable ex){
            ex.printStackTrace();
            this.wasError = true;
            error(ex.toString(), ex);
        }
    }

    protected void addReturnButton(String name, AbstractFrame frame){
        JButton button = new JButton(name);
        button.addActionListener(e -> MainFrame.getMainFrame().changeView(frame));
        setBounds(button,0.1f, 0.9f, 200, 30);
    }

    public void removeComponents(List<JComponent> components){
        for (JComponent component : components)
            this.panel.remove(component);
    }

    public FramePosition getDefaultFramePosition(float x,float y){
        return getDefaultFramePosition(x, y, 150, 30);
    }

    public FramePosition getDefaultFramePosition(float x, float y, int width, int height){
        return new FramePosition(this,Math.round(this.width * x), Math.round(this.height * y), width, height);
    }

    protected  FramePosition setListBounds(List<? extends JComponent> list, float x, float y){
        return setListBounds(list, x, y,150,30);
    }

    protected  FramePosition setListBounds(List<? extends JComponent> list, float x, float y, int width, int height){
        int yy = -1;
        for (int i = 0; i<list.size(); i++) {
            list.get(i).setBounds(Math.round(this.width * x), Math.round(this.height * y) + i * (height + 10), width, height);
            yy = Math.round(this.height * y) + i * (height + 10);
            panel.add(list.get(i));
        }
        return new FramePosition(this,Math.round(this.width * x), yy, width, height);
    }

    protected  FramePosition setBounds(JComponent component, float x, float y){
        return setBounds(component, x, y, 150, 30);
    }

    protected  FramePosition setBounds(JComponent component, float x, float y, int width, int height){
        component.setBounds(Math.round(this.width*x), Math.round(this.height*y), width, height);
        panel.add(component);
        return new FramePosition(this,Math.round(this.width*x), Math.round(this.height*y), width, height);
    }

    protected  FramePosition setListBounds(List<? extends JComponent> list, int x, int y){
        return setListBounds(list, x, y,150,30);
    }

    protected  FramePosition setListBounds(List<? extends JComponent> list, int x, int y, int width, int height){
        int yy = -1;
        for (int i = 0; i<list.size(); i++) {
            list.get(i).setBounds(x, y + i * (height + 10), width, height);
            yy = y + i * (height + 10);
            panel.add(list.get(i));
        }
        return new FramePosition(this,x, yy, width, height);
    }

    protected  FramePosition setBounds(JComponent component, int x, int y){
        return setBounds(component, x, y, 150, 30);
    }

    boolean hasError(){
        boolean result = wasError;
        wasError = false;
        return result;
    }

    protected void error(String message){
        this.wasError = true;
        JOptionPane.showMessageDialog(new Frame(), message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void error(String message, Throwable ex){
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        ErrorRepository.getInstance().save(sw.toString());
        error(message);
    }

    protected  FramePosition setBounds(JComponent component, int x, int y, int width, int height){
        component.setBounds(x, y, width, height);
        panel.add(component);
        return new FramePosition(this,x, y, width, height);
    }

    public void refresh(){
        MainFrame.getMainFrame().refresh();
    }

    JPanel getPanel(){
        return panel;
    }

    @AllArgsConstructor
    public static class FramePosition{

        private AbstractFrame frame;
        private int x;
        private int y;
        private int width;
        private int height;

        public FramePosition under(JComponent component){
            return frame.setBounds(component, x , (y == -1 ? 0 : y + height + 10), width, height);
        }
        public FramePosition under(List<? extends JComponent> components){
            return frame.setListBounds(components, x ,(y == -1 ? 0 : y + height + 10), width, height);
        }
        public FramePosition under(JComponent component,int width, int height){
            return frame.setBounds(component, x ,(y == -1 ? 0 : y + this.height + 10), width, height);
        }
        public FramePosition under(List<? extends JComponent> components,int width, int height){
            return frame.setListBounds(components, x ,(y == -1 ? 0 : y + this.height + 10), width, height);
        }
        public FramePosition right(JComponent component){
            return frame.setBounds(component, (x + width + 10), y, width, height);
        }
        public FramePosition right(JComponent component, int width, int height){
            return frame.setBounds(component, (x + this.width + 10), y, width, height);
        }
    }

}
