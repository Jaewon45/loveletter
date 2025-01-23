package com.example.loveletter;

public enum Card {
    GUARD(1, "Guard", "When you discard the Guard, choose a player and name a number (other than 1). If that player has that number in their hand, that player is knocked out of the round. If all other players still in the round cannot be chosen (eg. due to Handmaid or Sycophant), this card is discarded without effect."),
    PRIEST(2, "Priest", "When you discard the Priest, you can look at another player's hand. Do not reveal the hand to any other players."),
    BARON(3, "Baron", "When you discard the Baron, choose another player still in the round. You and that player secretly compare your hands. The player with the lower number is knocked out of the round. In case of a tie, nothing happens."),
    HANDMAID(4, "Handmaid", "When you discard the Handmaid, you are immune to the effects of other players' cards until the start of your next turn. If all players other than the player whose turn it is are protected by the Handmaid, the player must choose him or herself for a card's effects, if possible"),
    PRINCE(5, "Prince", "When you discard Prince Arnaud, choose one player still in the round (including yourself). That player discards his or her hand (but doesn't apply its effect, unless it is the Princess, see page 8) and draws a new one. If the deck is empty and the player cannot draw a card, that player draws the card that was removed at the start of the round. If all other players are protected by the Handmaid, you must choose yourself."),
    KING(6, "King", "When you discard King Arnaud IV, trade the card in your hand with the card held by another player of your choice. You cannot trade with a player who is out of the round"),
    COUNTESS(7, "Countess", "like other cards, which take effect when discarded, the text on the Countess applies while she is in your hand. In fact, the only time it doesn't apply is when you discard her. If you ever have the Countess and either the King or Prince in your hand, you must discard the Countess. You do not have to reveal the other card in your hand. Of course, you can also discard the Countess even if you do not have a royal family member in your hand. The Countess likes to play mind games...."),
    PRINCESS(8, "Princess", "If you discard the Princess—no matter how or why—she has tossed your letter into the fire. You are immediately knocked out of the round. If the Princess was discarded by a card effect, any remaining effects of that card do not apply (you do not draw a card from the Prince, for example). Effects tied to being knocked out the round still apply (e.g., Constable, Jester), however.");

    private final int value;
    private final String name;
    private final String effect;

    Card(int value, String name, String effect
    ) {
        this.value = value;
        this.name = name;
        this.effect = effect;
    }

    public Card fromString(String name) {
        for (Card card : Card.values()) {
            if (card.getName().equals(name)) {
                return card;
            }
        }
        return null;
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
