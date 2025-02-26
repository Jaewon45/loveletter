package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Princess Effect - Princess Annette (8).
 *
 * <p>Discarding the Princess results in immediate elimination from the round. Typically, this is
 * handled during the discard process, and this method should not be called in normal play.
 */
public class PrincessEffect implements Effect {

  /**
   * Applies the Princess card effect.
   *
   * @param game the current game instance
   * @param currentPlayer the player who discarded the Princess
   * @param targetPlayer unused for the Princess effect
   * @param guess unused for the Princess effect
   * @throws RuntimeException if called, as discarding the Princess should immediately eliminate the
   *     player
   */
  @Override
  public boolean apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
    TCPServer.broadcast(
        "Princess discarded, Player " + currentPlayer.getName() + " has been eliminated.");
    game.eliminatePlayer(currentPlayer);
    return true;
  }
}
