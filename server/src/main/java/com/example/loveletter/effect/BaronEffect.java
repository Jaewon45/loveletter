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
    if (!hasValidTargets(game, currentPlayer)) {
      TCPServer.broadcast(
          "- " + currentPlayer.getName() + " discards Baron with no effect (no valid targets).");
      return true;
    }

    if (targetPlayer == null || !targetPlayer.isAlive() || targetPlayer.isProtectedByHandmaid()) {
      TCPServer.sendDirect(currentPlayer.getName(), "Invalid target: Player must be in the round.");
      return false;
    }

    if (targetPlayer == currentPlayer) {
      TCPServer.sendDirect(
          currentPlayer.getName(), "Invalid target: Cannot target yourself with Baron.");
      return false;
    }

    // Compare card values
    if (!currentPlayer.getHand().isEmpty() && !targetPlayer.getHand().isEmpty()) {
      Card myCard = currentPlayer.getLowest();
      Card theirCard = targetPlayer.getLowest();

      // Public announcement
      TCPServer.broadcast(
          "- " + currentPlayer.getName() + " uses Baron targeting " + targetPlayer.getName() + ".");

      // Private reveals
      String compareResult =
          "- Card Comparison: "
              + currentPlayer.getName()
              + " ("
              + myCard.getName()
              + " - "
              + myCard.getValue()
              + ") vs "
              + targetPlayer.getName()
              + " ("
              + theirCard.getName()
              + " - "
              + theirCard.getValue()
              + ")";
      TCPServer.sendDirect(currentPlayer.getName(), compareResult);
      TCPServer.sendDirect(targetPlayer.getName(), compareResult);

      // Result announcement and elimination
      if (myCard.getValue() > theirCard.getValue()) {
        TCPServer.broadcast("- " + targetPlayer.getName() + " is eliminated (lower value).");
        game.eliminatePlayer(targetPlayer);
      } else if (myCard.getValue() < theirCard.getValue()) {
        TCPServer.broadcast("- " + currentPlayer.getName() + " is eliminated (lower value).");
        game.eliminatePlayer(currentPlayer);
      } else {
        TCPServer.broadcast("- The values were equal - no effect.");
      }

      return true;
    }
    return false;
  }
}
