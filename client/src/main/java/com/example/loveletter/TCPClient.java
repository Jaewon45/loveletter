package com.example.loveletter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@SuppressWarnings("CallToPrintStackTrace")
public class TCPClient {

    private static final int SERVER_PORT = Integer.parseInt(System.getProperty("server.port", "12345"));
    private static final String SERVER_HOST = System.getProperty("server.host", "localhost");

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT); BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true); BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Connected to server on port " + SERVER_PORT);

            Thread serverListener = new Thread(() -> {
                try {
                    String message;
                    while ((message = serverIn.readLine()) != null) {
                        if (message.startsWith("BROADCAST:") || message.startsWith("WELCOME:") || message.startsWith("ERROR:") || message.startsWith("GAME:") || message.startsWith("RECEIVED:")) {
                            System.out.println("SERVER: " + message);
                        } else {
                            System.out.println("SERVER: " + message);
                            String userResponse = userIn.readLine();
                            if (userResponse != null) {
                                serverOut.println(userResponse);
                                System.out.println("SENDING: " + userResponse);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                    System.err.println("I/O error: " + e.getMessage());
                }
            });

            serverListener.start();

            serverListener.join();

            System.out.println("Client terminated.");

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
