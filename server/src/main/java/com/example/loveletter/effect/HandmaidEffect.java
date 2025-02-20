package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Handmaid Effect - Handmaid (4).
 *
 * <p>When you discard the Handmaid, you become protected until your next turn, preventing other
 * players from targeting you with card effects.
 */
public class HandmaidEffect implements Effect {

  /**
   * Applies the Handmaid card effect.
   *
   * @param game the current game instance
   * @param currentPlayer the player who discarded the Handmaid
   * @param targetPlayer unused for the Handmaid effect
   * @param guess unused for the Handmaid effect
   */
  @Override
  public boolean apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
    currentPlayer.setProtectedByHandmaid(true);
    TCPServer.broadcast(
        "Handmaid: " + currentPlayer.getName() + " is protected until their next turn.", null);
    return true;
  }
}
