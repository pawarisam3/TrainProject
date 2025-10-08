package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

    public static List<String> readLines(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            lines = br.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void writeLines(String filename, List<String> lines) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            lines.forEach(writer::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
