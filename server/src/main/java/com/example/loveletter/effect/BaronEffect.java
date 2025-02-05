package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;

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
  public void apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
    if (targetPlayer == null || !targetPlayer.isAlive()) {
      System.out.println("Baron: No valid target.");
      return;
    }
    // Compare card values
    if (!currentPlayer.getHand().isEmpty() && !targetPlayer.getHand().isEmpty()) {
      Card myCard = currentPlayer.getHand().get(0);
      Card theirCard = targetPlayer.getHand().get(0);

      System.out.println(
          "Baron: "
              + currentPlayer.getName()
              + " ("
              + myCard.getValue()
              + ") vs. "
              + targetPlayer.getName()
              + " ("
              + theirCard.getValue()
              + ")");
      if (myCard.getValue() > theirCard.getValue()) {
        game.eliminatePlayer(targetPlayer);
      } else if (myCard.getValue() < theirCard.getValue()) {
        game.eliminatePlayer(currentPlayer);
      } else {
        System.out.println("Baron: Tie => nothing happens.");
      }
    }
  }
}
