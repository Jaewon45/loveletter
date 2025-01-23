package com.example.loveletter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Deck {

    private final List<Card> cards = new ArrayList<>();

    public Deck(int numberOfPlayers) {
        initDeck();
        if (numberOfPlayers > 4) {
            initDeck();
        }

        shuffle();

        int cardsToRemove = switch (numberOfPlayers) {
            case 2 ->
                4;
            case 3, 4, 5, 6, 7, 8 ->
                1;
            default ->
                throw new AssertionError("Invalid number of players: " + numberOfPlayers);
        };

        for (int i = 0; i < cardsToRemove; i++) {
            cards.remove(cards.size() - 1);
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card draw() {
        return cards.remove(cards.size() - 1);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    private void initDeck() {
        cards.add(Card.PRINCESS);
        cards.add(Card.COUNTESS);
        cards.add(Card.KING);

        cards.add(Card.PRINCE);
        cards.add(Card.PRINCE);

        cards.add(Card.HANDMAID);
        cards.add(Card.HANDMAID);

        cards.add(Card.BARON);
        cards.add(Card.BARON);

        cards.add(Card.PRIEST);
        cards.add(Card.PRIEST);

        cards.add(Card.GUARD);
        cards.add(Card.GUARD);
        cards.add(Card.GUARD);
        cards.add(Card.GUARD);
        cards.add(Card.GUARD);
    }
}
