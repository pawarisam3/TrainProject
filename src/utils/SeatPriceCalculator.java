package utils;

public class SeatPriceCalculator {

    public static String getSeatClass(int seatNumber) {
        if (seatNumber <= 20) return "ชั้น 1";
        if (seatNumber <= 40) return "ชั้น 2";
        return "ชั้น 3";
    }

    public static double calculatePrice(double basePrice, int seatNumber) {
        if (seatNumber <= 20) return basePrice * 1.5;
        if (seatNumber <= 40) return basePrice * 1.2;   
        return basePrice;   
    }

    public static double calculatePrice(String seatLabel, double basePrice) {
        try {
            int seatNumber = Integer.parseInt(seatLabel.replaceAll("[^0-9]", ""));
            return calculatePrice(basePrice, seatNumber);
        } catch (NumberFormatException e) {
            return basePrice;
        }
    }
}
