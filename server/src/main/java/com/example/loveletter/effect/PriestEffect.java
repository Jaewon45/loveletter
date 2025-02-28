package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Priest Effect - Priest Tomas (2).
 *
 * <p>When you discard the Priest, you may look at another player's hand.
 */
public class PriestEffect implements Effect {

  /**
   * Applies the Priest card effect.
   *
   * @param game the current game instance
   * @param currentPlayer the player who discarded the Priest
   * @param targetPlayer the target player whose hand will be revealed
   * @param guess unused for the Priest effect
   */
  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (targetPlayer == null || !targetPlayer.isAlive()) {
      TCPServer.sendDirect(currentPlayer.getName(), "Priest: No valid target or target is out.");
      return false;
    }
    if (!targetPlayer.getHand().isEmpty()) {
      Card card = targetPlayer.getHand().get(0);
      TCPServer.broadcast(
          "Priest: " + currentPlayer.getName() + " sees " + targetPlayer.getName() + "'s hand ");

      TCPServer.sendDirect(
          currentPlayer.getName(),
          "Priest: " + targetPlayer.getName() + "'s hand: " + card.getName());
      return true;
    }
    return false;
  }
}
