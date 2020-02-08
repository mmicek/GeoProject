package main.repository;

import main.dto.ConfigurationData;
import main.dto.GeneratedReportData;

import java.io.IOException;

public class GeneratedReportRepository extends AbstractRepository<GeneratedReportData> {

    private static GeneratedReportRepository instance;

    private GeneratedReportRepository() throws Exception { }

    public static GeneratedReportRepository getInstance(){
        if(instance == null) {
            try {
                instance = new GeneratedReportRepository();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public GeneratedReportData getReportByName(String name){
        for (GeneratedReportData data : getAllItems())
            if(data.getName().equalsIgnoreCase(name))
                return data;
        return null;
    }
}
