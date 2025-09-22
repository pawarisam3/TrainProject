package Utils;

import java.io.*;
import java.util.*;

public class CSVUtil {
    public static List<String> readLines(String filename) { // static ใช้ได้เลย ไม่ต้องสร้าง obj มาใหม่
        List<String> lines = new ArrayList<>();
        // อ่าน
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim()); // trim ใช้ ไม่มีเว้นว่าง
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void writeLines(String filename, List<String> lines) {
        // เขียน กรณีหลายตัว
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (String line : lines) {
                writer.println(line); // เขียนเป็นไฟล์ จนกว่าจะหมด list ที่ส่งเข้ามา
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendLine(String filename, String line) {
        // เขียนแบบตัวเดียว บรรทัดเดียว
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
