package org.ekstazi.configAware;

import org.ekstazi.Config;
import org.ekstazi.log.Log;
import org.ekstazi.util.FileUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ConfigLoader {
    private static final Map<String, String > sExercisedConfigMap = new HashMap<String, String>();

    public static Map<String, String> getUserConfigMap() {
        return sExercisedConfigMap;
    }

    public static Boolean hasConfigFile() {
        String configFileName = Config.CONFIG_FILE_PATH_V;
        File configFile = new File(configFileName);
        return configFile.exists();
    }

    /**
     *  Load user's configuration file
     *  TODO: Need to consider different types of configuration files
     */
    public static void loadConfigFromFile() {
        String configFileName = Config.CONFIG_FILE_PATH_V;
        load0(configFileName);
    }

    private static void load0(String filename) {
        InputStream is = null;
        try {
            is = new FileInputStream(filename);
            parseConfigurationFile(filename, is);
            //Log.d2f("Configuration file is loaded.");
            Log.printConfig(sExercisedConfigMap, "loadMethod");
        } catch (IOException e) {
            Log.e("Loading configuration is not successful", e);
            sExercisedConfigMap.clear();
        } finally {
            FileUtil.closeAndIgnoreExceptions(is);
        }
    }

    private static String getFileSuffix(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private static void parseConfigurationFile(String filename, InputStream is) throws IOException {
        String fileSuffix = getFileSuffix(filename).toLowerCase();
        switch (fileSuffix) {
            case "xml":
                loadFromXML(is);
                break;
            case "properties":
                loadFromProperties();
                break;
            case "cfg":
                loadFromCFG();
                break;
            default:
                Log.e("Can't load configuration from ." + fileSuffix + " file");
                throw new IOException();
        }
    }

    private static void loadFromXML(InputStream is) {
        parseXML(is, "property", "name", "value");
    }

    private static void loadFromProperties() {

    }

    private static void loadFromCFG() {

    }


    public static void parseXML(InputStream is, String tagName, String tagConfigName, String tagConfigValue) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            NodeList nl = doc.getElementsByTagName(tagName);
            for (int i = 0; i < nl.getLength(); i++) {
                String configName = doc.getElementsByTagName(tagConfigName).item(i).getFirstChild().getNodeValue();
                String configValue;
                try {
                    configValue = doc.getElementsByTagName(tagConfigValue).item(i).getFirstChild().getNodeValue();
                } catch (NullPointerException e) {
                    configValue = "";
                }
                sExercisedConfigMap.put(configName, configValue);
                //System.out.println(configName + " , " + configValue);
            }
        } catch (Exception e) {
            System.out.println("Loading configuration is not successful:");
            e.printStackTrace();
            Log.e("Loading configuration is not successful", e);
            sExercisedConfigMap.clear();
        }
    }

    // For simply test
    public static void main(String args[]) {
        //load0("/Users/alenwang/Documents/xlab/hadoop/hadoop-common-project/hadoop-kms/src/main/conf/kms-site.xml");
        load0("/Users/alenwang/Documents/xlab/hadoop/hadoop-common-project/hadoop-common/src/main/resources/core-default.xml");
        int count = 0;
        for(Map.Entry<String, String> entry : sExercisedConfigMap.entrySet()) {
            count += 1;
            System.out.println(entry.getKey() + " , " + entry.getValue());
        }
        System.out.println(count);
    }
}
