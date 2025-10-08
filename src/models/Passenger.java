package models;

public class Passenger {
    private String firstName;
    private String lastName;
    private String idCard;

    public Passenger(String firstName, String lastName, String idCard) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.idCard = idCard;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getIdCard() { return idCard; }

    public String toCSV() { return firstName + ";" + lastName + ";" + idCard; }

    public static Passenger fromCSV(String str) {
        String[] parts = str.split(";", 3);
        return new Passenger(parts[0], parts[1], parts[2]);
    }
}
