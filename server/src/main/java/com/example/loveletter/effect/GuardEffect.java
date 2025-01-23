package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;

public class GuardEffect implements Effect {

    @Override
    public void apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
        // Must name a rank other than 1, guess in [2..8]
        if (targetPlayer == null || guess < 2 || guess > 8) {
            System.out.println("Guard: invalid guess or no target.");
            return;
        }
        if (!targetPlayer.isAlive()) {
            System.out.println("Guard: target is already knocked out.");
            return;
        }

        // Check if the target's card matches guess
        if (!targetPlayer.getHand().isEmpty()) {
            Card theirCard = targetPlayer.getHand().get(0); // standard Love Letter is 1 card
            if (theirCard.getRank() == guess) {
                game.eliminatePlayer(targetPlayer);
            } else {
                System.out.println("Guard: Guess was incorrect!");
            }
        }
    }
}
