package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;

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
  public void apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
    if (targetPlayer == null || !targetPlayer.isAlive()) {
      System.out.println("King: No valid target or target is out.");
      return;
    }

    // Swap cards (in standard Love Letter, each player has exactly 1 card)
    if (!currentPlayer.getHand().isEmpty() && !targetPlayer.getHand().isEmpty()) {
      Card myCard = currentPlayer.getHand().remove(0);
      Card theirCard = targetPlayer.getHand().remove(0);

      currentPlayer.addCard(theirCard);
      targetPlayer.addCard(myCard);

      System.out.println(
          "King: " + currentPlayer.getName() + " swapped hands with " + targetPlayer.getName());
    }
  }
}
