package com.example.loveletter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TCPClient {
    private BufferedReader consoleReader;

    public TCPClient() {
        consoleReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() throws IOException {
        String nickname;
        while (true) {
            System.out.print("Enter your nickname: ");
            nickname = consoleReader.readLine().trim();
            if (!nickname.matches("[a-zA-Z0-9]+")) {
                System.out.println("Nickname must be alphanumeric without spaces or special characters. Please try again.");
                continue;
            }
            
            // Check if nickname is a card name
            if (Card.getCard(nickname) != null) {
                System.out.println("Nickname cannot be a card name. Please choose a different nickname.");
                continue;
            }
            
            break;
        }
    }
} 