package Ui;

import Model.User;
import Services.AuthService;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private AuthService authService;

    public LoginFrame() {
        authService = new AuthService();

        setTitle("Login");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(userLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(registerButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(loginButton, gbc);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> {
            dispose();
            new RegisterFrame();
        });

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = authService.login(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Logged in user: " + user.getuserName() + ", isAdmin: " + user.isAdmin());

        JOptionPane.showMessageDialog(this, "Welcome, " + user.getFullName() + "!", "Success",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();

        // new MainSearchFrame(user);
    }
}
