package models;

public class SeatStatus {
    private String trainId;
    private String seatNumber;
    private boolean booked;

    public SeatStatus(String trainId, String seatNumber, boolean booked) {
        this.trainId = trainId;
        this.seatNumber = seatNumber;
        this.booked = booked;
    }

    public String getTrainId() { return trainId; }
    public String getSeatNumber() { return seatNumber; }
    public boolean isBooked() { return booked; }
    
    public void setBooked(boolean booked) { this.booked = booked; } 

    public String toCSV() { return trainId + "," + seatNumber + "," + booked; }
    
    public static SeatStatus fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length == 3) {
            String trainId = parts[0];
            String seatNumber = parts[1];
            boolean booked = Boolean.parseBoolean(parts[2]);
            return new SeatStatus(trainId, seatNumber, booked);
        }
        return null;
    }
}
