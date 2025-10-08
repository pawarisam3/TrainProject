package services;

import models.SeatStatus;

import utils.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeatService {
    private final String seatFilePath = "seats.csv";
    public List<SeatStatus> loadSeatStatus() {
        List<SeatStatus> list = new ArrayList<>();
        List<String> lines = CSVUtils.readLines(seatFilePath);
        for (String line : lines) {
            try {
                SeatStatus ss = SeatStatus.fromCSV(line);
                list.add(ss);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public void saveSeatStatus(List<SeatStatus> list) {
    List<String> lines = list.stream().map(SeatStatus::toCSV).collect(Collectors.toList());
    CSVUtils.writeLines(seatFilePath, lines);
}


    public List<String> getBookedSeats(String trainId) {
        return loadSeatStatus().stream()
            .filter(s -> s.getTrainId().equals(trainId) && s.isBooked())
            .map(SeatStatus::getSeatNumber)
            .collect(Collectors.toList());
    }

    public void markSeatsAsBooked(String trainId, List<String> seatsToBook) {
        List<SeatStatus> allSeats = loadSeatStatus();
        boolean changed = false;

        for (String seat : seatsToBook) {
            boolean found = false;
            for (SeatStatus s : allSeats) {
                if (s.getTrainId().equals(trainId) && s.getSeatNumber().equals(seat)) {
                    s.setBooked(true);
                    found = true;
                    changed = true;
                    break;
                }
            }

            if (!found) {
                allSeats.add(new SeatStatus(trainId, seat, true));
                changed = true;
            }
        }

        if (changed) {
            saveSeatStatus(allSeats);
        }
    }

    public void unmarkSeats(String trainId, List<String> seatsToUnmark) {
        List<SeatStatus> allSeats = loadSeatStatus();
        boolean changed = false;

        for (SeatStatus s : allSeats) {
            if (s.getTrainId().equals(trainId) && seatsToUnmark.contains(s.getSeatNumber())) {
                s.setBooked(false);
                changed = true;
            }
        }

        if (changed) {
            saveSeatStatus(allSeats);
        }
    }

    public boolean areSeatsStillAvailable(String trainId, List<String> seatsToCheck) {
        List<String> bookedSeats = getBookedSeats(trainId);
        return seatsToCheck.stream().noneMatch(bookedSeats::contains);
    }
}
