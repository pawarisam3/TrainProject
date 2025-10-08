package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Train {
    private String trainId; 
    private String fromStation;
    private String toStation;
    private String departureTime;
    private String arrivalTime;
    private String type; 
    private int totalSeats;
    private List<String> serviceDates;
    private double basePrice;

    public Train(String trainId, String fromStation, String toStation,
            String departureTime, String arrivalTime, String type, int totalSeats,
            List<String> serviceDates, double basePrice) {
        this.trainId = trainId;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.type = type;
        this.totalSeats = totalSeats;
        this.serviceDates = serviceDates;
        this.basePrice = basePrice;
    }

    public String getTrainId() { return trainId; }
    public String getFromStation() { return fromStation; }
    public String getToStation() { return toStation; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public String getType() { return type; }
    public int getTotalSeats() { return totalSeats; }
    public List<String> getServiceDates() { return serviceDates; }
    public double getBasePrice() { return basePrice; }

    public String toCSV() {
        return String.join(",", trainId, fromStation, toStation,
            departureTime, arrivalTime, type,
            String.valueOf(totalSeats),
            String.join(";", serviceDates),
            String.valueOf(basePrice));
    }

    public static Train fromCSV(String line) {
        String[] parts = line.split(",");
        List<String> dates = new ArrayList<>();
        if (parts.length >= 8) {
            dates = Arrays.asList(parts[7].split(";"));
        }
        double basePrice = parts.length >= 9 ? Double.parseDouble(parts[8]) : 500;
        return new Train(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5],
                Integer.parseInt(parts[6]), dates, basePrice);
    }
}
