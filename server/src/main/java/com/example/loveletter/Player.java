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

}
