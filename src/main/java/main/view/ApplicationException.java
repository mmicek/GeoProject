package main.view;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationException extends Exception {

    private String message;

    @Override
    public String toString() {
        return message;
    }
}
