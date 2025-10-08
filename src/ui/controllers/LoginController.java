package ui.controllers;

import java.io.IOException;

import app.App;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import services.AuthService;
import models.User;
import utils.Session;

public class LoginController{   
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label usernameError;
    @FXML private Label passwordError;
    @FXML private Label signinlabel;
    @FXML private ImageView thelogo;

    private AuthService authService = new AuthService();

    @FXML
    public void initialize(){
        playBounce(signinlabel);
        playBounce(thelogo);
        spinLogo();
    }

    @FXML
    public void gotoSignUp() throws IOException{
        App.setRootWithFade("/ui/scenes/Register");
    }

    @FXML
    public void loginHandle() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        clearErrors();

        if (username.isEmpty()) {
            showError(usernameError, "โปรดกรอกชื่อผู้ใช้!");
            return;
        }
        if (password.isEmpty()) {
            showError(passwordError, "โปรดกรอกรหัสผ่าน!");
            return;
        }

        User user = authService.login(username, password);
        if (user == null) {
            showError(passwordError, "ชื่อผู้ใช้หรือรหัสผ่านผิดพลาด!");
            return;
        }

        Session.setCurrentUser(user);
        clearFields();
        App.setRootWithFade("/ui/scenes/MainFrame1");
    }

    private void clearErrors() {
        usernameError.setVisible(false);
        passwordError.setVisible(false);
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }

    @FXML
    private void onExit() {
        System.exit(0);
    }

    public void playBounce(Node node) {
        ScaleTransition bounceOut = new ScaleTransition(Duration.millis(500), node);
        bounceOut.setToX(1.1);
        bounceOut.setToY(1.1);

        ScaleTransition bounceBack = new ScaleTransition(Duration.millis(1000), node);
        bounceBack.setToX(1.0);
        bounceBack.setToY(1.0);

        SequentialTransition bounceSequence = new SequentialTransition(bounceOut, bounceBack);
        bounceSequence.play();
    }

    private void spinLogo() {
        RotateTransition rotate = new RotateTransition(Duration.seconds(1.5), thelogo);
        rotate.setByAngle(360);
        rotate.setCycleCount(100);
        rotate.setInterpolator(Interpolator.LINEAR); 
        rotate.play();
    }
}
