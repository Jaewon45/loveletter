package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Represents the effect of the Bishop card. If the named number matches a player's card, the player
 * gains a Token of Affection.
 */
public class BishopEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (targetPlayer == null || guess < 1 || guess > 8) {
      TCPServer.sendDirect(currentPlayer.getName(), "Invalid target or guess value (1-8).");
      return false;
    }

    TCPServer.broadcast(
        "- " + currentPlayer.getName() + " uses Bishop targeting " + targetPlayer.getName() + ".");

    if (targetPlayer.getHand().get(0).getValue() == guess) {
      TCPServer.broadcast(
          "- The guess was correct! " + currentPlayer.getName() + " gains a Token of Affection.");
      game.awardToken(currentPlayer, true);
      targetPlayer.discard(targetPlayer.getHand().get(0));
      game.drawCardFor(targetPlayer);
    } else {
      TCPServer.broadcast("The guess was incorrect.");
    }
    return true;
  }
}
