package ui.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import models.Train;
import models.User;
import services.SeatService;
import utils.SeatPriceCalculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionController {

    @FXML private GridPane seatGrid;
    @FXML private Button backButton;
    @FXML private Button confirmButton;

    private User user;
    private Train train;
    private int passengerCount;
    private List<String> selectedSeats = new ArrayList<>();

    private SeatService seatService = new SeatService();
    private List<Train> trainList;
    
    private double xOffset = 0;
    private double yOffset = 0;

    public void setData(User user, Train train, int passengerCount, List<Train> trainList) {
        this.user = user;
        this.train = train;
        this.passengerCount = passengerCount;
        this.trainList = trainList;
        populateSeats();
    }

    private void populateSeats() {
        seatGrid.getChildren().clear();

        List<String> booked = seatService.getBookedSeats(train.getTrainId());

        int totalSeats = train.getTotalSeats();
        int seatsPerRow = 4;
        int currentRow = 0;

        Label leftLabel = new Label("หน้าต่าง (ซ้าย)");
        leftLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        seatGrid.add(leftLabel, 0, currentRow, 2, 1); 

        Label divider = new Label(""); 
        seatGrid.add(divider, 2, currentRow);

        Label rightLabel = new Label("หน้าต่าง (ขวา)");
        rightLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        seatGrid.add(rightLabel, 3, currentRow, 2, 1); 

        currentRow++; 

        for (int i = 1; i <= totalSeats; i += seatsPerRow) {
            for (int col = 0; col < seatsPerRow; col++) {
                int seatNumber = i + col;
                if (seatNumber > totalSeats) break;

                String seatLabel = "ที่นั่งที่ " + seatNumber;
                Button seatBtn = new Button(seatLabel);
                seatBtn.setPrefWidth(100);
                seatBtn.setPrefHeight(35);
                seatBtn.setStyle("-fx-background-radius: 10; -fx-font-size: 12px;");

                seatBtn.setOnAction(e -> {
                    if (!seatBtn.isDisabled()) {
                        if (selectedSeats.contains(seatLabel)) {
                            selectedSeats.remove(seatLabel);
                            seatBtn.setStyle(seatBtn.getStyle().replace("yellow", "lightgreen"));
                        } else {
                            if (selectedSeats.size() >= passengerCount) {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "ท่านสามารถเลือกที่นั่งได้ " + passengerCount + " ที่นั่งเท่านั้น");
                                alert.showAndWait();
                            } else {
                                selectedSeats.add(seatLabel);
                                seatBtn.setStyle(seatBtn.getStyle().replace("lightgreen", "yellow"));
                            }
                        }
                    }
                });

                if (booked.contains(seatLabel)) {
                    seatBtn.setStyle(seatBtn.getStyle() + "-fx-background-color: red; -fx-text-fill: white;");
                    seatBtn.setDisable(true);
                } else if (selectedSeats.contains(seatLabel)) {
                    seatBtn.setStyle(seatBtn.getStyle() + "-fx-background-color: yellow;");
                } else {
                    seatBtn.setStyle(seatBtn.getStyle() + "-fx-background-color: lightgreen;");
                }

                int gridCol = (col < 2) ? col : col + 1;
                seatGrid.add(seatBtn, gridCol, currentRow);
            }

            Label centerDivider = new Label("|");
            centerDivider.setStyle("-fx-text-fill: gray; -fx-font-size: 18px;");
            seatGrid.add(centerDivider, 2, currentRow); 

            currentRow++;
        }
    }

    @FXML
    private void initialize() {
        backButton.setOnAction(e -> onBack());
        confirmButton.setOnAction(e -> onConfirm());
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

    @FXML
    private void onConfirm() {
        if (selectedSeats.size() != passengerCount) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "โปรดเลือกที่นั่งให้ครบ " + passengerCount + " ที่นั่ง");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/scenes/Payment.fxml"));
            Parent root = loader.load();
            root.getStyleClass().add("window-border");
            playOpenWindowAnimation(root);

            double totalPrice = 0;
            for (String seat : selectedSeats) {
                totalPrice += SeatPriceCalculator.calculatePrice(seat, train.getBasePrice());
            }

            PaymentController ctrl = loader.getController();
            ctrl.setData(user, train, selectedSeats, passengerCount, trainList, totalPrice);

            Stage stage = (Stage) confirmButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setTitle("ชำระเงิน");

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
            Alert alert = new Alert(Alert.AlertType.ERROR, "ไม่สามารถโหลดหน้าชำระเงินได้");
            alert.showAndWait();
        }
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
