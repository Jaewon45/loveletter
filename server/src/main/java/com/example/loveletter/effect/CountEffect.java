package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Represents the effect of the Count card. Increases the value of the player's hand by 1 at the end
 * of the round.
 */
public class CountEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    TCPServer.broadcast(
        "- " + currentPlayer.getName() + " uses Count to increase their hand value.");
    currentPlayer.countIncrease++;
    return true;
  }
}
