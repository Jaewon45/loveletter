package com.example.loveletter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("CallToPrintStackTrace")
public class TCPServer {

    private static final int SERVER_PORT = Integer.parseInt(System.getProperty("server.port", "12345"));
    private static final Map<String, PrintWriter> clients = new ConcurrentHashMap<>();

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
            synchronized (clients) {
                clients.forEach((name, writer) -> {
                    if (!name.equals(excludeUser)) {
                        writer.println(message);
                    }
                });
            }
        }
    }
}
