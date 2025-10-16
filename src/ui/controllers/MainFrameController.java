package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;
import models.Booking;
import models.User;
import utils.Session;

import java.io.IOException;

public class MainFrameController {

    @FXML private Label theusername;
    @FXML private AnchorPane centerPane;
    @FXML private Button adminBtn;
    @FXML private ImageView exit;
    @FXML private Button homeBtn;
    @FXML private Button historyBtn;
    @FXML private Button searchBtn;
    @FXML private Label trainbookinglabel;

    private Button selectedNavButton = null;

    public void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            Object controller = loader.getController();
            if (controller instanceof SearchController) {
                ((SearchController) controller).setUser(Session.getCurrentUser());
            } else if (controller instanceof BookingHistoryController) {
                ((BookingHistoryController) controller).setMainController(this);
                ((BookingHistoryController) controller).setUser(Session.getCurrentUser());
            } else if (controller instanceof BookingDetailsController) {
                ((BookingDetailsController) controller).setMainController(this);
            }
            setupContent(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupContent(Parent content) {

        centerPane.getChildren().clear();
        centerPane.getChildren().add(content);

        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);

        content.setTranslateX(centerPane.getWidth());
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), content);
        tt.setToX(0);
        tt.play();
    }

    public void loadBookingDetails(String fxmlPath, Booking booking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            Object controller = loader.getController();
            if (controller instanceof BookingDetailsController) {
                ((BookingDetailsController) controller).setMainController(this);
                ((BookingDetailsController) controller).setBooking(booking);
            }

            setupContent(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        User user = Session.getCurrentUser();
        if (user != null) {
            theusername.setText(user.getUsername());
            adminBtn.setVisible(user.isAdmin());
        } else {
            theusername.setText("ไม่พบชื่อผู้ใช้");
            adminBtn.setVisible(false);
        }

        addHoverAnimation(adminBtn);
        addHoverAnimation(homeBtn);
        addHoverAnimation(historyBtn);
        addHoverAnimation(searchBtn);

        playSlideAndBounce(theusername);
        playFadeInBounce(trainbookinglabel);

        loadContent("/ui/scenes/Home.fxml");
    }

    @FXML
    private void onAdmin() {
        loadContent("/ui/scenes/AdminTrainManagement.fxml");
        animateSelectedButton(adminBtn);
    }

    @FXML
    private void onLogout() throws IOException {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Logout?");
        alert.setHeaderText(null);
        alert.setContentText("คุณต้องการ Logout หรือไม่");
        alert.showAndWait().ifPresent(e -> {
            if(e == ButtonType.OK){
                Session.clear();
                if (selectedNavButton != null) {
                    selectedNavButton.setScaleX(1.0);
                    selectedNavButton.setScaleY(1.0);
                    selectedNavButton = null;
                }
                try {
                    app.App.setRootWithFade("/ui/scenes/Login");
                } catch (Exception err) {
                   err.printStackTrace();
                }
            }   
        });
    }

    @FXML
    private void onHome() {
        loadContent("/ui/scenes/Home.fxml");
        animateSelectedButton(homeBtn);
    }

    @FXML
    private void onSearch() {
        loadContent("/ui/scenes/Search.fxml");
        animateSelectedButton(searchBtn);
    }

    @FXML
    private void onBookingHistory() {
        loadContent("/ui/scenes/BookingHistory.fxml");
        animateSelectedButton(historyBtn);
    }

    @FXML
    private void onExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("ยืนยันการออกจากโปรแกรม");
        alert.setHeaderText(null);
        alert.setContentText("คุณแน่ใจว่าจะออกจากโปรแกรมหรือไม่?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Stage stage = (Stage) exit.getScene().getWindow();
                stage.close();
            }
        });
    }

    private void animateSelectedButton(Button clickedButton) {
        if (selectedNavButton != null && selectedNavButton != clickedButton) {
            ScaleTransition shrink = new ScaleTransition(Duration.millis(200), selectedNavButton);
            shrink.setToX(1.0);
            shrink.setToY(1.0);
            shrink.play();

            selectedNavButton.getStyleClass().remove("button3-selected");
            if (!selectedNavButton.getStyleClass().contains("button3")) {
                selectedNavButton.getStyleClass().add("button3");
            }
        }

        ScaleTransition expand = new ScaleTransition(Duration.millis(200), clickedButton);
        expand.setToX(1.2);
        expand.setToY(1.2);
        expand.play();

        clickedButton.getStyleClass().remove("button3");
        if (!clickedButton.getStyleClass().contains("button3-selected")) {
            clickedButton.getStyleClass().add("button3-selected");
        }

        selectedNavButton = clickedButton;
    }

    private void addHoverAnimation(Button button) {
        ScaleTransition stEnter = new ScaleTransition(Duration.millis(150), button);
        stEnter.setToX(1.1);
        stEnter.setToY(1.1);

        ScaleTransition stExit = new ScaleTransition(Duration.millis(150), button);
        stExit.setToX(1.0);
        stExit.setToY(1.0);

        DropShadow glow = new DropShadow(20, Color.web("#524A7B"));

        button.setOnMouseEntered(e -> {
            stExit.stop();
            stEnter.play();
            button.setEffect(glow);
        });

        button.setOnMouseExited(e -> {
            stEnter.stop();
            stExit.play();
            button.setEffect(null);
        });
    }

    public void playSlideAndBounce(javafx.scene.Node node) {
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(2000), node);
        slideIn.setFromX(300);
        slideIn.setToX(0);

        ScaleTransition bounceOut = new ScaleTransition(Duration.millis(1000), node);
        bounceOut.setToX(1.1);
        bounceOut.setToY(1.1);

        ScaleTransition bounceBack = new ScaleTransition(Duration.millis(1000), node);
        bounceBack.setToX(1.0);
        bounceBack.setToY(1.0);

        SequentialTransition fullAnimation = new SequentialTransition(slideIn, new SequentialTransition(bounceOut, bounceBack));
        fullAnimation.play();
    }

    public void playFadeInBounce(javafx.scene.Node node) {
        node.setOpacity(0);
        node.setScaleX(0.8);
        node.setScaleY(0.8);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(1500), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(1500), node);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(1500), node);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        ParallelTransition appear = new ParallelTransition(fadeIn, scaleUp);
        SequentialTransition bounceIn = new SequentialTransition(appear, scaleDown);
        bounceIn.play();
    }
}
