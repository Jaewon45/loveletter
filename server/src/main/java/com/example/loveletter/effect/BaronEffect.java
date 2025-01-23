package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;

public class BaronEffect implements Effect {
    @Override
    public void apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
        if (targetPlayer == null || !targetPlayer.isAlive()) {
            System.out.println("Baron: No valid target.");
            return;
        }
        // Compare ranks
        if (!currentPlayer.getHand().isEmpty() && !targetPlayer.getHand().isEmpty()) {
            Card myCard = currentPlayer.getHand().get(0);
            Card theirCard = targetPlayer.getHand().get(0);

            System.out.println("Baron: " + currentPlayer.getName() + " (" + myCard.getRank()
                    + ") vs. " + targetPlayer.getName() + " (" + theirCard.getRank() + ")");
            if (myCard.getRank() > theirCard.getRank()) {
                game.eliminatePlayer(targetPlayer);
            } else if (myCard.getRank() < theirCard.getRank()) {
                game.eliminatePlayer(currentPlayer);
            } else {
                System.out.println("Baron: Tie => nothing happens.");
            }
        }
    }
}
