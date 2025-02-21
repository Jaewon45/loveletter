package com.example.loveletter;

import java.util.ArrayList;
import java.util.List;

/** Represents a player in the Love Letter game. */
public class Player {

  private final String nickname;
  private final List<Card> hand;
  private final List<Card> discardPile;
  private boolean alive;
  private boolean protectedByHandmaid;

  /**
   * Constructs a new Player with the given nickname.
   *
   * @param nickname the player's nickname
   */
  public Player(String nickname) {
    this.nickname = nickname;
    this.hand = new ArrayList<>();
    this.discardPile = new ArrayList<>();
    this.alive = true; // By default, a new player starts alive
    this.protectedByHandmaid = false;
  }

  /**
   * Adds a drawn Card to this player's hand.
   *
   * @param drawnCard the card drawn from the deck
   */
  public void addCard(Card drawnCard) {
    hand.add(drawnCard);
    TCPServer.sendDirect(getName(), drawnCard.toString() + " was added to your hand.");
  }

  /**
   * Returns the player's nickname.
   *
   * @return the nickname of the player
   */
  public String getName() {
    return nickname;
  }

  /**
   * Returns the player's current hand.
   *
   * @return the list of cards in hand
   */
  public List<Card> getHand() {
    return hand;
  }

  /**
   * Checks whether the player is still alive in the current round.
   *
   * @return {@code true} if the player is alive, {@code false} otherwise
   */
  public boolean isAlive() {
    return alive;
  }

  /**
   * Sets the player's alive status.
   *
   * @param alive {@code true} if the player should be marked alive, {@code false} if knocked out
   */
  public void setAlive(boolean alive) {
    this.alive = alive;
  }

  /**
   * Checks if the player is protected by the Handmaid card.
   *
   * @return {@code true} if protected, {@code false} otherwise
   */
  public boolean isProtectedByHandmaid() {
    return protectedByHandmaid;
  }

  /**
   * Sets the player's Handmaid protection status.
   *
   * @param protectedByHandmaid {@code true} to enable protection, {@code false} to disable
   */
  public void setProtectedByHandmaid(boolean protectedByHandmaid) {
    this.protectedByHandmaid = protectedByHandmaid;
  }

  /** Clears the player's Handmaid protection status. */
  public void clearHandmaidProtection() {
    this.protectedByHandmaid = false;
  }

  /** Clears the player's hand. */
  public void clearHand() {
    hand.clear();
  }

  /** Clears the player's discard pile. */
  public void clearDiscardPile() {
    discardPile.clear();
  }

  /**
   * Adds a card to the player's discard pile.
   *
   * @param cardToDiscard the card to add to the discard pile
   */
  public void addToDiscardPile(Card cardToDiscard) {
    discardPile.add(cardToDiscard);
    TCPServer.broadcast("Player " + getName() + " discards: " + discardPile.toString() + ".", null);
  }

  /**
   * Returns the player's discard pile.
   *
   * @return the list of cards in the discard pile
   */
  public List<Card> getDiscardPile() {
    return discardPile;
  }

  /**
   * Returns the sum of the values of all cards in the discard pile.
   *
   * @return the total value of discarded cards
   */
  public int getDiscardPileSum() {
    int sum = 0;
    for (Card card : discardPile) {
      sum += card.getValue();
    }
    return sum;
  }

  /**
   * Returns a string representation of the player.
   *
   * @return the player's nickname with a marker if knocked out
   */
  @Override
  public String toString() {
    return nickname + (alive ? "" : " (knocked out)");
  }

  /** Returns the card with the lowest value in the player's hand. */
  public Card getLowest() {
    if (hand.isEmpty()) {
      return null;
    }
    Card lowest = hand.get(0);
    for (Card card : hand) {
      if (card.getValue() < lowest.getValue()) {
        lowest = card;
      }
    }
    return lowest;
  }

  public void discard(Card cardToDiscard) {
    hand.remove(cardToDiscard);
    addToDiscardPile(cardToDiscard);
  }
}
