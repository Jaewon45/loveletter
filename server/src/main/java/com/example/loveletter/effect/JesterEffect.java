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
    if (targetPlayer == null) {
      TCPServer.sendDirect(currentPlayer.getName(), "Invalid target: Player must be selected.");
      return false;
    }

    TCPServer.broadcast("- " + currentPlayer.getName() + " uses Jester targeting " + targetPlayer.getName() + ".");
    currentPlayer.setJesterTarget(targetPlayer);
    return true;
  }

  @Override
  public boolean requiresSecondTarget() {
    return true;
  }
}
