package com.example.loveletter;

@SuppressWarnings("unused")
class Game {

    private final Deck deck;
    private final Player[] players;
    private final int currentPlayerIndex = 0;
    private final int round = 0;

    public Game(String[] playerNames) {
        deck = new Deck(playerNames.length);
        players = new Player[playerNames.length];
        for (int i = 0; i < playerNames.length; i++) {
            players[i] = new Player(playerNames[i]);
        }

        // everyone draws a card
        for (Player player : players) {
            player.addCard(deck.draw());
        }

    }
}
