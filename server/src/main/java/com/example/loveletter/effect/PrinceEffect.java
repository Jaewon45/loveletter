package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;

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
   */
  @Override
  public void apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
    if (targetPlayer == null || !targetPlayer.isAlive()) {
      System.out.println("Prince: No valid target or target is out.");
      return;
    }
    // Force discard
    if (!targetPlayer.getHand().isEmpty()) {
      Card cardToDiscard = targetPlayer.getHand().get(0);
      game.discardCard(targetPlayer, cardToDiscard, null, -1);
      // If the discarded card was the Princess, the target is knocked out by discardCard.
    }

    // If target is still alive, draw a new card
    if (targetPlayer.isAlive()) {
      Card newCard = game.getDeck().draw();
      if (newCard != null) {
        targetPlayer.addCard(newCard);
        System.out.println("Prince: " + targetPlayer.getName() + " draws a new card.");
      }
    }
  }
}
