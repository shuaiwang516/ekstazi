package org.ekstazi.maven;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class MojoLog {
    public static void d2f (String s) {
        try {
            FileWriter fw = new FileWriter("mojo_debug.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(s);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void d2f (List<String> list) {
        try {
            FileWriter fw = new FileWriter("mojo_debug.txt", true);
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
