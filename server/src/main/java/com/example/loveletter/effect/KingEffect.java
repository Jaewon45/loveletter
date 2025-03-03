package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * King Effect - King (6).
 *
 * <p>When you discard the King, choose another player and swap your hand with theirs.
 */
public class KingEffect implements Effect {

  /**
   * Applies the King card effect.
   *
   * @param game the current game instance
   * @param currentPlayer the player who discarded the King
   * @param targetPlayer the target player with whom to swap hands
   * @param guess unused for the King effect
   */
  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (!hasValidTargets(game, currentPlayer)) {
      TCPServer.broadcast(
          "- " + currentPlayer.getName() + " discards King with no effect (no valid targets).");
      return true;
    }

    if (targetPlayer == null || !targetPlayer.isAlive() || targetPlayer.isProtectedByHandmaid()) {
      TCPServer.sendDirect(currentPlayer.getName(), "Invalid target: Player must be in the round.");
      return false;
    }

    // Swap cards
    if (!currentPlayer.getHand().isEmpty() && !targetPlayer.getHand().isEmpty()) {
      Card myCard =
          currentPlayer.getHand().stream()
              .filter(card -> !"King".equals(card.getName()))
              .findFirst()
              .orElse(null);
      Card theirCard = targetPlayer.getHand().get(0);

      // Public announcement
      TCPServer.broadcast(
          "- " + currentPlayer.getName() + " uses King targeting " + targetPlayer.getName() + ".");

      // Perform swap
      currentPlayer.addCard(theirCard);
      targetPlayer.addCard(myCard);
      currentPlayer.removeCard(myCard);
      targetPlayer.removeCard(theirCard);

      // Announce card movements to all players
      TCPServer.broadcast(
          "- " + currentPlayer.getName() + " and " + targetPlayer.getName() + " swap their hands.");

      // Show new hands to respective players
      game.revealHandToPlayer(currentPlayer, currentPlayer);
      game.revealHandToPlayer(targetPlayer, targetPlayer);

      return true;
    }
    return false;
  }
}
