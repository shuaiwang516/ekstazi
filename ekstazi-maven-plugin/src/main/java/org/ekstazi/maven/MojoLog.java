package org.ekstazi.maven;

import org.ekstazi.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MojoLog {

    public static final String MOJO_LOG_FILE = "mojo_debug.txt";
    private static Boolean mojoLogEnabled = true;
    private static Boolean NONAffectedLogEnabled = true;
    private static Boolean firstEnterUnAffectedLog = true;
    public static String NON_Affected_LOG_FOLDER = "NonAffectedLog";


    public static void d2f (String s) {
        if (!mojoLogEnabled)
            return;
        try {
            FileWriter fw = new FileWriter(MOJO_LOG_FILE, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(s);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void d2f (List<String> list) {
        if (!mojoLogEnabled)
            return;
        try {
            FileWriter fw = new FileWriter(MOJO_LOG_FILE, true);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String s : list) {
                bw.write(s);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unAffectedLog(String roundIndex, List<String> nonAffectedClassesFromPrev, List<String> nonAffectedClassesFromCurRound) {
        if (!NONAffectedLogEnabled)
            return;
        try {
            String curRoot = Config.getCurRoot();
            String logFolderPath = Paths.get(curRoot, NON_Affected_LOG_FOLDER).toAbsolutePath().toString();
            d2f("[DEBUG] root = " + logFolderPath);
            if (firstEnterUnAffectedLog) {
                File logFolder = new File(logFolderPath);
                if (!logFolder.exists()) {
                    if(!logFolder.mkdir()) {
                        throw new IOException("Can't create unaffected log folder");
                    }
                }
                firstEnterUnAffectedLog = false;
            }
            Path logFile = Paths.get(logFolderPath, roundIndex + ".txt");
            FileWriter fw = new FileWriter(logFile.toFile(), false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("==============================UNAFFECTED FROM PREV==============================\n");
            for (String prev : nonAffectedClassesFromPrev) {
                bw.write(prev + "\n");
            }
            bw.write("==============================UNAFFECTED FROM CURR==============================\n");
            for (String cur : nonAffectedClassesFromCurRound) {
                bw.write(cur + "\n");
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
