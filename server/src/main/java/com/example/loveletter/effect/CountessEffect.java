package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Countess Effect - Countess Wilhelmina (7).
 *
 * <p>The Countess must be discarded if you have either the King or Prince in your hand. This rule 
 * applies while she is in your hand, not when she is played. You don't need to show the other card 
 * when discarding her. You may also choose to discard the Countess even without holding the King 
 * or Prince.
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
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    TCPServer.broadcast("- " + currentPlayer.getName() + " uses Countess.");
    return true;
  }
}
