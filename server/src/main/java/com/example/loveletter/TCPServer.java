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
    private static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    private static boolean gameStarted = false;
    private static ClientHandler hostClient = null; // To keep track of the host

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
        private LocalDate lastDate;
        private PrintWriter out;
        private boolean isHost = false;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Client connected: " + socket.getInetAddress());
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                this.out = writer;

                // Login process
                while (true) {
                    out.println("Enter your nickname:");
                    nickname = in.readLine();

                    if (nickname == null || nickname.trim().isEmpty() || clients.containsKey(nickname)) {
                        out.println("ERROR: Nickname is invalid or already in use.");
                    } else {
                        synchronized (clients) {
                            clients.put(nickname, this);
                        }
                        out.println("WELCOME: " + nickname);
                        broadcast("BROADCAST: " + nickname + " joined the chat.", null);

                        synchronized (TCPServer.class) {
                            if (hostClient == null) {
                                hostClient = this;
                                isHost = true;
                                // we'll tell the client they are the host after the romantic date is set
                            }
                        }
                        break;
                    }
                }

                while (true) {
                    out.println("When was your last date (YYYY-MM-DD):");
                    String dateInput = in.readLine();

                    if (dateInput == null || dateInput.trim().isEmpty()) {
                        out.println("ERROR: Date cannot be empty.");
                        continue;
                    }

                    try {
                        lastDate = LocalDate.parse(dateInput.trim());
                        out.println("RECEIVED: Last date set to " + lastDate);
                        break;
                    } catch (DateTimeParseException e) {
                        out.println("ERROR: Invalid date format. Please use YYYY-MM-DD.");
                    }
                }

                if (isHost) {
                    out.println("INFO: You are the host. Type 'START' to begin the game when ready.");
                }

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("BYE")) {
                        out.println("INFO: Disconnecting from the server...");
                        break;
                    } else if (isHost && message.equalsIgnoreCase("START")) {
                        handleStartCommand(in);

                        break;
                    } else {
                        broadcast("BROADCAST: " + nickname + ": " + message, nickname);
                    }
                }

            } catch (IOException e) {
                System.err.println("I/O error with client " + nickname + ": " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        private void handleStartCommand(BufferedReader in) throws IOException {
            synchronized (TCPServer.class) {
                if (gameStarted) {
                    out.println("ERROR: Game has already started.");
                    return;
                }

                int clientCount = clients.size();
                if (clientCount < 2) {
                    out.println("ERROR: At least 2 players are required to start the game.");
                    return;
                }
                if (clientCount > 8) {
                    out.println("ERROR: Maximum of 8 players allowed.");
                    return;
                }

                String[] playerNames = clients.values().stream()
                        .sorted((c1, c2) -> c1.lastDate.compareTo(c2.lastDate))
                        .map(c -> c.nickname)
                        .toArray(String[]::new);
                game = new Game(playerNames);
                gameStarted = true;

                broadcast("GAME: The game has started with players: " + String.join(", ", playerNames), null);
                System.out.println("Game has been initialized by " + nickname);

                while (game.nextRound()) {
                    out.println("You have the following cards: " + game.getCurrentPlayer().getCardsString());
                    out.println("Their effects are: ");
                    for (Card card : game.getCurrentPlayer().getHand()) {
                        out.println("\u001B[34m" + card.getEffect() + "\u001B[0m");
                    }
                    out.println("INFO: Choose a card to play: ");

                    Card cardToPlay = Card.fromString(in.readLine());

                }
            }
        }

        private void disconnect() {
            if (nickname != null) {
                synchronized (clients) {
                    clients.remove(nickname);
                }
                broadcast("BROADCAST: " + nickname + " left the chat.", null);
            }
            //TODO(fix bug) we need to tell the client that they are the host after the pick name and date but right now it just skips this step
            if (nickname != null && lastDate != null && isHost) {
                synchronized (TCPServer.class) {
                    if (!clients.isEmpty()) {
                        ClientHandler newHost = clients.values().iterator().next();
                        newHost.isHost = true;
                        hostClient = newHost;
                        newHost.out.println("INFO: The previous host has disconnected. You are now the host. Type 'START' to begin the game.");
                    } else {
                        hostClient = null;
                    }
                }
            }

            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("I/O error while closing socket for " + nickname + ": " + e.getMessage());
            }
        }

        private void broadcast(String message, String excludeUser) {
            System.out.println(message);
            synchronized (clients) {
                clients.forEach((name, handler) -> {
                    if (excludeUser == null || !name.equals(excludeUser)) {
                        handler.out.println(message);
                    }
                });
            }
        }
    }
}
