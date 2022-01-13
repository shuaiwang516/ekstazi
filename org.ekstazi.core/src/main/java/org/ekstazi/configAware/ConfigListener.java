package org.ekstazi.configAware;


import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class instruments Listener methods to monitor configuration loading.
 */
public class ConfigListener {
    /** Exercised configuration <name, value> pairs */
    private static final Map<String, String> sGetConfigMap = new HashMap<String, String>();

    /** configuration pairs that from set() API */
    private static final Map<String, String> sSetConfigMap = new HashMap<String, String>();

    /** Lock for this listener */
    private static final ReentrantLock sLock = new ReentrantLock();

    // Helper methods

    /**
     * Clean collected Configuration <name, value> pairs
     * */
    public static void clean() {
        try {
            sLock.lock();
            clean0();
        } finally {
            sLock.unlock();
        }
    }

    private static void clean0() {
        sGetConfigMap.clear();
        sSetConfigMap.clear();
    }

    /**
     * Add the exercised configuration into collected info.
     * @param configName Exercised configuration name;
     * @param configValue Exercised configuration value;
     */
    public static void addGetConfig(String configName, String configValue) {
        try {
            sLock.lock();
            addGetConfig0(configName, configValue);
        } finally {
            sLock.unlock();
        }
    }

    private static void addGetConfig0(String configName, String configValue) {
        if (sGetConfigMap.containsKey(configName)) {
            sGetConfigMap.replace(configName, configValue);
        } else {
            sGetConfigMap.put(configName, configValue);
        }
    }

    /**
     * Add the set configuration into collected info.
     * @param configName set configuration name;
     * @param configValue set configuration value;
     */
    public static void addSetConfig(String configName, String configValue) {
        try {
            sLock.lock();
            addSetConfig0(configName, configValue);
        } finally {
            sLock.unlock();
        }
    }

    private static void addSetConfig0(String configName, String configValue) {
        if (sSetConfigMap.containsKey(configName)) {
            sSetConfigMap.replace(configName, configValue);
        } else {
            sSetConfigMap.put(configName, configValue);
        }
    }

    /**
     * In the dependency file, Configuration <name, value> are sorted by key
     * to make comparing config-diff easier.
     * @return Configuration Map sorted by key
     */
    public static Map<String, String> sortConfigMap() {
        try {
            sLock.lock();
            Map<String, String> sortedMap = new HashMap<String, String>();
            SortedSet<String> keys = new TreeSet<>(sGetConfigMap.keySet());
            for (String key : keys) {
                sortedMap.put(key, sGetConfigMap.get(key));
            }
            return sortedMap;
        } finally {
            sLock.unlock();
        }
    }

    public static Map<String, String> getConfigMap() {
        // Remove those configuration pairs that hardcoded in the unit tests.
        if (!sSetConfigMap.isEmpty() && !sGetConfigMap.isEmpty()) {
            for (Map.Entry<String, String> setEntry : sSetConfigMap.entrySet()) {
                if (sGetConfigMap.containsKey(setEntry.getKey())) {
                    sGetConfigMap.remove(setEntry.getKey());
                }
            }
        }
        return sGetConfigMap;
    }

    /**
     * Remove blank space, \r, \n, \t in a given string
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    // Touch method

    /**
     * Software's Configuration APIs invoke this method to collect
     * exercised configuration.
     * @param name Configuration name that used by get/set config-API
     * @param value Configuration value that used by get/set config-API
     */
    public static void recordGetConfig(String name, String value) {
        if (name != null && value != null && !name.equals("") && !value.equals("")) {
            name = replaceBlank(name);
            value = replaceBlank(name);
            addGetConfig(name, value);
        }
    }

    public static void recordSetConfig(String name, String value) {
        if (name != null && value != null && !name.equals("") && !value.equals("")) {
            name = replaceBlank(name);
            value = replaceBlank(name);
            addSetConfig(name, value);
        }
    }

}
