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
 * TCPClient connects to the Love Letter server and facilitates user input/output.
 *
 * <p>The client connects to the server using the host and port specified by system properties
 * (defaults: host = "localhost", port = 12345). It prompts the user for a nickname, sends messages
 * from the user to the server, and displays incoming messages from the server asynchronously.
 */
public class TCPClient {

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

  /**
   * The main method initiates the client, connects to the server, and handles input/output.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {
    try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
        BufferedReader consoleReader =
            new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        BufferedReader in =
            new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        PrintWriter out =
            new PrintWriter(
                new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)),
                true)) {

      // Prompt the user to enter a nickname.
      System.out.print("Enter your nickname: ");
      String nickname = consoleReader.readLine();
      out.println(nickname);

      // Start a new thread to read messages from the server asynchronously.
      new Thread(
              () -> {
                String msg;
                try {
                  while ((msg = in.readLine()) != null) {
                    System.out.println(msg);
                  }
                } catch (IOException e) {
                  LOGGER.log(Level.SEVERE, "Error reading message from server", e);
                }
              })
          .start();

      // Main thread: read user input from the console and send it to the server.
      String userInput;
      while ((userInput = consoleReader.readLine()) != null) {
        out.println(userInput);
        if ("bye".equalsIgnoreCase(userInput.trim())) {
          break;
        }
      }
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "An I/O error occurred", ex);
    }
  }
}
