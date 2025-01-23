package com.example.loveletter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Player {

    private final String nickname;
    private List<Card> hand = new ArrayList<>();
    private boolean alive = true;
    private boolean protectedByHandmaid = false;

    public Player(String nickname) {
        this.nickname = nickname;
        this.hand = new ArrayList<>();
        this.alive = true;               // By default, a new player starts alive
        this.protectedByHandmaid = false;
    }

    /**
     * Adds a drawn Card to this player's hand.
     *
     * @param drawnCard The card drawn from the deck.
     */
    public void addCard(Card drawnCard) {
        hand.add(drawnCard);
    }

    public String getName() {
        return nickname;
    }

    public List<Card> getHand() {
        return hand;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isProtectedByHandmaid() {
        return protectedByHandmaid;
    }

    public void setProtectedByHandmaid(boolean protectedByHandmaid) {
        this.protectedByHandmaid = protectedByHandmaid;
    }

    public void clearHandmaidProtection() {
        this.protectedByHandmaid = false;
    }

    @Override
    public String toString() {
        return nickname + (alive ? "" : " (knocked out)");
    }


}
