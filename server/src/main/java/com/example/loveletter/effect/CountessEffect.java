package com.example.loveletter.effect;

import com.example.loveletter.Game;
import com.example.loveletter.Player;

/**
 * Countess Wilhelmina (7) basically has no effect on discard;
 *
 * The real rule: if you ever hold the Countess + (King or Prince), you must
 * discard the Countess. That logic is typically enforced outside the 'apply'
 * method (e.g., in Game or the player's draw logic).
 */
public class CountessEffect implements Effect {

    @Override
    public void apply(Game game, Player currentPlayer, Player targetPlayer, int guess) {
        System.out.println("Countess: No immediate effect on discard.");
    }
}
