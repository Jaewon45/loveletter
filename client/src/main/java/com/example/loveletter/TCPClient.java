package com.example.loveletter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient {

    private static final int SERVER_PORT = Integer.parseInt(System.getProperty("server.port", "12345"));

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", SERVER_PORT); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            ExecutorService executor = Executors.newSingleThreadExecutor();

            // Reading server messages asynchronously
            executor.execute(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                }
            });

            // Sending user input to the server
            String input;
            while ((input = userInput.readLine()) != null) {
                out.println(input);
                if ("BYE".equalsIgnoreCase(input)) {
                    break;
                }
            }

        } catch (IOException e) {
        }
    }
}
