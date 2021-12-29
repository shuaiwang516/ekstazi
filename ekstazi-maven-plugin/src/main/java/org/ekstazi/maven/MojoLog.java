package org.ekstazi.maven;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class MojoLog {

    public static final String MOJO_LOG_FILE = "mojo_debug.txt";
    private static Boolean mojoLogEnabled = true;

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
}
