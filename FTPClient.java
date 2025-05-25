import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FTPClient {
    public static void main(String[] args) {
        final String SERVER = "localhost";
        final int PORT = 2121;

        try (Socket socket = new Socket(SERVER, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             InputStream dataIn = socket.getInputStream();
             OutputStream dataOut = socket.getOutputStream();
             Scanner scanner = new Scanner(System.in)) {

            System.out.println(in.readLine()); // Welcome

            System.out.print(in.readLine()); // Username:
            String username = scanner.nextLine();
            out.println(username);

            System.out.print(in.readLine()); // Password:
            String password = scanner.nextLine();
            out.println(password);

            String response = in.readLine();
            System.out.println(response);

            if (!response.equalsIgnoreCase("Login successful.")) {
                return;
            }

            while (true) {
                System.out.print("ftp> ");
                String command = scanner.nextLine();
                out.println(command);
                out.flush();

                if (command.equalsIgnoreCase("quit")) {
                    System.out.println(in.readLine());
                    break;
                } else if (command.equalsIgnoreCase("ls")) {
                    String line;
                    while (!(line = in.readLine()).equals("END_OF_LIST")) {
                        System.out.println(line);
                    }
                } else if (command.startsWith("get ")) {
                    String filename = command.substring(4).trim();
                    String start = in.readLine();
                    if (start.equals("START_FILE")) {
                        try (FileOutputStream fos = new FileOutputStream("client_" + filename)) {
                            byte[] buffer = new byte[4096];
                            int count;
                            while ((count = dataIn.read(buffer)) > 0) {
                                fos.write(buffer, 0, count);
                                if (count < 4096) break;
                            }
                            fos.flush();
                            System.out.println("Download complete.");
                        } catch (IOException e) {
                            System.out.println("Download failed: " + e.getMessage());
                        }
                    } else {
                        System.out.println(start); // e.g. "File not found."
                    }

                } else if (command.startsWith("put ")) {
                    String filename = command.substring(4).trim();
                    File file = new File(filename);
                    if (!file.exists()) {
                        System.out.println("File not found locally.");
                        continue;
                    }

                    String responsePut = in.readLine();

                    if (responsePut.equals("EXISTS")) {
                        System.out.print("File already exists. Overwrite? (yes/no): ");
                        String answer = scanner.nextLine();
                        out.println(answer);
                        if (!answer.equalsIgnoreCase("yes")) {
                            System.out.println(in.readLine()); // Upload canceled.
                            continue;
                        }
                        responsePut = in.readLine(); // Server sends READY after 'yes'
                    }

                    if (!responsePut.equals("READY")) {
                        System.out.println("Server not ready to receive.");
                        continue;
                    }

                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int count;
                        while ((count = fis.read(buffer)) > 0) {
                            dataOut.write(buffer, 0, count);
                        }
                        dataOut.flush();
                        System.out.println(in.readLine()); // Upload complete
                    } catch (IOException e) {
                        System.out.println("Upload failed: " + e.getMessage());
                    }

                } else if (command.equalsIgnoreCase("help")) {
                    String line;
                    while (!(line = in.readLine()).equals("END_HELP")) {
                        System.out.println(line);
                    }
                } else {
                    System.out.println(in.readLine());
                }
            }

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
