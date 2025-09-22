package Utils;

import java.text.*;
import java.util.*;

public class DateUtil {
    public static boolean isValidDate(int day, int month, int year) {
        try {
            String dateString = String.format("%02d/%02d/%02d", day, month, year);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            sdf.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static int calculateAge(int day, int month, int year) { // คำนวณอายุ ใช้วันที่ปัจจุบัน - วันเกิด
        Calendar birth = Calendar.getInstance(); // จัดการเวลา getInstanceเอาวันที่ ณ ปัจจุบันของจริง
        birth.set(year, month - 1, day); // ที่ -1 เดือน เริ่มจาก 0 จาก Calender

        Calendar today = Calendar.getInstance(); // วันปัจจุบัน

        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR); // คิดอายุ ว/ด/ป ปัจจุบัน - ว/ด/ป ที่เกิด
        if (today.get(Calendar.MONTH) < birth.get(Calendar.MONTH) || // ถ้า ปัจจุบันเดือนน้อยกว่า เดือนเกิด หรือ ถ้า
                                                                     // เดือนปัจจุบันเท่ากับวันเกิด
                                                                     // และวันปัจจุบันน้อยกว่าวันเกิด
                (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH) &&
                        today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))) {
            age--; // อายุ -1 เพราะถ้ายังไม่ถึงวันเกิด
        }
        return age;
    }
}
