package ui.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import models.Train;
import models.User;
import services.TrainService;

import java.io.IOException;
import java.util.List;

public class SearchController {

    @FXML private TextField fromStationField;
    @FXML private ComboBox<String> fromStationCombo;
    @FXML private TextField toStationField;
    @FXML private ComboBox<String> toStationCombo;
    @FXML private ComboBox<Integer> dayCombo;
    @FXML private ComboBox<Integer> monthCombo;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private TextField passengerCountField;
    @FXML private Button searchButton;
    @FXML private Label departurelabel;
    @FXML private Label arrivallabel;
    @FXML private Label traveldatelabel;
    @FXML private Label passengerslabel;

    private User user;

    private double xOffset = 0;
    private double yOffset = 0;

    public void setUser(User user) {
        this.user = user;
        initializeData();
    }

    @FXML
    private void initialize() {
        for (int i = 1; i <= 31; i++) dayCombo.getItems().add(i);
        for (int i = 1; i <= 12; i++) monthCombo.getItems().add(i);
        yearCombo.getItems().add(2025);
        searchButton.setOnAction(e -> onSearch());

        playBounce(fromStationField);
        playBounce(departurelabel);
        playBounce(arrivallabel);
        playBounce(traveldatelabel);
        playBounce(passengerslabel);
    }

    private void initializeData() {
        if (user == null) return;

        TrainService trainService = new TrainService();
        List<String> stationsList = trainService.getAllStations();

        fromStationCombo.getItems().clear();
        fromStationCombo.getItems().add("สถานีต้นทาง");
        fromStationCombo.getItems().addAll(stationsList);

        toStationCombo.getItems().clear();
        toStationCombo.getItems().add("สถานีปลายทาง");
        toStationCombo.getItems().addAll(stationsList);
    }

    private void onSearch() {
        String fromStation = fromStationField.getText().trim();
        if (fromStation.isEmpty() && fromStationCombo.getSelectionModel().getSelectedIndex() > 0) {
            fromStation = fromStationCombo.getValue();
        }

        String toStation = toStationField.getText().trim();
        if (toStation.isEmpty() && toStationCombo.getSelectionModel().getSelectedIndex() > 0) {
            toStation = toStationCombo.getValue();
        }

        if (fromStation == null || fromStation.isEmpty() || toStation == null || toStation.isEmpty()) {
            showAlert(AlertType.ERROR, "ผิดพลาด!", "โปรดเลือกทั้งสถานีต้นทางและปลายทาง");
            return;
        }

        Integer day = dayCombo.getValue();
        Integer month = monthCombo.getValue();
        Integer year = yearCombo.getValue();
        if (day == null || month == null || year == null) {
            showAlert(AlertType.ERROR, "ผิดพลาด!", "โปรดเลือกวันที่ที่ถูกต้อง");
            return;
        }
        String date = String.format("%04d-%02d-%02d", year, month, day);

        int passengerCount;
        try {
            passengerCount = Integer.parseInt(passengerCountField.getText().trim());
            if (passengerCount <= 0 || passengerCount >= 4) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "ผิดพลาด!", "โปรดกรอกจำนวนผู้โดยสารที่ถูกต้อง(ไม่เกิน 3 คน)");
            return;
        }

        TrainService trainService = new TrainService();
        List<Train> trains = trainService.searchTrains(fromStation, toStation, date);

        if (trains.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "ไม่พบ", "ไม่พบเที่ยวรถไฟที่ท่านค้นหา");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/scenes/TrainList.fxml"));
            Parent root = loader.load();
            root.getStyleClass().add("window-border");
            playOpenWindowAnimation(root);

            TrainListController controller = loader.getController();
            controller.setTrains(trains, user, passengerCount);

            Stage stage = new Stage();
            stage.setTitle("ผลการค้นหาเที่ยวรถไฟ");

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); 

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(searchButton.getScene().getWindow());
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void playBounce(Node node) {
        ScaleTransition bounceOut = new ScaleTransition(Duration.millis(1000), node);
        bounceOut.setToX(1.1);
        bounceOut.setToY(1.1);

        ScaleTransition bounceBack = new ScaleTransition(Duration.millis(1000), node);
        bounceBack.setToX(1.0);
        bounceBack.setToY(1.0);

        SequentialTransition bounceSequence = new SequentialTransition(bounceOut, bounceBack);
        bounceSequence.play();
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
