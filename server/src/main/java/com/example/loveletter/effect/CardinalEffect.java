package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the effect of the Cardinal card. Allows two players to swap hands and lets the current
 * player peek at one.
 */
public class CardinalEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    // Cardinal needs two targets that aren't protected
    List<Player> validTargets =
        game.getAlivePlayers().stream()
            .filter(p -> !p.isProtectedByHandmaid())
            .collect(Collectors.toList());

    if (validTargets.size() < 2) {
      TCPServer.broadcast(
          "- "
              + currentPlayer.getName()
              + " discards Cardinal with no effect (not enough valid targets).");
      return true;
    }

    if (targetPlayer == null
        || secondTarget == null
        || !targetPlayer.isAlive()
        || !secondTarget.isAlive()
        || targetPlayer.isProtectedByHandmaid()
        || secondTarget.isProtectedByHandmaid()) {
      TCPServer.sendDirect(
          currentPlayer.getName(),
          "Invalid targets: Both players must be in the round and not protected.");
      return false;
    }

    // Swap hands between target players
    List<Card> tempHand = new ArrayList<>(targetPlayer.getHand());
    targetPlayer.setHand(secondTarget.getHand());
    secondTarget.setHand(tempHand);

    // Announce the swap
    TCPServer.broadcast(
        "- "
            + currentPlayer.getName()
            + " uses Cardinal. "
            + targetPlayer.getName()
            + " and "
            + secondTarget.getName()
            + " swap hands.");

    Player viewplayer = targetPlayer;
    if (viewplayer == currentPlayer) {
      viewplayer = secondTarget;
    }
    // Let current player peek at one of the hands (first target's hand)
    game.revealHandToPlayer(currentPlayer, viewplayer);

    return true;
  }

  @Override
  public boolean requiresSecondTarget() {
    return true;
  }
}
