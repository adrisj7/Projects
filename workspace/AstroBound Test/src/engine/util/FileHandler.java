package engine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileHandler {

    public static String readTextFile(String fname) {
        StringBuilder builder = new StringBuilder();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(new File(fname)));
            String line;
            while( (line = br.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            br.close();
            return builder.toString();
        } catch(FileNotFoundException e) {
            System.err.println("[FileHandler.java] No file at " + fname + " found!");
            return "";
        } catch (IOException e) {
            System.err.println("[FileHandler.java] Other IO exception ruh roh");
            e.printStackTrace();
            return "";
        }
    }
}
