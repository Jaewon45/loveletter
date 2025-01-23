package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

public class HandmaidEffect implements Effect {
    @Override
    public void apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
        currentPlayer.setProtectedByHandmaid(true);
        System.out.println("Handmaid: " + currentPlayer.getName() 
                + " is protected until their next turn.");
    }
}
