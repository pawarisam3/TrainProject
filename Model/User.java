package Model;

public class User {
    private String firstName; // เก็บ ชื่อจริง
    private String lastName;
    private String username;
    private String password;
    private String citizenId; // เลขบัตรประชาชน
    private String birthDate;
    private boolean isAdmin;

    public User(String firstName, String lastName, String username, String password, String citizenId, String birthDate,
            boolean isAdmin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.citizenId = citizenId;
        this.birthDate = birthDate;
        this.isAdmin = isAdmin;
    }

    public String getuserName() {
        return username;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getpassword() {
        return password;
    }

    public String getcitizenId() {
        return citizenId;
    }

    public String getbirthDate() {
        return birthDate;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String TOCSV() { // เอาไปเก็บไว้ในไฟล์ User.csv
        return String.join(",", firstName, lastName, username, password, citizenId, birthDate, isAdmin ? "1" : "0"); // join
                                                                                                                     // ,ใช้เชื่อม
                                                                                                                     // ถ้าเป็น
                                                                                                                     // admin
                                                                                                                     // 1
                                                                                                                     // ถ้าไม่เป็น
                                                                                                                     // 0
    }

    public static User fromCSV(String line) { // โหลดข้อมูลจาก CSV แยกข้อมูลด้วย ,
        String[] parts = line.split(","); // ใช้เป็นคำสั่งแยก ในที่นี้แยกด้วย ,
        return new User(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6].trim().equals("1")); // part
                                                                                                                  // 6 1
                                                                                                                  // ใช้เทียบว่า
                                                                                                                  // ถ้าเป็นจริง
                                                                                                                  // แสดงว่า
                                                                                                                  // แอดมิน
    }
}