package main.service;

import main.dto.ConfigurationData;
import main.dto.GeneratedReportData;
import main.utils.MathUtils;
import main.view.ApplicationException;

import javax.script.ScriptException;
import java.io.*;
import java.util.*;

public class GenerateReportService {

    public GeneratedReportData generateReport(ConfigurationData data, File file) throws IOException, ScriptException, ApplicationException {
        GeneratedReportData reportData = new GeneratedReportData();
        reportData.setColumns(new HashSet<>(data.getColumns()));
        List<HashMap<String, String>> report = loadFromFile(data, file);
        for(Map.Entry<String, String> entry : data.getTransformation().entrySet()){
            reportData.getColumns().add(entry.getKey());
        }
        for (HashMap<String, String> columns : report){
            for(Map.Entry<String, String> entry : data.getTransformation().entrySet()){
                columns.put(entry.getKey(),String.valueOf(MathUtils.evalExpression(columns,entry.getValue())));
            }
        }
        reportData.setReport(report);
        reportData.setData(generateTableData(reportData));
        return reportData;
    }

    private String[][] generateTableData(GeneratedReportData reportData) {
        String[][] result = new String[reportData.getReport().size()][reportData.getColumns().size()];
        int i = 0;
        for(HashMap<String, String> row : reportData.getReport()){
            int j = 0;
            for(String column : reportData.getColumns()){
                result[i][j] = row.get(column);
                j++;
            }
            i++;
        }
        return result;
    }

    private List<HashMap<String, String>> loadFromFile(ConfigurationData data, File file) throws IOException, ApplicationException {
        List<HashMap<String, String>> result = new LinkedList<>();
        String line;
        List<Integer> columnIndexes = new LinkedList<>();
        List<String> columnNames = new LinkedList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        boolean shouldContinue = true;
        while((line = reader.readLine()) != null){
            if(line.startsWith(" "))
                line = line.replaceFirst("\\s+","");
            String[] splitedLine = line.split("\\s+");
            if(line.startsWith("~A")){
                shouldContinue = false;
                for (int i = 0; i<splitedLine.length; i++)
                    if(data.getColumns().contains(splitedLine[i])){
                        columnIndexes.add(i - 1);
                        columnNames.add(splitedLine[i]);
                    }
                if(columnNames.size() != data.getColumns().size())
                    foundMissingAndThrow(columnNames, data.getColumns());
            }
            if(shouldContinue)
                continue;
            if(!line.startsWith("~A")){
                HashMap<String, String> columns = new HashMap<>();
                for (int i=0;i<columnNames.size();i++)
                    columns.put(columnNames.get(i), splitedLine[columnIndexes.get(i)]);
                result.add(columns);
            }
        }
        return result;
    }

    private void foundMissingAndThrow(List<String> columnNames, Set<String> columns) throws ApplicationException {
        Set<String> names = new HashSet<>(columnNames);
        for (String column : columns){
            if(!names.contains(column))
                throw new ApplicationException("Nie znaleziono kolumny: "+column);
        }
    }
}
