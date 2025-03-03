package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;
import java.util.List;

/**
 * Represents the effect of the Sycophant card. Forces the next player to include the selected
 * player as a target if their card has a targeting effect.
 */
public class SycophantEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (targetPlayer == null) {
      TCPServer.sendDirect(currentPlayer.getName(), "Sycophant requires a target player.");
      return false;
    }

    if (!targetPlayer.isAlive()) {
      TCPServer.sendDirect(currentPlayer.getName(), "Cannot target an eliminated player.");
      return false;
    }

    game.setForcedTarget(targetPlayer);
    TCPServer.broadcast(
        "- "
            + currentPlayer.getName()
            + " uses Sycophant targeting "
            + targetPlayer.getName()
            + ". The next card played must include "
            + targetPlayer.getName()
            + " as a target if it has a targeting effect.");

    return true;
  }

  @Override
  public boolean canTargetSelf() {
    return true;
  }

  @Override
  public List<Player> validTargets(Game game, Player currentPlayer) {
    return game.getAlivePlayers().stream()
        .filter(p -> !p.isProtectedByHandmaid() || p == currentPlayer)
        .toList();
  }
}
