package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Represents the effect of the Dowager Queen card. The player compares their hand with another
 * player; the higher value loses.
 */
public class DowagerQueenEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (targetPlayer == null || currentPlayer == targetPlayer) {
      TCPServer.sendDirect(currentPlayer.getName(), "Invalid target: Must choose another player.");
      return false;
    }

    TCPServer.broadcast(
        "- "
            + currentPlayer.getName()
            + " uses Dowager Queen targeting "
            + targetPlayer.getName()
            + ".");

    int currentValue = currentPlayer.getHand().get(0).getValue();
    int targetValue = targetPlayer.getHand().get(0).getValue();
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
