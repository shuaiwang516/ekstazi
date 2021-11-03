package org.ekstazi.configAware;


import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class instruments Listener methods to monitor configuration loading.
 */
public class APIListener {
    /** Exercised configuration <name, value> pairs */
    private static final Map<String, String> sConfigMap = new HashMap<String, String>();

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
        sConfigMap.clear();
    }

    /**
     * Add the exercised configuration into collected info.
     * @param configName Exercised configuration name;
     * @param configValue Exercised configuration value;
     */
    public static void addConfig(String configName, String configValue) {
        try {
            sLock.lock();
            addConfig0(configName, configValue);
        } finally {
            sLock.unlock();
        }
    }

    private static void addConfig0(String configName, String configValue) {
        if (sConfigMap.containsKey(configName)) {
            sConfigMap.replace(configName, configValue);
        } else {
            sConfigMap.put(configName, configValue);
        }
    }

    /**
     *
     * @return Configuration Map sorted by key
     */
    private static Map<String, String> sortedConfigMap() {
        try {
            sLock.lock();
            Map<String, String> sortedMap = new HashMap<String, String>();
            SortedSet<String> keys = new TreeSet<>(sConfigMap.keySet());
            for (String key : keys) {
                sortedMap.put(key, sConfigMap.get(key));
            }
            return sortedMap;
        } finally {
            sLock.unlock();
        }
    }
}
