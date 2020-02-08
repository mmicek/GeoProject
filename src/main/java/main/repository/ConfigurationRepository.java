package main.repository;

import main.dto.ConfigurationData;

import java.io.IOException;

public class ConfigurationRepository extends AbstractRepository<ConfigurationData> {

    private static ConfigurationRepository instance;

    private ConfigurationRepository() throws Exception{}

    @Override
    public boolean save(ConfigurationData data) {
        if(getConfigurationByName(data.getName()) != null)
            return false;
        return super.save(data);
    }

    public static ConfigurationRepository getInstance(){
        if(instance == null) {
            try {
                instance = new ConfigurationRepository();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public ConfigurationData getConfigurationByName(String name){
        for (ConfigurationData data : getAllItems())
            if(data.getName().equalsIgnoreCase(name))
                return data;
        return null;
    }
}
