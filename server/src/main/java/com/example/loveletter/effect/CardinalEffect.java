package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the effect of the Cardinal card. Allows two players to swap hands and lets the current
 * player peek at one.
 */
public class CardinalEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (targetPlayer == null || secondTarget == null) {
      TCPServer.sendDirect(
          currentPlayer.getName(),
          "Invalid target: Cardinal requires exactly two players to swap hands.");
      return false;
    }

    if (!targetPlayer.isAlive() || !secondTarget.isAlive()) {
      TCPServer.sendDirect(
          currentPlayer.getName(), "Invalid target: Both players must be in the round.");
      return false;
    }

    // Swap hands between target players
    List<Card> tempHand = new ArrayList<>(targetPlayer.getHand());
    targetPlayer.setHand(secondTarget.getHand());
    secondTarget.setHand(tempHand);

    // Announce the swap
    TCPServer.broadcast(
        "- "
            + currentPlayer.getName()
            + " uses Cardinal. "
            + targetPlayer.getName()
            + " and "
            + secondTarget.getName()
            + " swap hands.");

    // Let current player peek at one of the hands (first target's hand)
    game.revealHandToPlayer(currentPlayer, targetPlayer);

    return true;
  }

  @Override
  public boolean requiresSecondTarget() {
    return true;
  }
}
