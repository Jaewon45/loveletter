package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

/**
 * Each card's special power is implemented by a class that implements this
 * interface. The effect is applied when the card is discarded (unless otherwise
 * noted by special rules).
 */
public interface Effect {

    /**
     * Apply this card's effect in the context of the given Game.
     *
     * @param game The Game instance (contains Deck, players, etc.)
     * @param currentPlayer The player who discarded this card
     * @param targetPlayer Another player chosen as the target (may be null)
     * @param guess Used by cards like Guard (otherwise -1)
     */
    void apply(Game game, Player currentPlayer, Player targetPlayer, int guess);
}
