package main.utils;

import org.mariuszgromada.math.mxparser.Expression;

import javax.script.ScriptException;
import java.util.Map;

public class MathUtils {

    public static double evalExpression(Map<String, String> columns, String expression) throws ScriptException {
        for(Map.Entry<String, String> entry : columns.entrySet())
            expression = expression.replace(entry.getKey(), entry.getValue());
        return new Expression(expression).calculate();
    }
}
