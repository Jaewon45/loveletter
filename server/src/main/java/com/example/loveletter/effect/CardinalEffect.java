package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

/**
 * Represents the effect of the Cardinal card.
 * Allows two players to swap hands and lets the current player peek at one.
 */
public class CardinalEffect implements Effect {

    @Override
    public boolean apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
        if (targetPlayer == null) {
            return false;
        }
        Player secondTarget = game.getSecondTarget(currentPlayer, targetPlayer);
        if (secondTarget == null) {
            return false;
        }
        List<Card> tempHand = new ArrayList<>(targetPlayer.getHand());
        targetPlayer.setHand(secondTarget.getHand());
        secondTarget.setHand(tempHand);
        game.revealHandToPlayer(currentPlayer, targetPlayer);
        return true;
    }
}
