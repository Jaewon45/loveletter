package com.example.loveletter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings({"CallToPrintStackTrace", "unused"})
public class TCPServer {

    private static Game game = null;

    private static final int SERVER_PORT = Integer.parseInt(System.getProperty("server.port", "12345"));
    private static final Map<String, PrintWriter> clients = new ConcurrentHashMap<>();

    private static boolean gameStarted = false;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started on port " + SERVER_PORT);

            ExecutorService executor = Executors.newCachedThreadPool();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {

        private final Socket socket;
        private String nickname;
        private LocalDate lastRomanticDate;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Client connected: " + socket.getInetAddress());
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // Login process
                while (true) {
                    out.println("Enter your nickname:");
                    nickname = in.readLine();

                    if (nickname == null || nickname.trim().isEmpty() || clients.containsKey(nickname)) {
                        out.println("ERROR: Nickname is invalid or already in use.");
                    } else {
                        synchronized (clients) {
                            clients.put(nickname, out);
                        }
                        out.println("WELCOME: " + nickname);
                        broadcast("BROADCAST: " + nickname + " joined the chat.", null);
                        break;
                    }
                }

                // Prompt for the last date
                while (true) {
                    out.println("When was your most last date (YYYY-MM-DD):");
                    String dateInput = in.readLine();

                    if (dateInput == null || dateInput.trim().isEmpty()) {
                        out.println("ERROR: Date cannot be empty.");
                        continue;
                    }

                    try {
                        lastRomanticDate = LocalDate.parse(dateInput.trim());
                        out.println("RECEIVED: Last date set to " + lastRomanticDate);
                        break;
                    } catch (DateTimeParseException e) {
                        out.println("ERROR: Invalid date format. Please use YYYY-MM-DD.");
                    }
                }

                // Check if this is the first client to start the game
                synchronized (TCPServer.class) {
                    if (!gameStarted) {
                        game = new Game(clients.keySet().toArray(String[]::new));
                        gameStarted = true;
                        out.println("GAME: The game has started!");
                        System.out.println("Game has been initialized by " + nickname);
                        // Stop further processing as per your instruction
                        return;
                    }
                }

                // Message loop
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("BYE")) {
                        broadcast("BROADCAST: " + nickname + " left the chat.", nickname);
                        break;
                    }
                    broadcast("BROADCAST: " + nickname + ": " + message, nickname);
                }

            } catch (IOException e) {
                System.err.println("I/O error: " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        private void disconnect() {
            if (nickname != null) {
                synchronized (clients) {
                    clients.remove(nickname);
                }
                broadcast("BROADCAST: " + nickname + " left the chat.", null);
            }
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("I/O error: " + e.getMessage());
            }
        }

        private void broadcast(String message, String excludeUser) {
            System.out.println(message);
            synchronized (clients) {
                clients.forEach((name, writer) -> {
                    if (excludeUser == null || !name.equals(excludeUser)) {
                        writer.println(message);
                    }
                });
            }
        }
    }
}
