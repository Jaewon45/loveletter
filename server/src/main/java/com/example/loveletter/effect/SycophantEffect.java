package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Represents the effect of the Sycophant card. Forces the next player to target the selected player
 * with their effect.
 */
public class SycophantEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (targetPlayer == null) {
      return false; // Target is required
    }
    game.setForcedTarget(targetPlayer);
    TCPServer.broadcast("Jester: the next player has to target:" + targetPlayer.getName());
    return true;
  }
}
