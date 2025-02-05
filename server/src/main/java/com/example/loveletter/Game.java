package com.example.loveletter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Love Letter game.
 *
 * <p>This class manages the deck, players, rounds, and scores, and provides methods to start the
 * game, process turns, apply card effects, and determine the round winner.
 */
public class Game {

  /** The deck used for the current round. */
  private Deck deck;

  /** The list of players participating in the game. */
  private final List<Player> players = new ArrayList<>();

  /** A mapping of player nicknames to their current score (tokens of affection). */
  private final Map<String, Integer> scores = new HashMap<>();

  /** The index of the current player whose turn it is. */
  private int currentPlayerIndex = 0;

  /** The current round number. */
  private int round = 0;

  /** Indicates whether the game has started. */
  private boolean started = false;

  /** Constructs a new Love Letter game. */
  public Game() {}

  /**
   * Adds a player to the game.
   *
   * <p>A player is added only if the game has not yet started and the nickname is unique.
   *
   * @param nickname the nickname of the player to add
   * @return {@code true} if the player was successfully added; {@code false} otherwise
   */
  public boolean addPlayer(String nickname) {
    if (started || getPlayerByNickname(nickname) != null) {
      return false;
    }
    Player newPlayer = new Player(nickname);
    players.add(newPlayer);
    scores.put(nickname, 0);
    return true;
  }

  /**
   * Retrieves a player by their nickname.
   *
   * @param nickname the nickname of the player
   * @return the {@code Player} with the given nickname, or {@code null} if not found
   */
  private Player getPlayerByNickname(String nickname) {
    for (Player p : players) {
      if (p.getName().equals(nickname)) {
        return p;
      }
    }
    return null;
  }

  /**
   * Checks if the game has started.
   *
   * @return {@code true} if the game has started; {@code false} otherwise
   */
  public boolean isStarted() {
    return started;
  }

  /**
   * Returns the total number of players in the game.
   *
   * @return the number of players
   */
  public int getPlayerCount() {
    return players.size();
  }

  /**
   * Starts the game round.
   *
   * <p>This method performs the following:
   *
   * <ul>
   *   <li>Initializes the deck based on the number of players.
   *   <li>Removes the top card of the deck (face-down card).
   *   <li>For a 2-player game, removes three additional cards (face-up cards).
   *   <li>Resets players’ hands, discard piles, and statuses, then deals one card to each player.
   *   <li>Sets the first player (index 0) as the current player.
   * </ul>
   */
  public void start() {
    if (started) {
      return;
    }
    started = true;
    round = 1;
    deck = new Deck(players.size());
    // Remove the top card (face-down card)
    deck.draw();
    // For 2-player game, remove three additional cards (face-up cards)
    if (players.size() == 2) {
      deck.draw();
      deck.draw();
      deck.draw();
    }
    // Reset all players for the new round
    for (Player p : players) {
      p.clearHand();
      p.clearDiscardPile();
      p.setAlive(true);
      p.addCard(deck.draw());
    }
    // Set first player (for simplicity, index 0)
    currentPlayerIndex = 0;
  }

  /**
   * Returns the current player whose turn it is.
   *
   * @return the current {@code Player}, or {@code null} if there are no players
   */
  public Player getCurrentPlayer() {
    if (players.isEmpty()) {
      return null;
    }
    return players.get(currentPlayerIndex);
  }

  /**
   * Advances the game to the next turn.
   *
   * <p>This method does the following:
   *
   * <ul>
   *   <li>If the deck is empty or only one player is alive, the round ends.
   *   <li>Otherwise, it advances to the next alive player.
   *   <li>The current player draws a card (if available) and the Countess rule is checked.
   * </ul>
   *
   * @return {@code true} if the turn was successfully advanced; {@code false} if the round ended
   */
  public boolean nextTurn() {
    if (deck.isEmpty() || getAlivePlayers().size() <= 1) {
      endRound();
      return false;
    }
    // Advance to next alive player.
    do {
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    } while (!players.get(currentPlayerIndex).isAlive());
    // Current player draws a card if available.
    if (!deck.isEmpty()) {
      players.get(currentPlayerIndex).addCard(deck.draw());
      checkCountessRule(players.get(currentPlayerIndex));
    }
    return true;
  }

  /**
   * Processes a card discard by the current player.
   *
   * <p>If the card is the Princess (value 8), the player is immediately eliminated. Otherwise, the
   * card's effect is applied.
   *
   * @param currentPlayer the player discarding the card
   * @param cardToDiscard the card being discarded
   * @param target the target player for the card's effect (may be {@code null})
   * @param guess an integer parameter used by some card effects (-1 if unused)
   */
  public void discardCard(Player currentPlayer, Card cardToDiscard, Player target, int guess) {
    System.out.println(currentPlayer.getName() + " discards " + cardToDiscard.getName());
    currentPlayer.getHand().remove(cardToDiscard);
    // Add to discard pile (assumes the player maintains a discard pile).
    currentPlayer.addToDiscardPile(cardToDiscard);
    if (cardToDiscard.getValue() == 8) {
      eliminatePlayer(currentPlayer);
      return;
    }
    cardToDiscard.getEffect().apply(this, currentPlayer, target, guess);
  }

  /**
   * Eliminates (knocks out) a player from the current round.
   *
   * @param p the player to eliminate
   */
  public void eliminatePlayer(Player p) {
    p.setAlive(false);
    System.out.println(p.getName() + " has been knocked out of the round!");
  }

  /**
   * Checks and applies the Countess rule.
   *
   * <p>If a player holds exactly two cards and has the Countess (value 7) along with either the
   * King (6) or Prince (5), the Countess must be discarded.
   *
   * @param player the player to check for the Countess rule
   */
  public void checkCountessRule(Player player) {
    if (player.getHand().size() == 2) {
      Card c1 = player.getHand().get(0);
      Card c2 = player.getHand().get(1);
      boolean hasCountess = (c1.getValue() == 7) || (c2.getValue() == 7);
      boolean hasRoyal =
          (c1.getValue() == 5 || c1.getValue() == 6) || (c2.getValue() == 5 || c2.getValue() == 6);
      if (hasCountess && hasRoyal) {
        Card countessCard = (c1.getValue() == 7) ? c1 : c2;
        // Discard the Countess without a target or guess (-1 indicates unused).
        discardCard(player, countessCard, null, -1);
      }
    }
  }

  /**
   * Returns a list of players who are still alive in the current round.
   *
   * @return a list of alive players
   */
  private List<Player> getAlivePlayers() {
    List<Player> alive = new ArrayList<>();
    for (Player p : players) {
      if (p.isAlive()) {
        alive.add(p);
      }
    }
    return alive;
  }

  /**
   * Ends the current round.
   *
   * <p>This method performs the following:
   *
   * <ul>
   *   <li>Determines the round winner based on the remaining players and their cards.
   *   <li>Awards a token of affection to the round winner.
   *   <li>Resets player statuses and reinitializes the deck for the next round.
   * </ul>
   */
  private void endRound() {
    List<Player> alive = getAlivePlayers();
    Player roundWinner = null;
    if (alive.size() == 1) {
      roundWinner = alive.get(0);
    } else {
      int highestValue = -1;
      for (Player p : alive) {
        if (!p.getHand().isEmpty()) {
          int value = p.getHand().get(0).getValue();
          if (value > highestValue) {
            highestValue = value;
            roundWinner = p;
          } else if (value == highestValue && roundWinner != null) {
            // Tie-breaker: higher sum of discard pile values wins.
            int currentSum = p.getDiscardPileSum();
            int winnerSum = roundWinner.getDiscardPileSum();
            if (currentSum > winnerSum) {
              roundWinner = p;
            }
          }
        }
      }
    }
    if (roundWinner != null) {
      System.out.println("Round " + round + " winner: " + roundWinner.getName());
      scores.put(roundWinner.getName(), scores.get(roundWinner.getName()) + 1);
    }
    round++;
    // Prepare for a new round: reset player statuses and clear hands/discard piles.
    for (Player p : players) {
      p.setAlive(true);
      p.clearHand();
      p.clearDiscardPile();
    }
    deck.reset();
    // Remove the top card (face-down card)
    deck.draw();
    // For 2-player game, remove three additional cards.
    if (players.size() == 2) {
      deck.draw();
      deck.draw();
      deck.draw();
    }
    // Deal one card to each player.
    for (Player p : players) {
      p.addCard(deck.draw());
    }
    // The round winner starts the next round (or default to index 0 if no winner).
    currentPlayerIndex = (roundWinner != null) ? players.indexOf(roundWinner) : 0;
  }

  /**
   * Returns a string representation of the current scores.
   *
   * @return the scores as a string
   */
  public String getScores() {
    return scores.toString();
  }

  /**
   * Returns the current deck.
   *
   * @return the {@code Deck} object
   */
  public Deck getDeck() {
    return deck;
  }

  /**
   * Attempts to play a card for the specified player.
   *
   * <p>This method performs the following steps:
   *
   * <ul>
   *   <li>Locate the player by nickname and verify that the player is alive.
   *   <li>Search the player's hand for a card matching the given card name (case-insensitive).
   *   <li>If a target is specified (i.e. non-empty), locate the target player by nickname.
   *   <li>Discard the card, which applies its effect.
   *   <li>Advance the game turn by drawing a card for the next player (if applicable).
   * </ul>
   *
   * @param nickname the nickname of the player who is playing the card
   * @param cardName the name of the card to be played
   * @param target the nickname of the target player (may be {@code null} or empty if not required)
   * @param guess an additional parameter used by some card effects (e.g., Guard)
   * @return {@code true} if the card was successfully played; {@code false} otherwise
   */
  boolean playCard(String nickname, String cardName, String target, int guess) {
    // Locate the player by nickname.
    Player player = getPlayerByNickname(nickname);
    if (player == null) {
      System.out.println("Error: Player '" + nickname + "' not found.");
      return false;
    }
    if (!player.isAlive()) {
      System.out.println("Error: Player '" + nickname + "' is not alive.");
      return false;
    }

    // Search for the card in the player's hand (case-insensitive comparison).
    Card cardToPlay = null;
    for (Card card : player.getHand()) {
      if (card.getName().equalsIgnoreCase(cardName)) {
        cardToPlay = card;
        break;
      }
    }
    if (cardToPlay == null) {
      System.out.println("Error: Card '" + cardName + "' not found in " + nickname + "'s hand.");
      return false;
    }

    // If a target is specified, attempt to locate the target player.
    Player targetPlayer = null;
    if (target != null && !target.trim().isEmpty()) {
      targetPlayer = getPlayerByNickname(target);
      if (targetPlayer == null) {
        System.out.println("Error: Target player '" + target + "' not found.");
        return false;
      }
    }

    // Discard the card, which applies its effect.
    discardCard(player, cardToPlay, targetPlayer, guess);

    // Advance the turn. Note: if the round ends (e.g. deck empty or only one player alive),
    // nextTurn() will call endRound(), and turn advancement is not applicable.
    nextTurn();

    return true;
  }
}
