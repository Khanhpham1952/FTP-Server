import java.io.*;
import java.net.*;

public class FTPServer{
    private static final int PORT = 2121;
    private static final String USERS_FILE = "users.txt";
    private static final String SERVER_FILES_DIR = "server_files";

    public static void main(String[] args) {
        try {
            // Create server_files folder if not exists
            File fileDirectory = new File(SERVER_FILES_DIR);
            if (!fileDirectory.exists()) {
                if (fileDirectory.mkdir()) {
                    System.out.println("Created directory: " + SERVER_FILES_DIR);
                } else {
                    System.err.println("Failed to create directory: " + SERVER_FILES_DIR);
                }
            }

            // Load users
            UserDB userDB = new UserDB(USERS_FILE);

            // Start the server socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("FTP Server started on port " + PORT);

            // Accept clients in a loop
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected from " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket, userDB, SERVER_FILES_DIR);
                Thread thread = new Thread(handler);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
