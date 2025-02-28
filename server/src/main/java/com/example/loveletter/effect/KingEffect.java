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
  public boolean apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
    if (targetPlayer == null || !targetPlayer.isAlive()) {
      TCPServer.sendDirect(currentPlayer.getName(), "King: No valid target or target is out.");
      return false;
    }

    // Swap cards (in standard Love Letter, each player has exactly 1 card)
    if (!currentPlayer.getHand().isEmpty() && !targetPlayer.getHand().isEmpty()) {
      Card myCard =
          currentPlayer.getHand().stream()
              .filter(card -> !"King".equals(card.getName()))
              .findFirst()
              .orElse(null);
      Card theirCard = targetPlayer.getHand().get(0);

      currentPlayer.addCard(theirCard);
      targetPlayer.addCard(myCard);
      currentPlayer.removeCard(myCard);
      targetPlayer.removeCard(theirCard);

      TCPServer.broadcast(
          "King: " + currentPlayer.getName() + " swapped hands with " + targetPlayer.getName());
      TCPServer.sendDirect(
          currentPlayer.getName(),
          "King: You swapped hands with "
              + targetPlayer.getName()
              + ". Your new card: "
              + theirCard);
      TCPServer.sendDirect(
          targetPlayer.getName(),
          "King: You swapped hands with " + currentPlayer.getName() + ". Your new card: " + myCard);
      return true;
    }
    return false;
  }
}
