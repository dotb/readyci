package com.squarepolka.readyci.configuration.parameter;

public class ParameterParseException extends RuntimeException {
    public ParameterParseException(String parameterArgument) {
        super(String.format("Could not parse the parameter argument %s", parameterArgument));
    }
}
