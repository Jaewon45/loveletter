package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Represents the effect of the Jester card. If the chosen player wins the round, the current player
 * gains a Token of Affection.
 */
public class JesterEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (!hasValidTargets(game, currentPlayer)) {
      TCPServer.broadcast("- " + currentPlayer.getName() + " discards Jester with no effect (no valid targets).");
      return true;
    }

    if (targetPlayer == null || !targetPlayer.isAlive() || targetPlayer.isProtectedByHandmaid()) {
      TCPServer.sendDirect(currentPlayer.getName(), "Invalid target: Player must be in the round.");
      return false;
    }

    if (targetPlayer == currentPlayer) {
      TCPServer.sendDirect(currentPlayer.getName(), "Invalid target: Cannot target yourself with Jester.");
      return false;
    }

    TCPServer.broadcast(
        "- " + currentPlayer.getName() + " uses Jester targeting " + targetPlayer.getName() + ".");
    currentPlayer.setJesterTarget(targetPlayer);
    return true;
  }
}
