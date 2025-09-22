package Services;

import Model.User;
import Utils.CSVUtil;
import java.util.*;

public class AuthService {
    private static final String USER_FILE = "data/user.csv";
    private List<User> users;

    public AuthService() {
        users = loadUsers();
    }

    private List<User> loadUsers() {
        List<User> list = new ArrayList<>();
        List<String> lines = CSVUtil.readLines(USER_FILE);
        for (String line : lines) {
            try {
                User user = User.fromCSV(line);
                list.add(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public boolean isUsernameExists(String username) {
        return users.stream().anyMatch(u -> u.getuserName().equalsIgnoreCase(username));
    }

    public boolean registerUser(User user) {
        if (isUsernameExists(user.getuserName())) {
            return false;
        }
        users.add(user);
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(u.TOCSV());
        }
        CSVUtil.writeLines(USER_FILE, lines);
        return true;
    }

    public User login(String username, String password) {
        return users.stream()
                .filter(u -> u.getuserName().equalsIgnoreCase(username) && u.getpassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}
