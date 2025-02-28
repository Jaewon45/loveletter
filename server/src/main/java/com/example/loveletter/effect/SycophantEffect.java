package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

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
    return true;
  }
}
