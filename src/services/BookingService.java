package services;

import models.Booking;
import utils.CSVUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BookingService {
    private static final String BOOKING_FILE = "bookings.csv";
    private List<Booking> bookings;

    public BookingService() {
        bookings = CSVUtils.readLines(BOOKING_FILE).stream()
                .map(Booking::fromCSV)
                .filter(b -> b != null)
                .collect(Collectors.toList());
    }

    public boolean addBooking(Booking booking) {
        bookings.add(booking);
        saveBookings();
        return true;
    }

    public void saveBookings() {
        List<String> lines = bookings.stream()
                .map(Booking::toCSV)
                .collect(Collectors.toList());
        CSVUtils.writeLines(BOOKING_FILE, lines);
    }

    public List<Booking> getBookings() { return bookings; }

    public String generateBookingId() {
        return "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
