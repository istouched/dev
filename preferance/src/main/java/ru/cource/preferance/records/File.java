package ru.cource.preferance.records;

import java.io.FileWriter;
import java.io.IOException;

public class File {
    private static FileWriter writer;

    public static void writeFile(String log){
        try {
            writer  = new FileWriter("src/main/resources/file.txt", true);
            writer.write(log);
            writer.flush();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }
    public static void logFile(String log){
        try {
            writer  = new FileWriter("src/main/resources/log.txt", true);
            writer.write("\n"+log);
            writer.flush();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
