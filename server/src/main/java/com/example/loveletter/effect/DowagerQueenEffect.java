package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Represents the effect of the DowagerQueen card. The player compares their hand with another
 * player; the higher value loses.
 */
public class DowagerQueenEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (!hasValidTargets(game, currentPlayer)) {
      TCPServer.broadcast(
          "- "
              + currentPlayer.getName()
              + " discards DowagerQueen with no effect (no valid targets).");
      return true;
    }

    if (targetPlayer == null
        || currentPlayer == targetPlayer
        || !targetPlayer.isAlive()
        || targetPlayer.isProtectedByHandmaid()) {
      TCPServer.sendDirect(currentPlayer.getName(), "Invalid target: Must choose another player.");
      return false;
    }

    TCPServer.broadcast(
        "- "
            + currentPlayer.getName()
            + " uses DowagerQueen targeting "
            + targetPlayer.getName()
            + ".");

    int currentValue = currentPlayer.getHand().get(0).getValue();
    int targetValue = targetPlayer.getHand().get(0).getValue();

    // Send private comparison message only to the player who used DowagerQueen
    String compareResult =
        String.format(
            "Card Comparison: Your %s (%d) vs opponent's %s (%d)",
            currentPlayer.getHand().get(0).getName(),
            currentValue,
            targetPlayer.getHand().get(0).getName(),
            targetValue);
    TCPServer.sendDirect(currentPlayer.getName(), compareResult);

    if (currentValue > targetValue) {
      TCPServer.broadcast(
          "- " + currentPlayer.getName() + " had the higher value and is eliminated.");
      game.eliminatePlayer(currentPlayer);
    } else if (currentValue < targetValue) {
      TCPServer.broadcast(
          "- " + targetPlayer.getName() + " had the higher value and is eliminated.");
      game.eliminatePlayer(targetPlayer);
    } else {
      TCPServer.broadcast("The values were equal - no effect.");
    }
    return true;
  }
}
