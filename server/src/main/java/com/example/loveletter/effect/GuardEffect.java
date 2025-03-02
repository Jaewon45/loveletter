package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Guard Effect - Guard Odette (1).
 *
 * <p>When you discard the Guard, choose a player and guess a card number (other than 1). If the
 * target player's card matches your guess, they are knocked out of the round.
 */
public class GuardEffect implements Effect {

  /**
   * Applies the Guard card effect.
   *
   * @param game the current game instance
   * @param currentPlayer the player who discarded the Guard
   * @param targetPlayer the target player chosen for the guess
   * @param guess the guessed card number (must be between 2 and 8)
   */
  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    // Must name a value other than 1, guess in [2..8]
    if (targetPlayer == null || guess < 2 || guess > 8) {
      TCPServer.sendDirect(currentPlayer.getName(), "Guard: invalid guess or no target.");
      return false;
    }
    if (!targetPlayer.isAlive()) {
      TCPServer.sendDirect(currentPlayer.getName(), "Guard: target is already knocked out.");
      return false;
    }

    // Check if the target's card matches the guess
    if (!targetPlayer.getHand().isEmpty()) {
      Card theirCard = targetPlayer.getHand().get(0);

      // Initial announcement
      TCPServer.broadcast(
          "- " + currentPlayer.getName() + " uses Guard targeting " + targetPlayer.getName() + ".");

      if (theirCard.equals(Card.ASSASSIN)) {
        TCPServer.broadcast("- The target reveals Assassin!");
        game.eliminatePlayer(currentPlayer);
        targetPlayer.discard(theirCard);
        game.drawCardFor(targetPlayer);
      } else if (theirCard.getValue() == guess) {
        TCPServer.broadcast(
            "- The guess was " + guess + " (" + theirCard.getName() + "), which was correct!");
        game.eliminatePlayer(targetPlayer);
      } else {
        TCPServer.broadcast("- The guess was " + guess + ", which was incorrect.");
      }

      return true;
    }
    return false;
  }
}
