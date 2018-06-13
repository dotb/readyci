package com.squarepolka.readyci.util;

public class PropertyMissingException extends RuntimeException {

    public PropertyMissingException(String propertyName) {
        super(String.format("Could not load the value for %s. Please specify %s in your configuration.", propertyName, propertyName));
    }
}
