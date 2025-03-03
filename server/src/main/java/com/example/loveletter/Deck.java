package com.example.loveletter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Represents the deck of cards used in the Love Letter game. */
public final class Deck {

  private List<Card> cards = new ArrayList<>();
  private final int numberOfPlayers;
  private Card faceDownCard; // Store the face-down card
  private Card[] faceUpCards = new Card[3]; // For 2-player game

  /**
   * Constructs a new deck based on the number of players.
   *
   * <p>For 2 players, 4 cards are removed (1 face-down plus 3 extra for 2-player game). For 3 to 8
   * players, 1 card is removed (face-down card). For more than 4 players, the deck is doubled.
   *
   * @param numberOfPlayers the number of players participating
   */
  public Deck(int numberOfPlayers) {
    if (numberOfPlayers < 2 || numberOfPlayers > 8) {
      throw new IllegalArgumentException("Invalid number of players: " + numberOfPlayers);
    }

    this.numberOfPlayers = numberOfPlayers;
    initDeck();
    if (numberOfPlayers > 4) {
      initDeckExtra();
    }
    shuffle();

    // Debug: Print entire deck order
    StringBuilder deckOrder = new StringBuilder("\nDEBUG - Full deck order (bottom to top):");
    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);
      deckOrder
          .append("\n")
          .append(i + 1)
          .append(". ")
          .append(card.getValue())
          .append(": ")
          .append(card.getName());
    }
    TCPServer.broadcast(deckOrder.toString());

    // Remove and store face-down card
    faceDownCard = cards.remove(cards.size() - 1);
    TCPServer.broadcast(
        "\nFace-down card removed: " + faceDownCard.getValue() + ": " + faceDownCard.getName());

    if (numberOfPlayers == 2) {
      // For 2 players, remove 3 more cards face-up
      faceUpCards[0] = cards.remove(cards.size() - 1);
      faceUpCards[1] = cards.remove(cards.size() - 1);
      faceUpCards[2] = cards.remove(cards.size() - 1);

      StringBuilder faceUpMessage = new StringBuilder("Face-up removed cards:");
      for (Card card : faceUpCards) {
        faceUpMessage.append("\n- ").append(card.getValue()).append(": ").append(card.getName());
      }
      TCPServer.broadcast(faceUpMessage.toString());
    }
  }

  /** Shuffles the deck. */
  public void shuffle() {
    Collections.shuffle(cards);
  }

  /**
   * Draws the top card from the deck.
   *
   * @throws IllegalStateException if the deck is empty
   */
  public Card draw(Player player) {
    if (cards.isEmpty()) {
      return null;
    }
    Card drawnCard = cards.remove(cards.size() - 1);
    player.addCard(drawnCard);
    return drawnCard;
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

    assert cards.size() == 16;
  }

  /**
   * Resets the deck for a new round.
   *
   * <p>The deck is reinitialized using the same rules as in the constructor
   */
  public void reset() {
    Deck deck = new Deck(numberOfPlayers);
    this.cards = deck.cards;
    this.faceDownCard = deck.faceDownCard;
    this.faceUpCards = deck.faceUpCards;
  }

  /**
   * Gets the face-down card when deck is empty (for Prince effect).
   *
   * @return the face-down card
   */
  public Card drawFaceDownCard() {
    Card card = faceDownCard;
    faceDownCard = null;
    return card;
  }

  private void initDeckExtra() {
    cards.add(Card.ASSASSIN);
    cards.add(Card.ASSASSIN); // Add second Assassin

    cards.add(Card.JESTER);
    cards.add(Card.JESTER); // Add second Jester

    cards.add(Card.CARDINAL);
    cards.add(Card.CARDINAL);

    cards.add(Card.BARONESS);
    cards.add(Card.BARONESS);

    cards.add(Card.SYCOPHANT);
    cards.add(Card.SYCOPHANT);

    cards.add(Card.COUNT);
    cards.add(Card.COUNT);

    cards.add(Card.DOWAGERQUEEN);
    cards.add(Card.DOWAGERQUEEN);

    cards.add(Card.BISHOP);
    cards.add(Card.BISHOP);

    assert cards.size() == 32; // Now should be 32 total (16 original + 16 extra)
  }
}
