package org.ekstazi.configAware;

import org.ekstazi.Config;
import org.ekstazi.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigInjector {

    public static void injectConfig(Map<String, String> configPairs) {
        String[] splitedFileName = Config.CONFIG_INJECT_FILE_PATH_V.split("\\.");
        String fileType = splitedFileName[splitedFileName.length - 1];

        switch (fileType.toLowerCase()) {
            case "xml":
                injectConfigXML(configPairs);
                break;
            default:
                Log.d2f("[ERROR] Failed to inject configuration : Do not support " + fileType.toLowerCase() + " file");
                Log.e("Failed to inject configuration : Do not support " + fileType.toLowerCase() + " file");
                break;
        }
    }

    private static void injectConfigXML(Map<String, String> configPairs) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root element and add xml declaration
            Document doc = docBuilder.newDocument();
            // TODO: Here is a hardcoded for Hadoop softwares, we may want to support more softwares in the future.
            ProcessingInstruction pi1 = doc.createProcessingInstruction("xml", "version=\"1.0\"");
            ProcessingInstruction pi2 = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"configuration.xsl\"");
            doc.insertBefore(pi1, doc.getDocumentElement());
            doc.insertBefore(pi2, doc.getDocumentElement());

            //addingStylesheet(doc);
            Element rootElement = doc.createElement("configuration");
            doc.appendChild(rootElement);

            for (Map.Entry<String, String> configPair : configPairs.entrySet()) {
                // add xml elements and add property to root
                Element property = doc.createElement("property");
                rootElement.appendChild(property);
                // add name and value to property
                Element name = doc.createElement("name");
                Element value = doc.createElement("value");
                name.setTextContent(configPair.getKey());
                value.setTextContent(configPair.getValue());
                property.appendChild(name);
                property.appendChild(value);
            }
            OutputStream os = new FileOutputStream(Config.CONFIG_INJECT_FILE_PATH_V);
            writeXML(doc, os);
            os.close();
        } catch (Exception e) {
            Log.d2f("[ERROR] Failed to inject configuration " + e.getMessage());
            Log.e("Failed to inject configuration " + e.getMessage());
        }
    }

    private static void writeXML(Document doc, OutputStream output) throws TransformerException, IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);
    }


    // simple test
    public static void main (String args[]) {
        Map<String, String> configPairs = new HashMap<>();
        configPairs.put("univeristy", "UIUC");
        configPairs.put("person.name", "shuai");
        configPairs.put("person.age", "23");
        configPairs.put("person.info", "");
        Config.CONFIG_INJECT_FILE_PATH_V = "/Users/alenwang/Desktop/generatedXML.xml";
        injectConfig(configPairs);
    }
}
