package main.view.frames;

import main.dto.ConfigurationData;
import main.repository.ConfigurationRepository;
import main.utils.ColorUtils;
import main.view.AbstractFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurationsFrame extends AbstractFrame {

    private static ConfigurationRepository repository = ConfigurationRepository.getInstance();
    private List<JComponent> showComponentsList = new LinkedList<>();

    private static int columnKeyWidth = 100;
    private static int columnValueWidth = 250;

    @Override
    public void createPanel(JPanel panel) {
        showList(panel);
        configurationForm(panel);
    }

    private void configurationForm(JPanel panel) {
        final HashMap<String, String> map = new HashMap<>();
        JTextField name = new JTextField("Nazwa");
        JTextArea selected = new JTextArea("Wybrane");
        JTextField column = new JTextField("Nowa kolumna");
        JTextField transform = new JTextField("Wartosc (puste == zwykła kolumna)");
        JButton add = new JButton("Dodaj kolumne");
        JButton create = new JButton("Zapisz konfiguracje");

        FramePosition position = setBounds(name, 0.05f,0.2f, 200, 40).under(column);
        position.right(transform);
        position.under(add).right(create);

        final FramePosition selectedPosition = setBounds(selected, 0.3f, 0.2f, 200, 30);
        ColorUtils.setTextBaldComponent(selected);

        add.addActionListener(new ActionListener() {
            FramePosition position = selectedPosition;
            @Override
            public void actionPerformed(ActionEvent e) {
                map.put(column.getText(), transform.getText());
                position = position.under(new JTextArea(column.getText()),
                        ConfigurationsFrame.columnKeyWidth, 30);
                if(!transform.getText().isEmpty())
                    position.right(new JTextArea(transform.getText()),
                            ConfigurationsFrame.columnValueWidth, 30);
                refresh();
            }
        });
        create.addActionListener(e -> {
            Map<String, String> transformation = new HashMap<>();
            Set<String> columns = new LinkedHashSet<>();
            ConfigurationData data = new ConfigurationData();
            data.setColumns(columns);
            data.setTransformation(transformation);
            data.setName(name.getText());
            for(Map.Entry<String, String> var : map.entrySet()){
                if(var.getValue().isEmpty())
                    columns.add(var.getKey());
                else
                    transformation.put(var.getKey(), var.getValue());
            }
            if(!repository.save(data))
                error("Nazwa jest już zajęta");
            showList(panel);
            refresh();
        });
        addReturnButton("Menu", new StartFrame());
    }

    private void showList(JPanel panel){
        List<JButton> configurations = repository.getAllItems().stream()
                .map(e -> {
                    JButton button = new JButton(e.getName());
                    button.addActionListener(this::showConfiguration);
                    return button;
                })
                .collect(Collectors.toList());
        setListBounds(configurations, 0.85f, 0.2f);
        configurations.forEach(panel::add);
    }

    private void showConfiguration(ActionEvent e1) {
        if(!this.showComponentsList.isEmpty()) {
            removeComponents(showComponentsList);
            showComponentsList.clear();
        }

        ConfigurationData data = repository.getConfigurationByName(e1.getActionCommand());
        JTextArea name = new JTextArea(e1.getActionCommand());
        this.showComponentsList.add(name);
        FramePosition position = setBounds(name, 0.58f,0.2f, 200, 30);
        ColorUtils.setTextBaldComponent(name);

        for (String column : data.getColumns()) {
            JTextArea columnArea = new JTextArea(column);
            this.showComponentsList.add(columnArea);
            position = position.under(columnArea, ConfigurationsFrame.columnKeyWidth ,30);
        }

        for(Map.Entry<String, String> transform : data.getTransformation().entrySet()){
            JTextArea columnArea = new JTextArea(transform.getKey());
            JTextArea valueArea = new JTextArea(transform.getValue());
            this.showComponentsList.add(columnArea);
            this.showComponentsList.add(valueArea);

            position = position.under(columnArea, ConfigurationsFrame.columnKeyWidth, 30);
            position.right(valueArea, ConfigurationsFrame.columnValueWidth, 30);
        }
        this.refresh();
    }

}
