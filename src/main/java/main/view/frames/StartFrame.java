package main.view.frames;

import main.dto.ConfigurationData;
import main.dto.GeneratedReportData;
import main.repository.ConfigurationRepository;
import main.repository.GeneratedReportRepository;
import main.view.AbstractFrame;
import main.view.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

public class StartFrame extends AbstractFrame {

    private static ConfigurationRepository repository = ConfigurationRepository.getInstance();
    private static GeneratedReportRepository reportRepository = GeneratedReportRepository.getInstance();

    @Override
    public void createPanel(JPanel panel) {
        JButton configurations = new JButton("Configurations");
        configurations.addActionListener(arg0 -> MainFrame.getMainFrame().changeView(new ConfigurationsFrame()));

        List<ConfigurationData> list = repository.getAllItems();
        ConfigurationData[] data = new ConfigurationData[repository.getAllItems().size()];
        for(int i = 0; i<data.length;i ++)
            data[i] = list.get(i);

        JComboBox<ConfigurationData> configuration = new JComboBox<>(data);
        JButton resultArray = new JButton("Generuj");
        JFileChooser fileChooser = new JFileChooser("Wybierz plik");

        setBounds(resultArray,0.3f,0.25f).under(fileChooser, 700, 400);
        resultArray.addActionListener(e -> {
            if(configuration.getSelectedItem() == null)
                error("Brak wybranej konfiguracji");
            else
                MainFrame.getMainFrame().changeView(new GenerateReportFrame(
                    Objects.requireNonNull(configuration.getSelectedItem().toString()),
                    fileChooser.getSelectedFile()));
        });
        setBounds(configurations, 0.70f, 0.25f).under(configuration);

        setReportsView();
    }

    private void setReportsView() {
        JComboBox<GeneratedReportData> reports = new JComboBox<>(reportRepository.getAllItems()
                .toArray(new GeneratedReportData[0]));
        JButton load = new JButton("Wczytaj raport");
        load.addActionListener(e -> {
            if(reports.getSelectedItem() == null)
                error("Brak wybranego raportu");
            else
                MainFrame.getMainFrame().changeView(new GenerateReportFrame(reports.getSelectedItem().toString()));
        });
        setBounds(load, 0.13f, 0.25f).under(reports);
    }
}
