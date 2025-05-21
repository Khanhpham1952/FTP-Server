import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private UserDB userDB;
    private String serverDir;

    private BufferedReader in;
    private PrintWriter out;
    private InputStream dataIn;
    private OutputStream dataOut;

    public ClientHandler(Socket socket, UserDB userDB, String serverDir) {
        this.socket = socket;
        this.userDB = userDB;
        this.serverDir = serverDir;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            dataIn = socket.getInputStream();
            dataOut = socket.getOutputStream();

            out.println("Welcome to FTP Server.");
            out.println("Username:");
            String username = in.readLine();

            out.println("Password:");
            String password = in.readLine();

            if (username == null || password == null || !userDB.isValidUser(username, password)) {
                out.println("Login failed.");
                return;
            }

            out.println("Login successful.");

            String command;
            while ((command = in.readLine()) != null) {
                if (command.equalsIgnoreCase("quit")) {
                    out.println("Goodbye!");
                    break;
                } else if (command.equalsIgnoreCase("ls")) {
                    handleList();
                } else if (command.startsWith("get ")) {
                    handleGet(command.substring(4).trim());
                } else if (command.startsWith("put ")) {
                    handlePut(command.substring(4).trim());
                } else if (command.equalsIgnoreCase("help")) {
                    handleHelp();
                } else if (command.startsWith("delete ")) {
                    handleDelete(command.substring(7).trim());
                } else {
                    out.println("Unknown command.");
                }
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed to close socket.");
            }
        }
    }

    private void handleList() {
        File folder = new File(serverDir);
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            out.println("No files found.");
        } else {
            for (File file : files) {
                out.println(file.getName());
            }
        }
        out.println("END_OF_LIST");
    }

    private void handleGet(String filename) {
        File file = new File(serverDir, filename);
        if (!file.exists()) {
            out.println("File not found.");
            return;
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            out.println("START_FILE");
            byte[] buffer = new byte[4096];
            int count;
            while ((count = bis.read(buffer)) > 0) {
                dataOut.write(buffer, 0, count);
            }
            dataOut.flush();
        } catch (IOException e) {
            out.println("Error sending file.");
        }
    }

    private void handlePut(String filename) {
        File file = new File(serverDir, filename);
        if (file.exists()) {
            out.println("File already exists on server.");
            return;
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            out.println("READY");
            byte[] buffer = new byte[4096];
            int count;
            while ((count = dataIn.read(buffer)) > 0) {
                bos.write(buffer, 0, count);
                if (count < 4096) break;
            }
            bos.flush();
            out.println("Upload complete.");
        } catch (IOException e) {
            out.println("Error receiving file.");
        }
    }

    private void handleHelp() {
        out.println("Available commands:");
        out.println("ls             - list files");
        out.println("get <filename> - download file");
        out.println("put <filename> - upload file");
        out.println("delete <file>  - delete file from server");
        out.println("quit           - disconnect");
        out.println("help           - show this help message");
        out.println("END_HELP");
    }

    private void handleDelete(String filename) {
        File file = new File(serverDir, filename);
        if (!file.exists()) {
            out.println("File not found.");
        } else if (file.delete()) {
            out.println("File deleted.");
        } else {
            out.println("Failed to delete file.");
        }
    }
}
