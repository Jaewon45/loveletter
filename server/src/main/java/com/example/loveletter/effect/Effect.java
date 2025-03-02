package com.example.loveletter.effect;

import java.util.List;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

/**
 * Represents the special power of a card.
 *
 * <p>Each card's unique effect is implemented by a class that implements this interface. The effect
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
  boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget);

  // New method to indicate if effect needs a second target
  default boolean requiresSecondTarget() {
    return false;
  }

  /**
   * Checks if there are any valid targets for the effect. If all other players are protected by
   * Handmaid, the effect cannot target anyone.
   */
  default boolean hasValidTargets(Game game, Player currentPlayer) {
    return !validTargets(game, currentPlayer).isEmpty();
  }

  default List<Player> validTargets(Game game, Player currentPlayer) {
    return game.getAlivePlayers().stream()
        .filter(p -> p != currentPlayer && !p.isProtectedByHandmaid())
        .toList();
  }
}
