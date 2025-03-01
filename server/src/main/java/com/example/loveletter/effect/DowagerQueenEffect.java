package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

/**
 * Represents the effect of the Dowager Queen card. The player compares their hand with another
 * player; the higher value loses.
 */
public class DowagerQueenEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (targetPlayer == null || currentPlayer == targetPlayer) {
      return false; // Must choose another player
    }

    int currentValue = currentPlayer.getHand().get(0).getValue();
    int targetValue = targetPlayer.getHand().get(0).getValue();
    if (currentValue > targetValue) {
      game.eliminatePlayer(currentPlayer);
    } else if (currentValue < targetValue) {
      game.eliminatePlayer(targetPlayer);
    }
    return true;
  }
}
