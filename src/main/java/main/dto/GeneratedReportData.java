package main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneratedReportData {

    private String name;
    private Set<String> columns;
    private List<HashMap<String, String>> report;
    private String[][] data;

    @Override
    public String toString(){
        return name;
    }
}
