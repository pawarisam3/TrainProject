package ui.controllers;

import java.io.IOException;

import app.App;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;
import services.AuthService;
import utils.DateUtils;

public class RegisterController {

    @FXML private TextField firstnamefield;
    @FXML private Label firstnameError;

    @FXML private TextField lastnamefield;
    @FXML private Label lastnameError;

    @FXML private TextField usernameField;
    @FXML private Label usernameError;

    @FXML private PasswordField passwordField;
    @FXML private Label passwordError;

    @FXML private PasswordField confirmpassword;
    @FXML private Label confirmPasswordError;

    @FXML private TextField citizenIDField;
    @FXML private Label citizenIdError;

    @FXML private TextField dayField;
    @FXML private TextField monthField;
    @FXML private TextField yearField;
    @FXML private Label birthDateError;

    AuthService authService;

    @FXML
    public void gotoLogin() throws IOException{
        App.setRootWithFade("/ui/scenes/Login");
    }

    @FXML
    public void handleRegister() throws IOException {
        clearErrors();

        String firstname = firstnamefield.getText().trim();
        String lastname = lastnamefield.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String passwordconfirm = confirmpassword.getText().trim();
        String citizenId = citizenIDField.getText().trim();
        String dayStr = dayField.getText().trim();
        String monthStr = monthField.getText().trim();
        String yearStr = yearField.getText().trim();

        boolean hasError = false;

        if (firstname.isEmpty()) {
            firstnameError.setText("กรุณากรอกชื่อจริง");
            hasError = true;
        }

        if (lastname.isEmpty()) {
            lastnameError.setText("กรุณากรอกนามสกุล");
            hasError = true;
        }

        if (username.isEmpty()) {
            usernameError.setText("กรุณากรอกชื่อผู้ใช้");
            hasError = true;
        }

        if (password.isEmpty()) {
            passwordError.setText("กรุณากรอกรหัสผ่าน");
            hasError = true;
        }

        if (passwordconfirm.isEmpty()) {
            confirmPasswordError.setText("กรุณายืนยันรหัสผ่าน");
            hasError = true;
        }

        if (citizenId.isEmpty()) {
            citizenIdError.setText("กรุณากรอกเลขประจำตัวประชาชน");
            hasError = true;
        }

        if (dayStr.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty()) {
            birthDateError.setText("กรุณากรอกวัน/เดือน/ปีเกิดให้ครบ");
            hasError = true;
        }

        if (hasError) return;

        if (!citizenId.matches("\\d{13}")) {
            citizenIdError.setText("เลขประจำตัวประชาชนต้องมี 13 หลัก");
            hasError = true;
        }

        int day = 0, month = 0, year = 0;
        try {
            day = Integer.parseInt(dayStr);
            month = Integer.parseInt(monthStr);
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException ex) {
            birthDateError.setText("วัน/เดือน/ปีเกิดต้องเป็นตัวเลขเท่านั้น");
            hasError = true;
        }

        if (hasError) return;

        if (!DateUtils.isValidDate(day, month, year)) {
            birthDateError.setText("วัน/เดือน/ปีเกิดไม่ถูกต้อง");
            return;
        }

        int age = DateUtils.calculateAge(day, month, year);
        if (age < 12) {
            birthDateError.setText("อายุไม่ถึงเกณฑ์ที่กำหนด");
            return;
        }

        if (!password.equals(passwordconfirm)) {
            confirmPasswordError.setText("รหัสผ่านไม่ตรงกัน");
            return;
        }

        authService = new AuthService();

        if (authService.isUsernameExists(username)) {
            usernameError.setText("ชื่อนี้ถูกใช้ไปแล้ว");
            return;
        }

        String birthDate = String.format("%02d/%02d/%04d", day, month, year);
        User user = new User(firstname, lastname, username, password, citizenId, birthDate, false);

        authService.registerUser(user);
    }

    private void clearErrors() {
        firstnameError.setText("");
        lastnameError.setText("");
        usernameError.setText("");
        passwordError.setText("");
        confirmPasswordError.setText("");
        citizenIdError.setText("");
        birthDateError.setText("");
    }

    @FXML
    private void onExit() {
        System.exit(0);
    }
}
