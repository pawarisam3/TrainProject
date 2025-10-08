package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Booking {
    private String bookingId;
    private String username;
    private String trainId;
    private List<String> seatNumbers;
    private List<Passenger> passengers;
    private String paymentMethod;
    private double totalPrice;
    private boolean cancelled;

    public Booking(String bookingId, String username, String trainId, List<String> seatNumbers, List<Passenger> passengers,
                   String paymentMethod, double totalPrice, boolean cancelled) {
        this.bookingId = bookingId;
        this.username = username;
        this.trainId = trainId;
        this.seatNumbers = seatNumbers;
        this.passengers = passengers;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.cancelled = cancelled;
    }

    public String getBookingId() { return bookingId; }
    public String getUsername() { return username; }
    public String getTrainId() { return trainId; }
    public List<String> getSeatNumbers() { return seatNumbers; }
    public List<Passenger> getPassengers() { return passengers; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalPrice() { return totalPrice; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public String toCSV() {
        String seats = String.join(";", seatNumbers);
        StringBuilder passStr = new StringBuilder();
        
        passengers.forEach(p -> passStr.append(p.toCSV()).append("|"));
            
        if (passStr.length() > 0) passStr.setLength(passStr.length() - 1);

        return String.join(",",
                bookingId,
                username,
                trainId,
                seats,
                passStr.toString(),
                paymentMethod,
                String.valueOf(totalPrice),
                cancelled ? "1" : "0");
    }

    public static Booking fromCSV(String line) {
        try {
            String[] parts = line.split(",", 8);
            if (parts.length < 8) throw new IllegalArgumentException("Invalid booking CSV line: " + line);

            String bookingId = parts[0];
            String username = parts[1];
            String trainId = parts[2];
            List<String> seats = Arrays.asList(parts[3].split(";"));
            List<Passenger> passengers = new ArrayList<>();
            if (!parts[4].isEmpty()) {
                for (String pStr : parts[4].split("\\|")) {
                    passengers.add(Passenger.fromCSV(pStr));
                }
            }
            String paymentMethod = parts[5];
            double price = Double.parseDouble(parts[6]);
            boolean cancelled = parts[7].equals("1");

            return new Booking(bookingId, username, trainId, seats, passengers, paymentMethod, price, cancelled);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
