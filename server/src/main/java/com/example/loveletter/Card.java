package com.example.loveletter;

public enum Card {
    GUARD(1, "Guard", 5),
    PRIEST(2, "Priest", 2),
    BARON(3, "Baron", 2),
    HANDMAID(4, "Handmaid", 2),
    PRINCE(5, "Prince", 2),
    KING(6, "King", 1),
    COUNTESS(7, "Countess", 1),
    PRINCESS(8, "Princess", 1);

    private final int value;
    private final String name;
    private final int count;

    Card(int value, String name, int count) {
        this.value = value;
        this.name = name;
        this.count = count;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public static Card getCard(int value) {
        for (Card card : Card.values()) {
            if (card.getValue() == value) {
                return card;
            }
        }
        return null;
    }
}
