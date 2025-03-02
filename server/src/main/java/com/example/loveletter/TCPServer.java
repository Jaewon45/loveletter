package com.example.loveletter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCPServer handles incoming client connections and game/chat commands.
 *
 * <p>The server listens on a specified port (default: 12345) for client connections. Clients must
 * send a unique nickname upon connecting. The server supports broadcast chat, direct messaging, and
 * game commands (e.g., create, join, start, play a card, and show score).
 */
@SuppressWarnings("CallToPrintStackTrace")
public class TCPServer {

  private static final Logger LOGGER =
      Logger.getLogger(
          TCPServer.class.getName()); // Other classes don't need to access the server's logger

  /**
   * Port on which the server listens (default: 12345, can be overridden via system property
   * "server.port").
   */
  private static final int PORT = Integer.parseInt(System.getProperty("server.port", "12345"));

  /**
   * Map holding connected clients by nickname.
   *
   * <p>This map is synchronized to allow thread-safe access.
   */
  private static final Map<String, ClientHandler> clients =
      Collections.synchronizedMap(new HashMap<>()); // Only one thread modifies the map at a time

  /**
   * The currently active game.
   *
   * <p>If no game is active, this will be {@code null}.
   */
  private static volatile Game currentGame = null;

  // J: `volatile` ensures visibility of updates across multiple threads.
  // J: so that changes made to currentGame by one thread are immediately visible to all other
  // threads.
  // J: Since TCPServer is a singleton server (only one instance runs), currentGame should be shared
  // across all client threads.
  // J: private: any client or external class could modify the game state unpredictably.

  /**
   * The main method starts the server and listens for incoming connections.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(
      String[] args) { // J: Creates a ServerSocket that listens on a specified port.
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("Server running on port " + PORT);
      while (true) { // Infinite loop (while (true)) waits for new clients
        Socket socket = serverSocket.accept();
        new Thread(new ClientHandler(socket))
            .start(); // J: Starts a new thread to handle each client separately
      }
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "Server exception", ex);
    }
  }

  /**
   * Broadcasts a message to all connected clients except an optionally excluded client.
   *
   * @param message the message to broadcast
   * @param exclude the client to exclude from receiving the message, or {@code null} to send to all
   */
  public static void broadcast(String message, ClientHandler exclude) {
    System.out.println("Broadcasting: " + message);
    synchronized (clients) { // J: Ensures thread-safe access while iterating over clients
      for (ClientHandler client : clients.values()) {
        if (client != exclude) {
          client.send(message);
        }
      }
    }
  }

  /**
   * Broadcasts a message to all connected clients except an optionally excluded client.
   *
   * @param message the message to broadcast
   */
  public static void broadcast(String message) {
    System.out.println("Broadcasting: " + message);
    synchronized (clients) {
      for (ClientHandler client : clients.values()) {
        client.send(message);
      }
    }
  }

  /**
   * Broadcasts a message to all connected clients except an optionally excluded client.
   *
   * @param message the message to broadcast
   * @param name the name of the exclude from receiving the message, or {@code null} to send to all
   */
  public static void broadcast(String message, String name) {
    ClientHandler exclude = clients.get(name);
    System.out.println("Broadcasting: " + message);
    synchronized (clients) {
      for (ClientHandler client : clients.values()) {
        if (client != exclude) {
          client.send(message);
        }
      }
    }
  }

  /**
   * Sends a direct message to a specific client identified by nickname.
   *
   * @param recipient the nickname of the client to send the message to
   * @param message the message to send
   * @return {@code true} if the message was successfully sent; {@code false} if the recipient was
   *     not found
   */
  public static boolean sendDirect(String recipient, String message) {
    System.out.println("Sending DM to " + recipient + ": " + message);
    ClientHandler client = clients.get(recipient);
    if (client != null) {
      client.send(message);
      return true;
    }
    return false; // J: Q: what does it do?
  }

  /**
   * Handles communication with a connected client.
   *
   * <p>This inner class processes incoming messages and commands from the client.
   */
  public static class ClientHandler implements Runnable {
    // J: static, it does not require an instance of TCPServer to be created, so that it can be used
    // independently of the outer TCPServer class.

    /** The socket associated with this client. */
    private final Socket socket;

    /** The nickname chosen by the client. */
    private String nickname;

    /** Writer used to send messages to the client. */
    private PrintWriter out;

    /** Reader used to receive messages from the client. */
    private BufferedReader in;

    /**
     * Constructs a ClientHandler for the specified socket.
     *
     * @param socket the socket associated with the client
     */
    ClientHandler(Socket socket) {
      this.socket = socket;
    }

    /**
     * Runs the client handler thread.
     *
     * <p>This method handles the initial handshake (receiving the nickname), processes incoming
     * messages, handles commands, and ensures proper cleanup on disconnection.
     */
    @Override
    // J: make sure that run() is an implementation of Runnable.run().
    // J: when there is an error under run(), the compiler throws an error instead of silently
    // creating a new method
    public void run() { // J: `run` contains the code that will be executed in a separate thread.
      // J: below is the client handling logic
      try {
        in =
            new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out =
            new PrintWriter(
                new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)),
                true);

        // First message must be the nickname.
        boolean nicknameAccepted = false;
        while (!nicknameAccepted) {
            nickname = in.readLine();
            if (nickname == null || nickname.trim().isEmpty()) {
                send("Error: Nickname cannot be empty. Please try again.");
                continue;
            }
            synchronized (clients) {
                if (clients.containsKey(nickname)) {
                    send("Error: Nickname already in use. Please try again.");
                    continue;
                }
                clients.put(nickname, this);
                nicknameAccepted = true;
            }
        }

        // Send welcome messages
        send("Welcome " + nickname + "!");
        send("\nAvailable commands:");
        send("- /create : Create a new game");
        send("- /join : Join an existing game");
        send("- /start : Start the game (2-8 players needed)");
        send("- /dm <player> <message> : Send a private message to a player");

        broadcast(nickname + " joined the room", this);

        String message;
        while ((message = in.readLine()) != null) {
          if ("bye".equalsIgnoreCase(message.trim())) {
            break;
          }
          if (message.startsWith("/")) {
            // Process command messages.
            processCommand(message);
          } else if (!message.trim().isEmpty()) {  // Only broadcast non-empty messages
            // Regular chat message.
            broadcast(nickname + ": " + message);
          }
        }
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Error in client handler", e);
      } finally {
        try {
          socket.close();
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "Error in client handler", e);
        }
        synchronized (clients) {
          clients.remove(nickname);
        }
        if (nickname != null && !nickname.isEmpty()) {
          broadcast(nickname + " left the room");
        }
      }
    }

    /**
     * Processes a command message sent by the client.
     *
     * <p>Supported commands include:
     *
     * <ul>
     *   <li><code>/dm &lt;recipient&gt; &lt;message&gt;</code> - Sends a direct message.
     *   <li><code>/create</code> - Creates a new game and adds the creator as a player.
     *   <li><code>/join</code> - Joins an existing game (if not started).
     *   <li><code>/start</code> - Starts the game if there are 2-4 players.
     *   <li><code>/play &lt;card&gt; [target guess]</code> - Plays a card during an active game.
     *   <li><code>/score</code> - Shows the current scores to the requesting client.
     *   <li><code>/hand</code> - Shows the player's hand during an active game.
     *   <li><code>/explain &lt;card&gt;</code> - Explains the card.
     *   <li><code>/end</code> - Ends the current game.
     *   <li><code>/values</code> - Shows all card values.
     * </ul>
     *
     * @param message the command message received from the client
     */
    private void processCommand(String message) {
      String[] tokens = message.split(" ", 3);
      String command = tokens[0];
      switch (command) {
        case "/dm" -> {
          // Direct message: /dm recipient message.
          if (tokens.length < 3) {
            send("Error: Usage /dm <recipient> <message>");
          } else {
            String recipient = tokens[1];
            String dmMessage = "(DM from " + nickname + "): " + tokens[2];
            if (!TCPServer.sendDirect(recipient, dmMessage)) {
              send("Error: Recipient '" + recipient + "' not found.");
            }
          }
        }
        case "/create" -> {
          // Create a new game. Automatically adds the creator.
          if (currentGame != null) {
            send("Error: A game is already active.");
          } else {
            currentGame = new Game();
            if (currentGame.addPlayer(nickname)) {
              broadcast("Game created by " + nickname);
            } else {
              send("Error: Unable to create game.");
            }
          }
        }
        case "/join" -> {
          // Join an existing game (if not started).
          if (currentGame == null) {
            send("Error: No game available. Create one with /create.");
          } else if (currentGame.isStarted()) {
            send("Error: Game already started.");
          } else if (currentGame.hasPlayer(nickname)) {
            send("Error: You have already joined this game.");
          } else {
            if (currentGame.addPlayer(nickname)) {
              broadcast(nickname + " joined the game");
            } else {
              send("Error: Game is full. Maximum 8 players allowed.");
            }
          }
        }
        case "/start" -> {
          if (currentGame == null) {
            send("Error: No game to start.");
          } else if (currentGame.isStarted()) {
            send("Error: Game already started.");
          } else if (!currentGame.hasPlayer(nickname)) {
            send("Error: You must join the game first with /join before starting.");
          } else if (currentGame.getPlayerCount() < 2 || currentGame.getPlayerCount() > 8) {
            send("Error: Need between 2 and 8 players to start the game.");
          } else {
            currentGame.start();
          }
        }
        case "/end" -> {
          // End the game.
          if (currentGame == null) {
            send("Error: No game to end.");
          } else {
            currentGame = null;
            broadcast("Game ended.");
          }
        }
        case "/play" -> {
          // Play a card command: /play <card> [target] [guess]
          if (currentGame == null || !currentGame.isStarted()) {
              send("Error: No active game in progress.");
              break;
          }
          // Split into more tokens to handle card, target, and guess separately
          String[] playTokens = message.split(" ");
          if (playTokens.length < 2) {
              send("Error: Usage /play <card> [target] [guess]");
              break;
          }
          if (!currentGame.getCurrentPlayer().getName().equals(nickname)) {
              send("It's not your turn");
              break;
          }

          String cardName = playTokens[1].toLowerCase();
          Card card = Card.getCard(cardName);
          if (card == null) {
              send("Error: Unknown card " + cardName);
              break;
          }

          CardAction.Builder actionBuilder = new CardAction.Builder(nickname, card);

          // Parse parameters based on card requirements
          if (card.getEffect().requiresSecondTarget()) {
              if (playTokens.length < 4) {
                  send("Error: This card requires two target players");
                  break;
              }
              actionBuilder.withTarget(playTokens[2])
                          .withSecondTarget(playTokens[3]);
          } else {
              if (playTokens.length >= 3) {
                  actionBuilder.withTarget(playTokens[2]);
              }
              if (playTokens.length >= 4) {
                  try {
                      actionBuilder.withGuess(Integer.parseInt(playTokens[3]));
                  } catch (NumberFormatException e) {
                      send("Error: Guess must be a number");
                      break;
                  }
              }
          }

          try {
              currentGame.playCard(actionBuilder.build());
          } catch (IllegalArgumentException e) {
              send("Error: " + e.getMessage());
          } catch (IllegalStateException e) {
              // Don't print the "Failed to apply card effect" message
              // Only print the specific error from the card effect
          }
        }
        case "/score" -> {
          // Show scores to the requesting player.
          if (currentGame == null) {
            send("Error: No game active.");
          } else {
            send("Scores: " + currentGame.getScores());
          }
        }
        case "/hand" -> {
          // Show the player's hand.
          if (currentGame == null) {
            send("Error: No game active.");
          } else {
            send("Your hand: " + currentGame.getHand(nickname));
          }
        }
        case "/explain" -> {
          // Explain the card.
          if (tokens.length < 2) {
            send("Error: Usage /explain <card>");
          } else {
            String cardName = tokens[1];
            Card card = Card.getCard(cardName);
            if (card == null) {
              send("Error: Unknown card " + cardName);
            } else {
              send(card.getDescription());
            }
          }
        }
        case "/values" -> {
            // Show all card values
            StringBuilder values = new StringBuilder("Card Values:\n");
            for (Card card : Card.values()) {
                values.append(card.getValue())
                      .append(" - ")
                      .append(card.getName())
                      .append("\n");
            }
            send(values.toString());
        }
        default -> send("Error: Unknown command.");
      }
    }

    /**
     * Sends a message to the client.
     *
     * @param message the message to send
     */
    void send(String message) {
      out.println(message);
    }
  }
}
