package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

/**
 * Represents the effect of the Bishop card.
 * If the named number matches a player's card, the player gains a Token of Affection.
 */
public class BishopEffect implements Effect {

    @Override
    public boolean apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
        if (targetPlayer == null || guess < 1 || guess > 8) {
            return false;
        }
        if (targetPlayer.getHand().get(0).getValue() == guess) {
            game.awardToken(currentPlayer);
            targetPlayer.discard(targetPlayer.getHand().get(0));
            game.drawCardFor(targetPlayer);
        }
        return true;
    }
}