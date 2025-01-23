package com.example.loveletter;

@SuppressWarnings("unused")
public
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

    /** "Knock out" (eliminate) a player from the round */
    public void eliminatePlayer(Player p) {
        p.setAlive(false);
        System.out.println(p.getName() + " has been knocked out of the round!");
    }

    /**
     * The main method to discard a card. 
     *  - Applies special checks (Princess).
     *  - Calls the card's effect if appropriate.
     */
    public void discardCard(Player currentPlayer, Card cardToDiscard, Player target, int guess) {
        System.out.println(currentPlayer.getName() + " discards " + cardToDiscard.getName());
        
        // 1) Remove the card from the player's hand
        currentPlayer.getHand().remove(cardToDiscard);
        // 2) If this is the Princess (rank 8), that player is immediately knocked out
        if (cardToDiscard.getRank() == 8) {
            eliminatePlayer(currentPlayer);
            // Skip the effect for the Princess. 
            // If another card forced this discard, the effect's further steps for that target are canceled.
            return;
        }
        // 3) Otherwise, apply the card's effect
        cardToDiscard.getEffect().apply(this, currentPlayer, target, guess);
    }
    
    /**
     * After a player draws a card (resulting in 2 cards in hand),
     * we must check the special Countess rule:
     *  - If they have Countess (7) AND King (6) or Prince (5), they MUST discard Countess.
     */
    public void checkCountessRule(Player player) {
        if (player.getHand().size() == 2) {
            Card c1 = player.getHand().get(0);
            Card c2 = player.getHand().get(1);

            boolean hasCountess = (c1.getRank() == 7) || (c2.getRank() == 7);
            boolean hasRoyal = (c1.getRank() == 5 || c1.getRank() == 6)
                            || (c2.getRank() == 5 || c2.getRank() == 6);

            if (hasCountess && hasRoyal) {
                // Must discard Countess
                Card countessCard = (c1.getRank() == 7) ? c1 : c2;
                discardCard(player, countessCard, null, -1);
            }
        }

    }

    /* Clear all players' handmaid protection at the start/end of each turn if needed. 
    public void clearHandmaidProtection() {
        for (Player p : players) {
            p.clearHandmaidProtection();
        }
    } */

    public Deck getDeck() {
        return deck;
    }
    
    public Player[] getPlayers() {
        return players;
    }
}
