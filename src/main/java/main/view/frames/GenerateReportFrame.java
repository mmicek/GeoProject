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
            setBounds(name, 0.05f, 0.3f).under(save);
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
        setBounds(scroll, 0.15f, 0.06f, 1400, 800);
    }

    private void chart(GeneratedReportData data) {
        JButton button = new JButton("Generuj wykres");
        JComboBox<String> xColumn = new JComboBox<>(data.getColumns().toArray(new String[0]));
        JComboBox<String> yColumn = new JComboBox<>(data.getColumns().toArray(new String[0]));
        JCheckBox zValue = new JCheckBox("Wykres kolumna Z");
        JComboBox<String> zColumn = new JComboBox<>(data.getColumns().toArray(new String[0]));

        button.addActionListener(e -> {
            Chart2DConfigData config = new Chart2DConfigData();
            config.setXColumn(Objects.requireNonNull(xColumn.getSelectedItem()).toString());
            config.setYColumn(Objects.requireNonNull(yColumn.getSelectedItem()).toString());
            if(zValue.isSelected())
                config.setZColumn(Objects.requireNonNull(zColumn.getSelectedItem()).toString());
            MainFrame.getMainFrame().changeView(new Chart2DFrame(data, config, this));
        });

        FramePosition position = setBounds(button, 0.89f,0.3f).under(xColumn);
        JTextArea x = new JTextArea("X");
        JTextArea y = new JTextArea("Y");
        JTextArea z = new JTextArea("Z");
        position.right(x, 20 ,30);
        position = position.under(yColumn);
        position.right(y, 20, 30);
        position.under(zValue).under(zColumn).right(z, 20, 30);

        ColorUtils.setTextBaldComponent(x);
        ColorUtils.setTextBaldComponent(y);
        ColorUtils.setTextBaldComponent(z);

    }
}
