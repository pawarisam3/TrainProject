package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import models.Booking;
import models.Passenger;
import models.Train;
import services.BookingService;
import services.SeatService;
import services.TrainService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BookingDetailsController {

    @FXML private Label bookingIdLabel;
    @FXML private Label trainIdLabel;
    @FXML private Label fromStationLabel;
    @FXML private Label toStationLabel;
    @FXML private Label travelDateLabel;
    @FXML private Label trainTypeLabel;
    @FXML private Label seatLabel;
    @FXML private Label paymentMethodLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Label statusLabel;
    @FXML private Label cancelledLabel;
    @FXML private VBox passengerListVBox;

    @FXML private ImageView qrCodeImageView;

    @FXML private Button backButton;
    @FXML private Button cancelButton;

    private Booking booking;
    private Train train;

    private BookingService bookingService = new BookingService();
    private TrainService trainService = new TrainService();
    private SeatService seatService = new SeatService();

    private MainFrameController mainController;

    public void setBooking(Booking booking) {
        this.booking = booking;
        this.train = trainService.getTrainById(booking.getTrainId());
        showBookingDetails();
    }

    public void setMainController(MainFrameController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        cancelledLabel.setVisible(false);
    }

    private void showBookingDetails() {
        bookingIdLabel.setText(booking.getBookingId());
        trainIdLabel.setText(String.valueOf(booking.getTrainId()));
        fromStationLabel.setText(train != null ? train.getFromStation() : "ไม่พบ");
        toStationLabel.setText(train != null ? train.getToStation() : "ไม่พบ");
        travelDateLabel.setText((train != null && !train.getServiceDates().isEmpty()) ? train.getServiceDates().get(0) : "ไม่พบ");
        trainTypeLabel.setText(train != null ? train.getType() : "ไม่พบ");
        seatLabel.setText(String.join(", ", booking.getSeatNumbers()));
        paymentMethodLabel.setText(booking.getPaymentMethod());
        totalPriceLabel.setText(booking.getTotalPrice() + " บาท");
        statusLabel.setText(booking.isCancelled() ? "ยกเลิกแล้ว" : "ใช้งานได้");
        cancelledLabel.setVisible(booking.isCancelled());

        passengerListVBox.getChildren().clear();
        for (Passenger p : booking.getPassengers()) {
            Label passengerLabel = new Label(
                p.getFirstName() + " " + p.getLastName() +
                " - เลขประจำตัวประชาชน: " + p.getIdCard());
            passengerListVBox.getChildren().add(passengerLabel);
        }

        cancelButton.setVisible(!booking.isCancelled());
        loadQRCodeImage();
    }

    private void loadQRCodeImage() {
    try {
        String data = booking.getBookingId();
        String encodedData = URLEncoder.encode(data, StandardCharsets.UTF_8.toString());
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + encodedData;

        Image qrImage = new Image(qrUrl, true);
        qrCodeImageView.setImage(qrImage);

    } catch (Exception e) {
        e.printStackTrace();
    }
}


    @FXML
    private void onBack() {
        mainController.loadContent("/ui/scenes/BookingHistory.fxml");
    }

    @FXML
    private void onCancelBooking() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("ยืนยันการยกเลิกตั๋ว");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("คุณแน่ใจที่จะยกเลิกตั๋วหรือไม่ การดำเนินการนี้จะคืนเงินให้คุณ 90% จากราคาเดิม");

        confirmAlert.showAndWait().ifPresent(e -> {
            if (e == ButtonType.OK) {
                booking.setCancelled(true);
                seatService.unmarkSeats(train.getTrainId(), booking.getSeatNumbers());

                bookingService.getBookings().removeIf(b -> b.getBookingId().equals(booking.getBookingId()));
                bookingService.getBookings().add(booking);
                bookingService.saveBookings();

                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("ยกเลิกแล้ว");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("การจองยกเลิกแล้ว คืนเงิน: " + (booking.getTotalPrice() * 0.9) + " บาท");
                infoAlert.showAndWait();
                mainController.loadContent("/ui/scenes/BookingHistory.fxml");
            }
        });
    }
}
