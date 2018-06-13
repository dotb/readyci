package com.squarepolka.readyci.util;

import com.squarepolka.readyci.taskrunner.BuildEnvironment;

import java.util.List;
import java.util.Map;

public class Util {

    public static String getMappedValueAtPath(Map map, String path) {
        String[] pathKeys = path.split("\\.");
        Object currentObject = map;
        for (String key : pathKeys) {

            // Traverse the map type objects
            if (null != currentObject && currentObject instanceof Map) {
                currentObject = ((Map) currentObject).get(key);
            } else if (null != currentObject && currentObject instanceof List) {
                List listItems = (List) currentObject;
                currentObject = parseListObject(listItems, key);
            }

        }

        // If we've landed on a String, return it
        if (null != currentObject && currentObject instanceof String) {
            return (String) currentObject;
        }

        // If all else, return an empty string
        return "";
    }


    public static Object parseListObject(List listItems, String key) {
        // Find a map in this list which contains the key we're looking for
        for (Object listObject : listItems) {
            if (listObject instanceof Map) {
                Map map = (Map) listObject;
                if (map.containsKey(key)) {
                    return map.get(key);
                }
            }
        }
        return null;
    }

    public static boolean valueExists(String string) {
        return string.length() > 0;
    }

    public static String getBuildProperty(BuildEnvironment buildEnvironment, String propertyName, String defaultValue) {
        try {
            String value = getBuildProperty(buildEnvironment, propertyName);
            return value;
        } catch (PropertyMissingException e) {
            return defaultValue;
        }
    }

    public static String getBuildProperty(BuildEnvironment buildEnvironment, String propertyName) {
        String value = buildEnvironment.buildParameters.get(propertyName);
        if (null == value) {
            throw new PropertyMissingException(propertyName);
        }
        return value;
    }
}
