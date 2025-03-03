package com.example.loveletter.effect;

import java.util.List;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;

/**
 * Represents the effect of the Cardinal card. Allows two players to swap hands and lets the current
 * player peek at one.
 */
public class CardinalEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    // Cardinal needs two targets that aren't protected
    List<Player> validTargets = validTargets(game, currentPlayer);

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

    // Get cards to swap
    Card firstCard = targetPlayer.getHand().get(0);
    Card secondCard = secondTarget.getHand().get(0);

    // If either player is the current player, make sure we don't swap the Cardinal
    if (targetPlayer == currentPlayer && firstCard.equals(Card.CARDINAL)) {
      firstCard = targetPlayer.getHand().get(1);
    }
    if (secondTarget == currentPlayer && secondCard.equals(Card.CARDINAL)) {
      secondCard = secondTarget.getHand().get(1);
    }

    // Perform swap
    targetPlayer.getHand().remove(firstCard);
    secondTarget.getHand().remove(secondCard);
    targetPlayer.addCard(secondCard);
    secondTarget.addCard(firstCard);

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
