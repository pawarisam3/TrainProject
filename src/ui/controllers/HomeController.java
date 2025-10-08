package ui.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import models.User;
import utils.Session;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HomeController {

    @FXML private Label welcomeuser;
    @FXML private Label clockLabel;
    @FXML private ImageView newsImageTop;
    @FXML private Label newsLabelBottom;
    @FXML private Label latestBookingLabel;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    @FXML
    public void initialize() {
        showWelcomeMessage();
        startLiveClock();
        playBounce(welcomeuser);
        setupNewsTextAnimation();
    }

    private void showWelcomeMessage() {
        User user = Session.getCurrentUser();
        if (user != null) {
            welcomeuser.setText("ยินดีต้อนรับคุณ " + user.getFullName());
        } else {
            welcomeuser.setText("ไม่พบชื่อผู้ใช้");
        }
    }

    private void startLiveClock() {
        Timeline clock = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                String formattedTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy"));
                clockLabel.setText("เวลาปัจจุบัน: " + formattedTime);
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

    private void setupNewsTextAnimation() {

        TranslateTransition textTransition = new TranslateTransition(Duration.seconds(10), newsLabelBottom);
        textTransition.setFromX(900);
        textTransition.setToX(-600);
        textTransition.setCycleCount(TranslateTransition.INDEFINITE);
        textTransition.setAutoReverse(false);
        textTransition.play();
    }
}
