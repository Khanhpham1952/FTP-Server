# Java FTP Server-Client Project

This project is a simple implementation of a command-line FTP server and client using basic Java networking libraries (`java.net.*`, `java.io.*`). It supports authentication, basic FTP commands, and concurrent client connections without using any frameworks.

---

## 📁 Project Structure

```
Test project 1/
├── FTPServer.java         # Main server entry point
├── ClientHandler.java     # Per-client command handling
├── UserDB.java            # User authentication logic
├── FTPClient.java         # Command-line FTP client
├── users.txt              # User credentials database
├── server_files/          # Folder where server stores files
└── README.md              # Project documentation
```

---

## ✅ Features Implemented

* [x] User authentication from `users.txt`
* [x] `ls` – list files on the server
* [x] `get <filename>` – download file from server
* [x] `put <filename>` – upload file to server
* [x] `quit` – disconnect client
* [x] Supports multiple clients (via `Thread` and `Runnable`)
* [x] `help` – display available FTP commands
* [x] `delete <filename>` – delete file from server

---

## 🚀 How to Compile and Run

### 1. Compile All Java Files

```bash
javac *.java
```

### 2. Start the Server

```bash
java FTPServer
```

The server will listen on port `2121` and create the folder `server_files/` if it doesn't exist.

### 3. Run the Client (in a new terminal)

```bash
java FTPClient
```

---

## 👤 Default Users (in `users.txt`)

```
KhanhPham,password10422103
NamHai,password10422094
```

Edit or add users manually.

---

## 🧪 FTP Client Commands

| Command             | Description             |
| ------------------- | ----------------------- |
| `ls`                | List files on server    |
| `get <filename>`    | Download file           |
| `put <filename>`    | Upload file             |
| `delete <filename>` | Delete file from server |
| `help`              | Show available commands |
| `quit`              | Disconnect from server  |

---

## 🛠 Requirements

* Java 8 or higher
* Terminal (CMD, PowerShell, or VS Code terminal)
* No external frameworks used

---

## 📌 Notes

* For simplicity, all files are kept in one directory.
* File transfers are handled using raw byte streams.
* Text-based commands are synchronized using line-based markers (e.g. `END_OF_LIST`, `END_HELP`).

---

## 🔒 Disclaimer

This project is for educational purposes only and not secure for use over the internet without TLS, encryption, and input validation.
