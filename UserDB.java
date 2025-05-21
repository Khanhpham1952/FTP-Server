import java.io.*;
import java.util.*;

public class UserDB {
    private Map<String, String> users;

    public UserDB(String filename) throws IOException {
        users = new HashMap<>();
        loadUsers(filename);
    }

    private void loadUsers(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                String username = parts[0].trim();
                String password = parts[1].trim();
                users.put(username, password);
            }
        }

        reader.close();
    }

    public boolean isValidUser(String username, String password) {
        return password.equals(users.get(username));
    }

    // Optional: for testing
    public static void main(String[] args) throws IOException {
        UserDB db = new UserDB("users.txt");
        System.out.println(db.isValidUser("KhanhPham", "KhanhPham10422103"));
    }
}
