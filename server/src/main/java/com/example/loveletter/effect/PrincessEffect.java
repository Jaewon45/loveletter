package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

/**
 * Princess Annette (8).
 *
 * If you discard the Princess, you are knocked out immediately. So typically,
 * we handle that in 'Game.discardCard' before calling this 'apply' method. This
 * effect might never be called in practice.
 */
public class PrincessEffect implements Effect {

    @Override
    public void apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
        throw new RuntimeException("Princess: Should not actually call apply if value=8 is discarded.");
    }
}
