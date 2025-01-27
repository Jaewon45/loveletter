package com.example.loveletter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings({ "CallToPrintStackTrace", "unused" })
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
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Client connected: " + socket.getInetAddress());
            try {
                // Initialize streams outside the try-with-resources to keep them open
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new PrintWriter(socket.getOutputStream(), true);

                // Login process
                while (true) {
                    out.println("Enter your nickname:");
                    nickname = in.readLine();

                    if (nickname == null || nickname.trim().isEmpty() || clients.containsKey(nickname)) {
                        out.println("ERROR: Nickname is invalid or already in use.");
                    } else {
                        synchronized (clients) {
                            clients.put(nickname, this);
                            // Assign host only if it's the first user
                            if (clients.size() == 1) {
                                hostClient = this;
                                isHost = true;
                            }
                        }
                        out.println("WELCOME: " + nickname);
                        broadcast("BROADCAST: " + nickname + " joined the room.", null);
                        break;
                    }
                }

                // Handle last date input
                handleLastDateInput(in);

                if (isHost) {
                    out.println("INFO: You are the host. Type 'START' to begin the game when ready.");
                }

                // Main message handling loop
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("BYE")) {
                        out.println("INFO: Disconnecting from the server...");
                        break;
                    } else if (isHost && message.equalsIgnoreCase("START")) {
                        handleStartCommand(in);
                    } else {
                        broadcast("BROADCAST: " + nickname + ": " + message, nickname);
                    }
                }

            } catch (IOException e) {
                System.err.println("I/O error with client " + nickname + ": " + e.getMessage());
            } finally {
                disconnect(); // Ensure disconnect is called in finally block
            }
        }

        private void handleLastDateInput(BufferedReader in) throws IOException {
            while (true) {
                out.println("When was your last date (YYYY-MM-DD):");
                String dateInput = "2024-01-01";// in.readLine();

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

                try {
                    // Main game loop
                    while (true) {
                        if (!game.nextRound()) {
                            broadcast("GAME: Game has ended!", null);
                            break;
                        }

                        String currentPlayerName = game.getCurrentPlayer().getName();
                        ClientHandler currentPlayerHandler = clients.get(currentPlayerName);

                        if (currentPlayerHandler != null) {
                            List<Card> cards = game.getCurrentPlayer().getHand();
                            currentPlayerHandler.out.println("Your turn!");
                            currentPlayerHandler.out.println("You have the following cards: " + cards.get(0).getName()
                                    + " & " + cards.get(1).getName());
                            currentPlayerHandler.out.println("Their effects are: ");
                            for (Card card : cards) {
                                currentPlayerHandler.out.println("\u001B[34m" + card.getDescription() + "\u001B[0m");
                            }
                            currentPlayerHandler.out
                                    .println("INFO: Choose a card to play - type \"A\" or \"B\" to choose "
                                            + cards.get(0).getName() + " and " + cards.get(1).getName()
                                            + " respectively");

                            // Broadcast game state to other players
                            broadcast("GAME: It's " + currentPlayerName + "'s turn!", currentPlayerName);

                            // Wait for player's move
                            String move = currentPlayerHandler.in.readLine();
                            if (move == null) {
                                broadcast("GAME: " + currentPlayerName + " disconnected!", null);
                                break;
                            }
                            // TODO: Process the move and update game state

                            // For now, just broadcast the move
                            broadcast("GAME: " + currentPlayerName + " played their card!", null);
                        }
                    }
                } finally {
                    gameStarted = false;
                }
            }
        }

        private void disconnect() {
            try {
                if (nickname != null) {
                    synchronized (clients) {
                        clients.remove(nickname);
                        broadcast("BROADCAST: " + nickname + " left the room.", null);
                    }
                }

                if (isHost) {
                    synchronized (TCPServer.class) {
                        if (!clients.isEmpty()) {
                            ClientHandler newHost = clients.values().iterator().next();
                            newHost.isHost = true;
                            hostClient = newHost;
                            newHost.out.println(
                                    "INFO: The previous host has disconnected. You are now the host. Type 'START' to begin the game.");
                        } else {
                            hostClient = null;
                        }
                    }
                }

                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (socket != null && !socket.isClosed())
                    socket.close();
            } catch (IOException e) {
                System.err.println("Error during disconnect for " + nickname + ": " + e.getMessage());
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
