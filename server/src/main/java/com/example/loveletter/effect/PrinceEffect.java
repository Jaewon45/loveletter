package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;
import java.util.ArrayList;
import java.util.List;

/**
 * Prince Effect - Prince Arnaud (5).
 *
 * <p>When you discard Prince Arnaud, choose one player (including yourself) to discard their hand
 * and draw a new card. If the Princess is discarded this way, that player is eliminated. If the
 * deck is empty, the player draws the face-down card from the start of the round. If all other
 * players are protected by the Handmaid, you must choose yourself.
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
    // Prince can target self, so only check other players if not targeting self
    if (targetPlayer != currentPlayer && !hasValidTargets(game, currentPlayer)) {
      TCPServer.broadcast(
          "- "
              + currentPlayer.getName()
              + " must target themselves with Prince (no other valid targets).");
      targetPlayer = currentPlayer;
    }

    if (targetPlayer == null) {
      TCPServer.sendDirect(currentPlayer.getName(), "Invalid target: Player must be selected.");
      return false;
    }

    // Force discard of current hand
    List<Card> discardedCards = new ArrayList<>(targetPlayer.getHand());
    targetPlayer.clearHand();

    // Announce the discard
    TCPServer.broadcast(
        "- "
            + currentPlayer.getName()
            + " uses Prince. "
            + targetPlayer.getName()
            + " discards "
            + discardedCards.get(0).getName()
            + ".");

    // Show discard pile
    TCPServer.broadcast("- " + targetPlayer.getName() + "'s discarded cards: " + discardedCards);

    // Draw new card silently and then announce it
    Card newCard = game.getDeck().draw(targetPlayer);
    TCPServer.sendDirect(
        targetPlayer.getName(), "🃏 " + newCard.getName() + " was added to your hand.");
    TCPServer.broadcast(targetPlayer.getName() + " draws a new card.");

    return true;
  }
}
