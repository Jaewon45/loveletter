package com.example.loveletter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Represents the deck of cards used in the Love Letter game. */
public final class Deck {

  private final List<Card> cards = new ArrayList<>();
  private final int numberOfPlayers;

  /**
   * Constructs a new deck based on the number of players.
   *
   * <p>For 2 players, 4 cards are removed (1 face-down plus 3 extra for 2-player game). For 3 to 8
   * players, 1 card is removed (face-down card). For more than 4 players, the deck is doubled.
   *
   * @param numberOfPlayers the number of players participating
   */
  public Deck(int numberOfPlayers) {
    this.numberOfPlayers = numberOfPlayers;
    initDeck();
    if (numberOfPlayers > 4) {
      initDeck();
    }
    shuffle();

    int cardsToRemove =
        switch (numberOfPlayers) {
          case 2 -> 4;
          case 3, 4, 5, 6, 7, 8 -> 1;
          default -> throw new AssertionError("Invalid number of players: " + numberOfPlayers);
        };

    for (int i = 0; i < cardsToRemove; i++) {
      cards.remove(cards.size() - 1);
    }
  }

  /** Shuffles the deck. */
  public void shuffle() {
    Collections.shuffle(cards);
  }

  /**
   * Draws the top card from the deck.
   *
   * @return the drawn {@link Card}
   * @throws IllegalStateException if the deck is empty
   */
  public Card draw() {
    if (cards.isEmpty()) {
      throw new IllegalStateException("Deck is empty.");
    }
    return cards.remove(cards.size() - 1);
  }

  /**
   * Checks if the deck is empty.
   *
   * @return {@code true} if no cards remain, {@code false} otherwise
   */
  public boolean isEmpty() {
    return cards.isEmpty();
  }

  /** Initializes the deck by adding one copy of each card. */
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

  /**
   * Resets the deck for a new round.
   *
   * <p>The deck is cleared and reinitialized using the same rules as in the constructor, then
   * shuffled.
   */
  public void reset() {
    cards.clear();
    initDeck();
    if (numberOfPlayers > 4) {
      initDeck();
    }
    shuffle();

    int cardsToRemove =
        switch (numberOfPlayers) {
          case 2 -> 4;
          case 3, 4, 5, 6, 7, 8 -> 1;
          default -> throw new AssertionError("Invalid number of players: " + numberOfPlayers);
        };

    for (int i = 0; i < cardsToRemove; i++) {
      cards.remove(cards.size() - 1);
    }
  }
}
