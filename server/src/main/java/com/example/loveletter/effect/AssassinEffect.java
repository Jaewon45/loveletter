package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Represents the effect of the Assassin card. If targeted by a Guard, the attacking player is
 * eliminated instead.
 */
public class AssassinEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    TCPServer.broadcast("- " + currentPlayer.getName() + " uses Assassin.");
    return true;
  }
}
