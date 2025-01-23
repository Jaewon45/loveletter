package com.example.loveletter;

import com.example.loveletter.effect.Effect;

public enum Card {
    GUARD(1, "Guard", 5),
    PRIEST(2, "Priest", 2),
    BARON(3, "Baron", 2),
    HANDMAID(4, "Handmaid", 2),
    PRINCE(5, "Prince", 2),
    KING(6, "King", 1),
    COUNTESS(7, "Countess", 1),
    PRINCESS(8, "Princess", 1);

    private final int rank;
    private final String name;
    private final int count;
    private final Effect effect;

    Card(int rank, String name, int count) {
        this.rank = rank;
        this.name = name;
        this.count = count;
        this.effect = effect;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public Effect getEffect() {
        return effect;
    }

    public static Card getCard(int rank) {
        for (Card card : Card.values()) {
            if (card.getRank() == rank) {
                return card;
            }
        }
        return null;
    }
}
