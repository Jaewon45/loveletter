package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Represents the effect of the Constable card. If knocked out while this is in the discard pile,
 * gain a Token of Affection.
 */
public class ConstableEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    TCPServer.broadcast("- " + currentPlayer.getName() + " uses Constable.");
    return true;
  }
}
