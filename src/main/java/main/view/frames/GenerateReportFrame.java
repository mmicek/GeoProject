package main.view.frames;

import main.dto.ConfigurationData;
import main.dto.GeneratedReportData;
import main.repository.ConfigurationRepository;
import main.repository.GeneratedReportRepository;
import main.service.GenerateReportService;
import main.utils.ColorUtils;
import main.view.AbstractFrame;
import main.view.ApplicationException;
import main.view.MainFrame;
import main.view.dto.Chart2DConfigData;
import main.view.dto.Color;

import javax.script.ScriptException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GenerateReportFrame extends AbstractFrame {

    private static GenerateReportService service = new GenerateReportService();
    private static ConfigurationRepository repository = ConfigurationRepository.getInstance();
    private static GeneratedReportRepository reportRepository = GeneratedReportRepository.getInstance();

    private GeneratedReportData data;
    private String reportName;
    private String configurationName;
    private File file;

    private boolean isGenerateZColumnChecked = false;

    public GenerateReportFrame(String reportName){
        this.reportName = reportName;
    }

    public GenerateReportFrame(String configurationName, File file){
        this.configurationName = configurationName;
        this.file = file;
    }

    @Override
    public void createPanel(JPanel panel) throws Exception {
        addReturnButton("Menu", new StartFrame());
        this.data = this.data != null ? this.data : reportRepository.getReportByName(this.reportName);
        if(this.data == null) {
            this.data = generateReport();
            final GeneratedReportData saveData = this.data;
            JTextField name = new JTextField("");
            JButton save = new JButton("Zapisz raport");
            save.addActionListener(e -> {
                saveData.setName(name.getText());
                reportRepository.save(saveData);
            });
            setBounds(name,  0.85f,0.1f).under(save);
        }
        loadReport(data);
        chart(data);
    }

    private GeneratedReportData generateReport() throws IOException, ScriptException, ApplicationException {
        ConfigurationData configuration = repository.getConfigurationByName(this.configurationName);
        if(configuration == null || configuration.getColumns() == null)
            error("Problem z wygenerowaniem raportu: nie znaleziono konfuguracji lub lista kolumn do wczytania jest pusta");
        return service.generateReport(configuration, this.file);
    }

    private void loadReport(GeneratedReportData report) {
        if(report == null || report.getReport().size() == 0)
            return;
        JTable table = new JTable(report.getData(), report.getColumns().toArray(new String[0]));
        JScrollPane scroll = new JScrollPane(table);
        setBounds(scroll, 0.10f, 0.06f, 1400, 800);
    }

    private void chart(GeneratedReportData data) {
        JButton button = new JButton("Generuj wykres");
        JComboBox<String> xColumn = new JComboBox<>(data.getColumns().toArray(new String[0]));
        JComboBox<String> yColumn = new JComboBox<>(data.getColumns().toArray(new String[0]));
        JCheckBox zValue = new JCheckBox("Wykres kolumna Z");
        JCheckBox calculateRegression = new JCheckBox("Wylicz regresje");
        calculateRegression.setSelected(true);
        zValue.setSelected(isGenerateZColumnChecked);
        JComboBox<String> zColumn = new JComboBox<>(data.getColumns().toArray(new String[0]));

        JTextField minValue = new JTextField("-1");
        JTextField maxValue = new JTextField("-1");
        JComboBox<Color> minColor = new JComboBox<>(Color.values());
        minColor.setSelectedItem(Color.BLUE);
        JComboBox<Color> maxColor = new JComboBox<>(Color.values());
        maxColor.setSelectedItem(Color.RED);

        button.addActionListener(e -> {
            Chart2DConfigData config = new Chart2DConfigData();
            config.setXColumn(Objects.requireNonNull(xColumn.getSelectedItem()).toString());
            config.setYColumn(Objects.requireNonNull(yColumn.getSelectedItem()).toString());
            config.setCalculateRegression(calculateRegression.isSelected());
            if(zValue.isSelected()) {
                isGenerateZColumnChecked = true;
                config.setZColumn(Objects.requireNonNull(zColumn.getSelectedItem()).toString());
                config.setMinColor((Color) minColor.getSelectedItem());
                config.setMaxColor((Color) maxColor.getSelectedItem());
                config.setMinValue(Double.valueOf(minValue.getText()));
                config.setMaxValue(Double.valueOf(maxValue.getText()));
            } else
                isGenerateZColumnChecked = false;
            MainFrame.getMainFrame().changeView(new Chart2DFrame(data, config, this));
        });

        FramePosition position = setBounds(button, 0.85f,0.3f).under(xColumn);
        JTextArea x = new JTextArea("X");
        JTextArea y = new JTextArea("Y");
        JTextArea z = new JTextArea("Z");
        JTextArea min = new JTextArea("Min");
        JTextArea max = new JTextArea("Max");

        position.right(x, 20 ,30);
        position = position.under(yColumn);
        position.right(y, 20, 30);
        position = position.under(zValue).under(zColumn);
        position.right(z, 20, 30);
        position = position.under(minColor);
        position.right(min, 40, 30);
        position = position.under(maxColor);
        position.right(max, 40, 30);
        position.under(minValue).under(maxValue).under(calculateRegression);

        ColorUtils.setTextBaldComponent(x);
        ColorUtils.setTextBaldComponent(y);
        ColorUtils.setTextBaldComponent(z);
        ColorUtils.setTextBaldComponent(min);
        ColorUtils.setTextBaldComponent(max);
    }
}
