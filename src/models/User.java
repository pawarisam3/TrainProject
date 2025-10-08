package models;

public class User {
    private String firstName;
    private String lastName; 
    private String username;
    private String password;
    private String citizenId;
    private String birthDate; 
    private boolean isAdmin;

    public User(String firstName, String lastName, String username, String password,
                String citizenId, String birthDate, boolean isAdmin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.citizenId = citizenId;
        this.birthDate = birthDate;
        this.isAdmin = isAdmin;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getCitizenId() { return citizenId; }
    public String getBirthDate() { return birthDate; }
    public boolean isAdmin() { return isAdmin; }

    public String toCSV() {
        return String.join(",", firstName, lastName, username, password, citizenId, birthDate, isAdmin ? "1" : "0");
    } 

    public static User fromCSV(String line) {
        String[] parts = line.split(",");
        return new User(
            parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6].trim().equals("1")
        );
    }
}
