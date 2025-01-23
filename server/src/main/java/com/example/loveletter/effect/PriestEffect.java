package com.example.loveletter.effect;

import com.example.loveletter.Card;
import com.example.loveletter.Game;
import com.example.loveletter.Player;

public class PriestEffect implements Effect {

    @Override
    public void apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
        if (targetPlayer == null || !targetPlayer.isAlive()) {
            System.out.println("Priest: No valid target or target is out.");
            return;
        }
        if (!targetPlayer.getHand().isEmpty()) {
            Card card = targetPlayer.getHand().get(0);
            System.out.println("Priest: " + currentPlayer.getName()
                    + " sees " + targetPlayer.getName() + "'s card is " + card);
        }
    }
}
