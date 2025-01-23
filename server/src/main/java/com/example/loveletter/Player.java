package com.example.loveletter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class Player {

    private final String nickname;
    private final List<Card> hand = new ArrayList<>();
    private final boolean alive = true;

    Player(String playerName) {
        this.nickname = playerName;
    }

    void addCard(Card draw) {
        hand.add(draw);
    }

    List<Card> getHand() {
        return hand;
    }

    String getCardsString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : hand) {
            sb.append(card).append(" ");
        }
        return sb.toString();
    }

}
