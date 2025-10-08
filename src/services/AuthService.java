package services;

import models.User;
import utils.CSVUtils;

import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private static final String USER_FILE = "users.csv";
    private final List<User> users;

    public AuthService() { this.users = loadUsers(); }

    private List<User> loadUsers() {
        List<String> lines = CSVUtils.readLines(USER_FILE);
        List<User> loadedUsers = new ArrayList<>();

        for (String line : lines) {
            try {
                User user = User.fromCSV(line);
                if (user != null) {
                    loadedUsers.add(user);
                }
            } catch (Exception e) {
                System.err.println("Error parsing user line: " + line);
            }
        }
        return loadedUsers;
    }

    public boolean isUsernameExists(String username) {
        String usernameLower = username.toLowerCase();
        return users.stream().anyMatch(u -> u.getUsername().toLowerCase().equals(usernameLower));
    }

    public boolean registerUser(User user) {
        if (isUsernameExists(user.getUsername())) return false;
        users.add(user);
        saveUsers();
        return true;
    }

    private void saveUsers() {
        List<String> lines = users.stream().map(User::toCSV).toList();
        CSVUtils.writeLines(USER_FILE, lines);
    }

    public User login(String username, String password) {
        String usernameLower = username.toLowerCase();
        return users.stream()
                .filter(u -> u.getUsername().toLowerCase().equals(usernameLower) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}
