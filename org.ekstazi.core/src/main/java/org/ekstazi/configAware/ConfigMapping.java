package org.ekstazi.configAware;

import org.ekstazi.Config;
import org.ekstazi.log.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class ConfigMapping {
    /** Ctest grouped mapping, key = TestName, Value = Set<configName>*/
    private static final Map<String, Set<String>> sGroupedMapping = new HashMap<String, Set<String>>();

    /** Return the grouped mapping */
    public static Map<String, Set<String>> getConfigMapping(){
        if (sGroupedMapping.isEmpty() || sGroupedMapping == null) {
            generateGroupedMapping();
        }
        return sGroupedMapping;
    }

    /** Generate the grouped mapping */
    private static void generateGroupedMapping(){
        String mappingFilePath = Config.CTEST_MAPPING_FILE_PATH_V;
        File mappingFile = new File(mappingFilePath);
        if (!mappingFile.exists() || mappingFile.isDirectory()) {
            Log.e("Mapping file is not exist");
            Log.d2f("Mapping file is not exist");
            return;
        }
        try {
            FileReader fileReader = new FileReader(mappingFile);
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(fileReader);

            JSONObject mappingObject = (JSONObject) obj;
            Set<String> testName = mappingObject.keySet();
            for (String test : testName) {
                JSONArray configArray = (JSONArray) mappingObject.get(test);
                Set<String> configName = new HashSet<>();
                for (int i = 0; i < configArray.size(); i++) {
                    String name = (String) configArray.get(i);
                    if (name != "" && !name.isEmpty()) {
                        configName.add(name);
                    }
                }
                sGroupedMapping.put(test, configName);
            }

            fileReader.close();
        } catch (Exception e) {
            Log.e("Failed to generate group ctest mapping " + e.getMessage());
            Log.d2f("Failed to generate group ctest mapping " + e.getMessage());
            sGroupedMapping.clear();
            e.printStackTrace();
        }
    }


    public static Map<String, String> getInjectConfigPairs(String testName) {
        Map<String, String> defaultConfigPairs = ConfigLoader.getDefaultConfigMap();
        Map<String, String> prodConfigPairs = ConfigLoader.getProdConfigMap();
        Map<String, String> injectParis = new HashMap<String, String>();

        // find different configuration pairs
        for (Map.Entry<String, String> entry : prodConfigPairs.entrySet()) {
            String configName = entry.getKey();
            String configValue = entry.getValue();
            if (defaultConfigPairs.containsKey(configName) && !configValue.equals(defaultConfigPairs.get(configName))) {
                injectParis.put(configName, configValue);
            }
        }

        // filter out those configuration that can't be tested by testName
        Map<String, Set<String>> configMapping = getConfigMapping();
        Set<String> unTestableConfigSet = configMapping.get(testName);
        for (String config : injectParis.keySet()) {
            if (unTestableConfigSet.contains(config)) {
                injectParis.remove(config);
            }
        }
        return injectParis;
    }


    // simple test
    public static void main(String args[]) {
        Config.CTEST_MAPPING_FILE_PATH_V = "/Users/alenwang/Desktop/test1.json";
        generateGroupedMapping();
        for (Map.Entry<String, Set<String>> entry : sGroupedMapping.entrySet()) {
            System.out.println(entry.getKey());
            int count = 0;
            for (String s : entry.getValue()) {
                System.out.println(s);
                count ++;
            }
            System.out.println(count);
        }
    }
}
