package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;


public class PrinceEffect implements Effect {
    @Override
    public void apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
        if (targetPlayer == null || !targetPlayer.isAlive()) {
            System.out.println("Prince: No valid target or target is out.");
            return;
        }
        // Force discard
        if (!targetPlayer.getHand().isEmpty()) {
            Card cardToDiscard = targetPlayer.getHand().get(0);
            game.discardCard(targetPlayer, cardToDiscard, null, -1); 
            // If that was the Princess => target is knocked out in discardCard.
        }

        // If target is still alive, draw a new card
        if (targetPlayer.isAlive()) {
            Card newCard = game.getDeck().draw();
            if (newCard != null) {
                targetPlayer.addCard(newCard);
                System.out.println("Prince: " + targetPlayer.getName() + " draws a new card.");
            }        
}
    }
}
