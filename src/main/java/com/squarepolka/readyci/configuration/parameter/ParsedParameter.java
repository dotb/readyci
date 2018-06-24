package com.squarepolka.readyci.configuration.parameter;

public class ParsedParameter {
    public String parameterKey;
    public String parameterValue;

    public ParsedParameter(String parameterArgument) {
        String[] parsedParameter = parameterArgument.split("=");
        if (parsedParameter.length >= 2) {
            this.parameterKey = parsedParameter[0];
            this.parameterValue = parsedParameter[1];
        } else {
            throw new ParameterParseException(parameterArgument);
        }
    }
}
