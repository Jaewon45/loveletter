package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

/**
 * Represents the effect of the Constable card.
 * If knocked out while this is in the discard pile, gain a Token of Affection.
 */
public class ConstableEffect implements Effect {

    @Override
    public boolean apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
        return true;
    }
}