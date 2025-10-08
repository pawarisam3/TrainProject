package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;

public class DateUtils {

    public static boolean isValidDate(int day, int month, int year) {
        String dateString = String.format("%02d/%02d/%04d", day, month, year);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); 
        try {
            sdf.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static int calculateAge(int day, int month, int year) {
        LocalDate birthDate = LocalDate.of(year, month, day);
        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today)) {
            throw new IllegalArgumentException("เกิดในอนาคต? อะไรของมึง?");
        }
        return Period.between(birthDate, today).getYears();
    }
}
