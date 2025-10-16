package ui.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import models.Booking;
import models.Passenger;
import models.Train;
import models.User;
import services.BookingService;
import services.SeatService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentController {

    @FXML private GridPane passengerGrid;
    @FXML private CheckBox creditCardBox;
    @FXML private CheckBox mobileBankingBox;
    @FXML private CheckBox promptPayBox;
    @FXML private Button backButton;
    @FXML private Button payButton;
    @FXML private Label totalpricelabel;

    private User user;
    private Train train;
    private List<String> selectedSeats;
    private int passengerCount;

    private List<TextField> firstNameFields = new ArrayList<>();
    private List<TextField> lastNameFields = new ArrayList<>();
    private List<TextField> idCardFields = new ArrayList<>();

    private BookingService bookingService = new BookingService();
    private SeatService seatService = new SeatService();

    private List<Train> trainList;

    private double xOffset = 0;
    private double yOffset = 0;

    private double totalPrice;

    public void setData(User user, Train train, List<String> selectedSeats,
                        int passengerCount, List<Train> trainList, double totalPrice) {
        this.user = user;
        this.train = train;
        this.selectedSeats = selectedSeats;
        this.passengerCount = passengerCount;
        this.trainList = trainList;
        this.totalPrice = totalPrice;
        totalpricelabel.setText("฿: " + totalPrice);
        buildPassengerFields();
    }

    private void buildPassengerFields() {
        passengerGrid.getChildren().clear();
        passengerGrid.setPadding(new Insets(10));
        passengerGrid.setHgap(10);
        passengerGrid.setVgap(5);

        for (int i = 0; i < passengerCount; i++) {
            Label lbl1 = new Label("ชื่อจริง:");
            lbl1.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            TextField tf1 = new TextField();
            firstNameFields.add(tf1);

            Label lbl2 = new Label("นามสกุล:");
            lbl2.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            TextField tf2 = new TextField();
            lastNameFields.add(tf2);

            Label lbl3 = new Label("เลขประจำตัวประชาชน:");
            lbl3.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            TextField tf3 = new TextField();
            idCardFields.add(tf3);

            passengerGrid.add(lbl1, 0, i * 3);
            passengerGrid.add(tf1, 1, i * 3);
            passengerGrid.add(lbl2, 0, i * 3 + 1);
            passengerGrid.add(tf2, 1, i * 3 + 1);
            passengerGrid.add(lbl3, 0, i * 3 + 2);
            passengerGrid.add(tf3, 1, i * 3 + 2);
        }   
    }

    @FXML
    private void initialize() {
        backButton.setOnAction(e -> onBack());
        payButton.setOnAction(e -> onPay());
    }

    @FXML
    private void onBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/scenes/TrainList.fxml"));
            Parent root = loader.load();
            root.getStyleClass().add("window-border");
            playOpenWindowAnimation(root);

            TrainListController ctrl = loader.getController();
            ctrl.setTrains(trainList, user, passengerCount);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setTitle("Train List");

            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isValidThaiID(String id) {
        return id.matches("\\d{13}");
    }

    private boolean isValidName(String name) {
        return name.matches("^[\\p{IsThai}a-zA-Z]{2,}$");
    }

    private void onPay() {
        if (!seatService.areSeatsStillAvailable(train.getTrainId(), selectedSeats)) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "มีผู้ใช้ท่านอื่นจองที่นั่งบางส่วนไปแล้ว\nโปรดเลือกที่นั่งใหม่");
            alert.showAndWait();
            
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/scenes/SeatSelection.fxml"));
                Parent root = loader.load();
                playOpenWindowAnimation(root);

                SeatSelectionController ctrl = loader.getController();
                ctrl.setData(user, train, passengerCount, trainList);

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.setResizable(false);

                root.setOnMousePressed(event -> {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                });

                root.setOnMouseDragged(event -> {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                });

                stage.show();

                ((Stage) payButton.getScene().getWindow()).close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return;
        }

        for (int i = 0; i < passengerCount; i++) {
            String firstName = firstNameFields.get(i).getText().trim();
            String lastName = lastNameFields.get(i).getText().trim();
            String idCard = idCardFields.get(i).getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || idCard.isEmpty()) {
                showValidationError("โปรดกรอกข้อมูลให้ครบทุกช่องของผู้โดยสารคนที่ " + (i + 1));
                return;
            }

            if (!isValidName(firstName)) {
                showValidationError("ชื่อของผู้โดยสารคนที่ " + (i + 1) + " ไม่ถูกต้อง (ต้องเป็นตัวอักษรไทย/อังกฤษเท่านั้น และยาวอย่างน้อย 2 ตัวอักษร)");
                return;
            }

            if (!isValidName(lastName)) {
                showValidationError("นามสกุลของผู้โดยสารคนที่ " + (i + 1) + " ไม่ถูกต้อง (ต้องเป็นตัวอักษรไทย/อังกฤษเท่านั้น และยาวอย่างน้อย 2 ตัวอักษร)");
                return;
            }

            if (!isValidThaiID(idCard)) {
                showValidationError("เลขประจำตัวประชาชนของผู้โดยสารคนที่ " + (i + 1) + " ไม่ถูกต้อง (ต้องเป็นเลข 13 หลักเท่านั้น)");
                return;
            }
        }

        int count = 0;
        String paymentMethod = null;
        if (creditCardBox.isSelected()) { paymentMethod = "บัตรเครดิต"; count++; }
        if (mobileBankingBox.isSelected()) { paymentMethod = "Mobile Banking"; count++; }
        if (promptPayBox.isSelected()) { paymentMethod = "PromptPay"; count++; }
        if (count != 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "โปรดเลือกแค่หนึ่งวิธีการชำระเงิน");
            alert.showAndWait();
            return;
        }

        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < passengerCount; i++) {
            passengers.add(new Passenger(
                firstNameFields.get(i).getText().trim(),
                lastNameFields.get(i).getText().trim(),
                idCardFields.get(i).getText().trim()
            ));
        }

        String bookingId = "BK" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Booking booking = new Booking(
            bookingId,
            user.getUsername(),
            train.getTrainId(),
            selectedSeats,
            passengers,
            paymentMethod,
            totalPrice,
            false
        );

        bookingService.addBooking(booking);
        seatService.markSeatsAsBooked(train.getTrainId(), selectedSeats);

        Alert succ = new Alert(Alert.AlertType.INFORMATION,
                "ชำระเงินสำเร็จ! รหัสตั๋วของท่านคือ " + bookingId + "\nท่านสามารถดูข้อมูลอีกครั้งได้ที่หน้าประวัติการจอง");
        succ.showAndWait();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < passengers.size(); i++) {
            Passenger p = passengers.get(i);
            sb.append("ผู้โดยสาร ").append(i + 1).append(": ")
              .append(p.getFirstName()).append(" ").append(p.getLastName())
              .append(" (เลขบัตร: ").append(p.getIdCard())
              .append(") ที่นั่ง: ").append(selectedSeats.get(i)).append("\n");
        }
        Alert ticket = new Alert(Alert.AlertType.INFORMATION, sb.toString());
        ticket.setHeaderText("ตั๋วของท่าน");
        ticket.showAndWait();

        Stage st = (Stage) payButton.getScene().getWindow();
        st.close();
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ข้อมูลไม่ถูกต้อง");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void playOpenWindowAnimation(Node node) {
        node.setOpacity(0);
        node.setScaleX(0.9);
        node.setScaleY(0.9);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), node);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition settle = new ScaleTransition(Duration.millis(150), node);
        settle.setToX(1.0);
        settle.setToY(1.0);

        ParallelTransition appear = new ParallelTransition(fadeIn, scaleUp);
        SequentialTransition sequence = new SequentialTransition(appear, settle);
        sequence.play();
    }
}
