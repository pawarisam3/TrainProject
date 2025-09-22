package Ui;

import javax.swing.*;
import Model.User;
import Services.AuthService;
import java.awt.*;
import Utils.DateUtil;

public class RegisterFrame extends JFrame {
    private JTextField firstNameField, lastNameField, usernameField;
    private JPasswordField passwordField;
    private JTextField citizenIdField;
    private JTextField dayField, monthField, yearField;
    private JButton registerButton, backButton;
    private AuthService authService;

    public RegisterFrame() {
        authService = new AuthService();

        setTitle("Register");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel fnLabel = new JLabel("First Name:");
        JLabel lnLabel = new JLabel("Last Name:");
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JLabel cidLabel = new JLabel("Citizen ID (13 digits):");
        JLabel dobLabel = new JLabel("Date of Birth (DD MM YYYY):");

        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        citizenIdField = new JTextField(15);

        dayField = new JTextField(2);
        monthField = new JTextField(2);
        yearField = new JTextField(4);

        registerButton = new JButton("Register");
        backButton = new JButton("Back");

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(fnLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lnLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(userLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(passLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(cidLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(citizenIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(dobLabel, gbc);
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        dobPanel.add(dayField);
        dobPanel.add(new JLabel(" / "));
        dobPanel.add(monthField);
        dobPanel.add(new JLabel(" / "));
        dobPanel.add(yearField);
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(dobPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(backButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(registerButton, gbc);

        registerButton.addActionListener(e -> register());
        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }

    private void register() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String citizenId = citizenIdField.getText().trim();
        String dayStr = dayField.getText().trim();
        String monthStr = monthField.getText().trim();
        String yearStr = yearField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()
                || citizenId.isEmpty() || dayStr.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!citizenId.matches("\\d{13}")) {
            JOptionPane.showMessageDialog(this, "Citizen ID must be exactly 13 digits.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int day, month, year;
        try {
            day = Integer.parseInt(dayStr);
            month = Integer.parseInt(monthStr);
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Day, Month and Year must be numbers.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!DateUtil.isValidDate(day, month, year)) {
            JOptionPane.showMessageDialog(this, "Invalid date.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int age = DateUtil.calculateAge(day, month, year);
        if (age < 12) {
            JOptionPane.showMessageDialog(this, "You must be at least 12 years old to register.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authService.isUsernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String birthDate = String.format("%02d/%02d/%04d", day, month, year);
        User user = new User(firstName, lastName, username, password, citizenId, birthDate, false);

        boolean success = authService.registerUser(user);

        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginFrame();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
