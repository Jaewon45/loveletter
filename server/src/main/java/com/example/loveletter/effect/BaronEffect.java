package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Baron Effect - Baron Talus (3).
 *
 * <p>When you discard the Baron, choose another player still in the round. You and that player
 * secretly compare the value of the card in your hand; the player with the lower value is knocked
 * out of the round. In case of a tie, nothing happens.
 */
public class BaronEffect implements Effect {

  /**
   * Applies the Baron card effect.
   *
   * @param game the current game instance
   * @param currentPlayer the player who discarded the Baron
   * @param targetPlayer the target player chosen for comparison
   * @param guess unused for the Baron effect
   */
  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (targetPlayer == null || !targetPlayer.isAlive()) {
      TCPServer.sendDirect(currentPlayer.getName(), "Baron: No valid target.");
      return false;
    }
    // Compare card values
    if (!currentPlayer.getHand().isEmpty() && !targetPlayer.getHand().isEmpty()) {
      Card myCard = currentPlayer.getLowest();
      Card theirCard = targetPlayer.getLowest();

      String message =
          "Baron: "
              + currentPlayer.getName()
              + " ("
              + myCard.getValue()
              + ") vs. "
              + targetPlayer.getName()
              + " ("
              + theirCard.getValue()
              + ")";

      TCPServer.sendDirect(currentPlayer.getName(), message);
      TCPServer.sendDirect(targetPlayer.getName(), message);

      if (myCard.getValue() > theirCard.getValue()) {
        game.eliminatePlayer(targetPlayer);
      } else if (myCard.getValue() < theirCard.getValue()) {
        game.eliminatePlayer(currentPlayer);
      } else {
        TCPServer.broadcast("Baron: Tie => nothing happens.");
      }
      return true;
    }
    return false;
  }
}
