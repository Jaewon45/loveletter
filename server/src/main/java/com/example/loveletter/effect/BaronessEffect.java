package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Represents the effect of the Baroness card. Allows the player to look at the hands of one or two
 * other players.
 */
public class BaronessEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (targetPlayer == null) {
      TCPServer.sendDirect(
          currentPlayer.getName(), "Invalid target: At least one player must be selected.");
      return false;
    }

    TCPServer.broadcast(
        "- " + currentPlayer.getName() + " uses Baroness to view other players' hands.");

    game.revealHandToPlayer(currentPlayer, targetPlayer);

    if (secondTarget != null) {
      game.revealHandToPlayer(currentPlayer, secondTarget);
    }

    return true;
  }
}
