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
      return false;
    }

    TCPServer.broadcast("- " + currentPlayer.getName() + " uses Baroness to view " + targetPlayer.getName() + "'s hand.");
    game.revealHandToPlayer(currentPlayer, targetPlayer);
    return true;
  }
}
