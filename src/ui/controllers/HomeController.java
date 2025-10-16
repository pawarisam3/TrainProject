package ui.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import models.Booking;
import models.Train;
import models.User;
import services.BookingService;
import services.TrainService;
import utils.Session;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class HomeController {

    @FXML private Label welcomeuser;
    @FXML private Label clockLabel;
    @FXML private ImageView newsImageTop;
    @FXML private Label newsLabelBottom;
    @FXML private Label latestBookingLabel;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    private List<String> imageFilenames;
    private int currentImageIndex = 0;
    private Timeline imageSlider;

    private BookingService bookingService = new BookingService();

    @FXML
    public void initialize() {
        showWelcomeMessage();
        startLiveClock();
        playBounce(welcomeuser);
        setupNewsTextAnimation();
        showLatestBooking();
        setupNewsSlideshow();
    }

    private void showWelcomeMessage() {
        User user = Session.getCurrentUser();
        if (user != null) {
            welcomeuser.setText("Welcome " + user.getFullName() + "!");
        } else {
            welcomeuser.setText("ไม่พบชื่อผู้ใช้");
        }
    }

    private void startLiveClock() {
        Timeline clock = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                String formattedTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                clockLabel.setText(formattedTime);
            }),
            new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void playBounce(Node node) {
        ScaleTransition bounceOut = new ScaleTransition(Duration.millis(300), node);
        bounceOut.setToX(1.1);
        bounceOut.setToY(1.1);

        ScaleTransition bounceBack = new ScaleTransition(Duration.millis(150), node);
        bounceBack.setToX(1.0);
        bounceBack.setToY(1.0);

        new SequentialTransition(bounceOut, bounceBack).play();
    }

    private void showLatestBooking() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            latestBookingLabel.setText("กรุณาเข้าสู่ระบบ");
            latestBookingLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 20px;");
            return;
        }

        List<Booking> userBookings = bookingService.getBookings().stream()
                .filter(b -> b.getUsername().equalsIgnoreCase(currentUser.getUsername()))
                .sorted((a, b) -> b.getBookingId().compareTo(a.getBookingId()))
                .toList();

        if (userBookings.isEmpty()) {
            latestBookingLabel.setText("คุณยังไม่มีการจองตั๋ว");
            latestBookingLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 20px;");
            return;
        }

        Booking latestBooking = userBookings.get(0);
        Train train = new TrainService().getTrainById(latestBooking.getTrainId());

        String fromStation = train != null ? train.getFromStation() : "ไม่พบต้นทาง";
        String toStation = train != null ? train.getToStation() : "ไม่พบปลายทาง";
        String dateStr = (train != null && !train.getServiceDates().isEmpty()) ? train.getServiceDates().get(0) : null;
        String status = latestBooking.isCancelled() ? " (ยกเลิกแล้ว)" : "";

        String dateInfo;
        String style;

        if (dateStr != null) {
            try {
                LocalDate travelDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate now = LocalDate.now();

                if (travelDate.isBefore(now)) {
                    dateInfo = "เลยวันเดินทางแล้ว";
                    style = "-fx-text-fill: red; -fx-font-size: 20px;";
                } else {
                    long daysLeft = ChronoUnit.DAYS.between(now, travelDate);
                    dateInfo = String.format("เหลืออีก %d วัน", daysLeft);
                    style = "-fx-text-fill: #00ff00; -fx-font-size: 20px;";
                }
            } catch (Exception e) {
                dateInfo = "ไม่สามารถแปลงวันที่ได้";
                style = "-fx-text-fill: yellow; -fx-font-size: 20px;";
            }
        } else {
            dateInfo = "ไม่พบวันที่เดินทาง";
            style = "-fx-text-fill: yellow; -fx-font-size: 20px;";
        }

        String displayText = String.format("%s → %s | วันที่เดินทาง: %s%s | %s",
                fromStation, toStation, dateStr != null ? dateStr : "ไม่พบ", status, dateInfo);

        latestBookingLabel.setText(displayText);
        latestBookingLabel.setStyle(style);
    }

    private void setupNewsSlideshow() {
        try {
            File folder = new File(Objects.requireNonNull(getClass().getResource("/ui/scenes/news-images")).toURI());
            imageFilenames = List.of(Objects.requireNonNull(folder.list()));
            if (imageFilenames.isEmpty()) return;

            showCurrentImage();

            imageSlider = new Timeline(new KeyFrame(Duration.seconds(5), e -> showNextImage()));
            imageSlider.setCycleCount(Timeline.INDEFINITE);
            imageSlider.play();

            prevButton.setOnAction(e -> {
                imageSlider.pause();
                showPreviousImage();
                imageSlider.playFromStart();
            });

            nextButton.setOnAction(e -> {
                imageSlider.pause();
                showNextImage();
                imageSlider.playFromStart();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCurrentImage() {
        if (imageFilenames == null || imageFilenames.isEmpty()) return;
        String filename = imageFilenames.get(currentImageIndex);
        Image image = new Image("/ui/scenes/news-images/" + filename);
        newsImageTop.setImage(image);
    }

    private void showNextImage() {
        currentImageIndex = (currentImageIndex + 1) % imageFilenames.size();
        showCurrentImage();
    }

    private void showPreviousImage() {
        currentImageIndex = (currentImageIndex - 1 + imageFilenames.size()) % imageFilenames.size();
        showCurrentImage(); 
    }

    private void setupNewsTextAnimation() {

        TranslateTransition textTransition = new TranslateTransition(Duration.seconds(10), newsLabelBottom);
        textTransition.setFromX(900);
        textTransition.setToX(-600);
        textTransition.setCycleCount(TranslateTransition.INDEFINITE);
        textTransition.setAutoReverse(false);
        textTransition.play();
    }
}
