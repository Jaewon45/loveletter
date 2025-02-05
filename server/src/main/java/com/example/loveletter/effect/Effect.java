package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

/**
 * Represents the special power of a card.
 *
 * <p>Each card’s unique effect is implemented by a class that implements this interface. The effect
 * is applied when the card is discarded (unless otherwise noted by special rules).
 */
public interface Effect {

  /**
   * Applies this card's effect in the context of the given game.
   *
   * @param game the Game instance (contains the deck, players, etc.)
   * @param currentPlayer the player who discarded this card
   * @param targetPlayer another player chosen as the target (may be null)
   * @param guess used by certain card effects (e.g., Guard), otherwise -1
   */
  void apply(Game game, Player currentPlayer, Player targetPlayer, int guess);
}
