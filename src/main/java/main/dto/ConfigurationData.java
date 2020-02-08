package main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationData {

    private String name;
    private Set<String> columns;
    private Map<String, String> transformation;

    @Override
    public String toString(){
        return name;
    }
}
