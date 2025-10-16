package ui.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Train;
import models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrainListController {

    @FXML private TableView<Train> trainTable;
    @FXML private TableColumn<Train, String> colTrainId;
    @FXML private TableColumn<Train, String> colFromStation;
    @FXML private TableColumn<Train, String> colToStation;
    @FXML private TableColumn<Train, String> colDepartureTime;
    @FXML private TableColumn<Train, String> colArrivalTime;
    @FXML private TableColumn<Train, String> colType;
    @FXML private TableColumn<Train, Integer> colTotalSeats;
    @FXML private Button bookButton;
    @FXML private ImageView exit;

    private User user;
    private int passengerCount;
    private List<Train> currentTrainList;

    @FXML private AnchorPane rootPane;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        
        colTrainId.setCellValueFactory(new PropertyValueFactory<>("trainId"));
        colFromStation.setCellValueFactory(new PropertyValueFactory<>("fromStation"));
        colToStation.setCellValueFactory(new PropertyValueFactory<>("toStation"));
        colDepartureTime.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        colArrivalTime.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colTotalSeats.setCellValueFactory(new PropertyValueFactory<>("totalSeats"));

        bookButton.setOnAction(e -> onBookSelectedTrain());

        javafx.application.Platform.runLater(() -> {
            rootPane.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            rootPane.setOnMouseDragged(event -> {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
        });
    }

    public void setTrains(List<Train> trains, User user, int passengerCount) {
        this.user = user;
        this.passengerCount = passengerCount;
        this.currentTrainList = trains != null ? trains : new ArrayList<>();
        trainTable.setItems(FXCollections.observableArrayList(this.currentTrainList));
    }

    private void onBookSelectedTrain() {
        Train selected = trainTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ท่านยังไม่ได้เลือกเที่ยวรถไฟ");
            alert.setHeaderText(null);
            alert.setContentText("โปรดคลิกเลือกเที่ยวรถไฟที่ต้องการ");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/scenes/SeatSelection.fxml"));
            Parent root = loader.load();
            root.getStyleClass().add("window-border");
            playOpenWindowAnimation(root);

            SeatSelectionController controller = loader.getController();
            controller.setData(user, selected, passengerCount, currentTrainList);

            Stage stage = (Stage) bookButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setTitle("เลือกที่นั่ง");

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
    private void onExit() {
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
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
