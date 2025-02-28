package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;
import com.example.loveletter.TCPServer;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the effect of the Cardinal card. Allows two players to swap hands and lets the current
 * player peek at one.
 */
public class CardinalEffect implements Effect {

  @Override
  public boolean apply(
      Game game, Player currentPlayer, Player targetPlayer, int guess, Player secondTarget) {
    if (targetPlayer == null) {
      return false;
    }

    if (guess != 1 && guess != 2) {
      TCPServer.sendDirect(
          currentPlayer.getName(),
          "you need to specify which players hand you want to view, 1 or 2 as the 3rd argument");
      return false;
    }

    List<Card> tempHand = new ArrayList<>(targetPlayer.getHand());
    targetPlayer.setHand(secondTarget.getHand());
    secondTarget.setHand(tempHand);

    Player second = guess == 1 ? targetPlayer : secondTarget;
    game.revealHandToPlayer(currentPlayer, second);
    return true;
  }
}
