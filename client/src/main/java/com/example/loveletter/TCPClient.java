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
  // J: entry point (?) - to be accessible from outside its package, so that java runtime can
  // execute it
  // J: socket을 이용해서 서버에 연결, 실시간으로 메시지 전송, 채팅창 메시지 입력받는 separate thread `multithreading` 하는 클래스
  // J: encapsulation by private constants, modularity by separate concerns

  /** Logger for logging client events and errors. */
  private static final Logger LOGGER = Logger.getLogger(TCPClient.class.getName());

  // J: private; 다른 클래스에서 수정 불가, static; 클래스내 모든 인스턴스에서 공유됨, final; not to be reassigned

  /**
   * The port number of the server.
   *
   * <p>Defaults to 12345 if the "server.port" system property is not set.
   */
  private static final int SERVER_PORT =
      Integer.parseInt(System.getProperty("server.port", "12345"));

  // J: private; port shouldn't be modifiable by external classes

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

  // J: not private as it can be accessed from a diff thread ?

  /**
   * The main method initiates the client, connects to the server, and handles input/output.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {
    // Create a single consoleReader that wraps System.in (do not close it)
    BufferedReader consoleReader =
        new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    while (true) { // J: infinite loop 문제가 생기거나 연결 끊기더라도 keep retrying connections
      try (Socket socket =
              new Socket(SERVER_HOST, SERVER_PORT); // J: create socket conn to the server
          BufferedReader in =
              new BufferedReader( // J: reads data from the server
                  new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
          PrintWriter server =
              new PrintWriter( // J: send msg to the server
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
            new Thread( // J: separate thread to listen for msg from the server
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
          } else { // J: 나머지 경우엔 인풋 서버로 전송
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
