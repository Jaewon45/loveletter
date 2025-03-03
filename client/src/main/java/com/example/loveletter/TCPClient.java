package com.example.loveletter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCPClient connects to the Love Letter server and facilitates user input/output. works as an entry
 * poin for the client application so that java runtime can execute it
 *
 * <p>The client connects to the server using the host and port specified by system properties
 * (defaults: host = "localhost", port = 12345). It prompts the user for a nickname, sends messages
 * from the user to the server via socket, and displays incoming messages from the server
 * asynchronously.
 */
public class TCPClient {
  // encapsulation by private constants, modularity by separate concerns

  /** Logger for logging client events and errors. */
  private static final Logger LOGGER = Logger.getLogger(TCPClient.class.getName());

  /**
   * The port number of the server.
   *
   * <p>Defaults to 12345 if the "server.port" system property is not set.
   */
  private static final int SERVER_PORT =
      Integer.parseInt(System.getProperty("server.port", "12345"));

  /**
   * The hostname of the server.
   *
   * <p>Defaults to "localhost" if the "server.host" system property is not set.
   */
  private static final String SERVER_HOST = System.getProperty("server.host", "localhost");

  /*
   * A flag to indicate if the client is reconnecting to the server.
   */
  static boolean reconnecting = false;

  /**
   * The main method initiates the client, connects to the server, and handles input/output.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {
    // Create a single consoleReader that wraps System.in (do not close it)
    BufferedReader consoleReader =
        new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    while (true) { // infinite loop to keep retrying connections
      try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT); // create socket conn to the server
          BufferedReader in =
              new BufferedReader( // eads data from the server
                  new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
          PrintWriter server =
              new PrintWriter( // send msg to the server
                  new BufferedWriter(
                      new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)),
                  true)) {

        // Prompt for nickname
        String nickname;
        while (true) {
          System.out.print("Enter your nickname: ");
          nickname = consoleReader.readLine().trim();
          if (!nickname.isEmpty()) {
            break;
          }
          System.out.println("Nickname cannot be empty. Please try again.");
        }
        server.println(nickname);

        // Start a thread to asynchronously read messages from the server.
        Thread readerThread =
            new Thread( // separate thread to listen for msg from the server
                () -> {
                  String msg;
                  try {
                    while ((msg = in.readLine()) != null) {
                      System.out.println(msg);
                    }
                  } catch (IOException e) {
                    if (reconnecting) {
                      reconnecting = false;
                    } else {
                      LOGGER.log(Level.SEVERE, "Error reading message from server", e);
                    }
                  }
                });
        readerThread.setDaemon(true); // terminates when main program ends
        readerThread.start();

        // Main loop: read user input and send to the server.
        String userInput;
        while ((userInput = consoleReader.readLine()) != null) {
          if ("bye".equalsIgnoreCase(userInput.trim())) { // J: exit the client
            System.exit(0);
          } else if ("/reconnect".equalsIgnoreCase(userInput.trim())) { // J: 소켓 닫고 재연결
            System.out.println("Reconnecting to the server...");
            reconnecting = true;
            socket.close(); // This will cause the reader thread to exit.
            readerThread.interrupt();
            break;
          } else { // send to the server
            server.println(userInput);
          }
        }

        // Wait briefly for the reader thread to finish cleanup.
        try {
          readerThread.join(1000); // main thread wait 1s
        } catch (InterruptedException ex) { // if waiting is interrupted
          Thread.currentThread()
              .interrupt(); // add interrupted flag so that the main thread can be handled properly
          // later
        }

      } catch (IOException ex) {
        LOGGER.log(Level.SEVERE, "I/O error during session", ex);
      }
    }
  }
}
