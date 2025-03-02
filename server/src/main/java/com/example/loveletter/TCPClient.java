import java.io.BufferedReader;
import java.io.IOException;

public class TCPClient {
    private BufferedReader consoleReader;

    public void start() throws IOException {
        String nickname;
        while (true) {
            System.out.print("Enter your nickname (3-10 letters): ");
            nickname = consoleReader.readLine().trim();
            
            if (!nickname.matches("[a-zA-Z0-9]+")) {
                System.out.println("Nickname must be alphanumeric without spaces or special characters. Please try again.");
                continue;
            }
            
            if (nickname.length() < 3) {
                System.out.println("Nickname must be at least 3 characters long. Please try again.");
                continue;
            }
            
            if (nickname.length() > 10) {
                System.out.println("Nickname is too long (max 10 characters) for other players to type easily. Please try again.");
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