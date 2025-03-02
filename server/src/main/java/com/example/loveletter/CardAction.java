package com.example.loveletter;

public class CardAction {
  private final String playerName;
  private final Card card;
  private final String target;
  private final String secondTarget; // For cards requiring two targets
  private final int guess; // For Guard's guess

  private CardAction(Builder builder) {
    this.playerName = builder.playerName;
    this.card = builder.card;
    this.target = builder.target;
    this.secondTarget = builder.secondTarget;
    this.guess = builder.guess;
  }

  public static class Builder {
    private final String playerName;
    private final Card card;
    private String target;
    private String secondTarget;
    private int guess = -1;

    public Builder(String playerName, Card card) {
      this.playerName = playerName;
      this.card = card;
    }

    public Builder withTarget(String target) {
      this.target = target;
      return this;
    }

    public Builder withSecondTarget(String secondTarget) {
      this.secondTarget = secondTarget;
      return this;
    }

    public Builder withGuess(int guess) {
      this.guess = guess;
      return this;
    }

    public CardAction build() {
      return new CardAction(this);
    }
  }

  // Getters
  public String getPlayerName() {
    return playerName;
  }

  public Card getCard() {
    return card;
  }

  public String getTarget() {
    return target;
  }

  public String getSecondTarget() {
    return secondTarget;
  }

  public int getGuess() {
    return guess;
  }
}
