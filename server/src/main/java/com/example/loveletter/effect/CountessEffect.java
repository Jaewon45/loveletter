package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

/**
 * Countess Effect - Countess Wilhelmina (7).
 *
 * <p>Discarding the Countess has no immediate effect. However, if you hold the Countess along with
 * the King or Prince, you must discard the Countess. That rule is enforced elsewhere (e.g., during
 * the draw or discard process).
 */
public class CountessEffect implements Effect {

  /**
   * Applies the Countess card effect.
   *
   * @param game the current game instance
   * @param currentPlayer the player who discarded the Countess
   * @param targetPlayer unused for the Countess effect
   * @param guess unused for the Countess effect
   */
  @Override
  public void apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
    System.out.println("Countess: No immediate effect on discard.");
  }
}
