package org.ekstazi.maven;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            if (firstEnterUnAffectedLog) {
                File logFolder = new File(NON_Affected_LOG_FOLDER);
                if (!logFolder.exists()) {
                    if(!logFolder.mkdir()) {
                        throw new IOException("Can't create unaffected log folder");
                    }
                }
                firstEnterUnAffectedLog = false;
            }
            FileWriter fw = new FileWriter(NON_Affected_LOG_FOLDER + "/" + roundIndex + ".txt", false);
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
