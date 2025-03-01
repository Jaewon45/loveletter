package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Prince Effect - Prince (5).
 *
 * <p>When you discard the Prince, choose a player (including yourself) who must discard their hand
 * and draw a new card. If the discarded card is the Princess, that player is knocked out of the
 * round.
 */
public class PrinceEffect implements Effect {

  /**
   * Applies the Prince card effect.
   *
   * @param game the current game instance
   * @param currentPlayer the player who discarded the Prince
   * @param targetPlayer the target player who must discard their hand
   * @param guess unused for the Prince effect
   * @return true if the effect was successfully applied, false otherwise
   */
  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (targetPlayer == null || !targetPlayer.isAlive()) {
      TCPServer.sendDirect(currentPlayer.getName(), "Prince: No valid target or target is out.");
      return false;
    }

    // Force discard
    if (!targetPlayer.getHand().isEmpty()) {
      Card cardToDiscard = targetPlayer.getHand().get(0);
      if (cardToDiscard.getValue() == 8) {
        TCPServer.broadcast(
            "Prince: " + targetPlayer.getName() + " discarded the Princess and is eliminated.");

        game.eliminatePlayer(targetPlayer);
        return true;
      } else {
        TCPServer.broadcast(
            "Prince: " + targetPlayer.getName() + " discards " + cardToDiscard.toString() + ".");
        targetPlayer.discard(cardToDiscard);
        game.getDeck().draw(targetPlayer);
      }
    }

    // If target is still alive, draw a new card
    if (targetPlayer.isAlive()) {
      game.getDeck().draw(targetPlayer);
      TCPServer.broadcast("Prince: " + targetPlayer.getName() + " draws a new card.");
      return true;
    }
    return false;
  }
}
