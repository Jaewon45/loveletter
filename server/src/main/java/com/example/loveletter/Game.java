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

  /** The player that must be targeted by the next effect (for Sycophant). */
  private Player forcedTarget = null;

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
    /*
    if ("j1".equals(nickname)) {
      scores.put(nickname, 3);
    }
    */
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
   *   <li>Resets players' hands, discard piles, and statuses, then deals one card to each player.
   *   <li>Sets the first player (index 0) as the current player.
   * </ul>
   */
  public void start() {
    if (started) {
      return;
    }
    started = true;
    round = 1;

    // Send game rules before starting
    TCPServer.broadcast(
        """
  \ud83d\udcdc Game Overview: Each player starts with one card. On your turn, draw a card and discard one, applying its effect. Effects may eliminate players or provide advantages. A round ends when all but one player is eliminated or the deck is empty. The player with the highest card wins the round and earns a Token of Affection. The first player to collect the required number of tokens wins the game.\
""");

    // Send gameplay commands right after rules
    TCPServer.broadcast("\nGameplay commands:");
    TCPServer.broadcast(
        "- /play <card> [target] [guess] : Play a card (guess must be a number indicating the"
            + " values of the card you are guessing)");
    TCPServer.broadcast("- /hand : View your current hand");
    TCPServer.broadcast("- /score : View current scores");
    TCPServer.broadcast("- /explain <card> : Get card explanation");
    TCPServer.broadcast("- /values : View all card values");
    TCPServer.broadcast("- /discarded <player> : Show a player's discarded cards\n");

    // Show current game status
    showGameStatus("Game Setup");

    // Initialize game and deal cards
    deck = new Deck(players.size());
    for (Player p : players) {
      p.clearHand();
      p.clearDiscardPile();
      p.setAlive(true);
      Card dealtCard = deck.draw(p);
      TCPServer.sendDirect(p.getName(), "\n🃏 " + dealtCard.getName() + " was added to your hand.");
    }

    // Announce game start and turn order
    StringBuilder turnOrder = new StringBuilder("\n🎮 Game begins! Turn order: ");
    for (int i = 0; i < players.size(); i++) {
      turnOrder.append(players.get(i).getName());
      if (i < players.size() - 1) {
        turnOrder.append(" → ");
      }
    }
    TCPServer.broadcast(turnOrder.toString());

    // Set and announce first player
    currentPlayerIndex = 0;
    Card firstPlayerCard = deck.draw(getCurrentPlayer());
    TCPServer.sendDirect(
        getCurrentPlayer().getName(),
        "🃏 " + firstPlayerCard.getName() + " was added to your hand.");
    TCPServer.broadcast("Current turn: " + getCurrentPlayer().getName());
    TCPServer.sendDirect(
        getCurrentPlayer().getName(), "It's your turn! Type /hand to see your cards.");
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

    // Advance to next alive player
    do {
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    } while (!players.get(currentPlayerIndex).isAlive());

    // Clear Handmaid protection at the start of player's turn
    Player currentPlayer = getCurrentPlayer();
    if (currentPlayer.isProtectedByHandmaid()) {
      currentPlayer.clearHandmaidProtection();
      TCPServer.broadcast("- " + currentPlayer.getName() + "'s Handmaid protection has ended.");
    }

    // Announce turn to all players
    TCPServer.broadcast("\nCurrent turn: " + currentPlayer.getName());

    // Send private message to current player
    TCPServer.sendDirect(currentPlayer.getName(), "It's your turn! Type /hand to see your cards.");

    // Current player draws a card if available
    if (!deck.isEmpty()) {
      Card drawnCard = deck.draw(getCurrentPlayer());
      TCPServer.sendDirect(
          getCurrentPlayer().getName(), "🃏 " + drawnCard.getName() + " was added to your hand.");
      checkCountessRule(getCurrentPlayer());
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
  public boolean discardCard(
      Player currentPlayer, Card cardToDiscard, Player target, int guess, Player secondTarget) {
    // Check if there's a forced target from Sycophant
    Player currentForcedTarget = getForcedTarget();
    if (currentForcedTarget != null
        && (cardToDiscard.getEffect().requiresSecondTarget() || target != null)) {
      // For cards that require targets, either target can be the forced target
      if (target != currentForcedTarget
          && (secondTarget == null || secondTarget != currentForcedTarget)) {
        TCPServer.sendDirect(
            currentPlayer.getName(),
            "Due to Sycophant's effect, you must include "
                + currentForcedTarget.getName()
                + " as a target.");
        return false;
      }
    }

    // Apply the card effect first to check if it is a legal move.
    if (target != null && target.isProtectedByHandmaid()) {
      boolean allProtected = true;
      for (Player p : players) {
        if (!p.isProtectedByHandmaid()) {
          allProtected = false;
          break;
        }
      }
      if (!allProtected) {
        TCPServer.sendDirect(
            currentPlayer.getName(), "You cannot target a player protected by a handmaid!");
        return false;
      } else {
        switch (cardToDiscard) {
          case GUARD, PRIEST, BARON, KING -> {
            TCPServer.broadcast("All players are protected by a handmaid!");
            TCPServer.broadcast(cardToDiscard.getName() + " discared without effect");
            currentPlayer.getHand().remove(cardToDiscard);
            return true;
          }
          case PRINCE -> {
            TCPServer.sendDirect(
                currentPlayer.getName(),
                "All players are protected by a handmaid!, to play the prince you must target"
                    + " yourself");
            return false;
          }
          default -> {}
        }
      }
    }
    boolean legalMove =
        cardToDiscard.getEffect().apply(this, currentPlayer, target, guess, secondTarget);
    if (legalMove) {
      currentPlayer.getHand().remove(cardToDiscard);
      currentPlayer.addToDiscardPile(cardToDiscard);

      // Clear the forced target after a successful card play
      setForcedTarget(null);

      if (cardToDiscard.getValue() == 8) {
        eliminatePlayer(currentPlayer);
      }
    }
    return legalMove;
  }

  /**
   * Eliminates a player from the round.
   *
   * @param player the player to eliminate
   */
  public void eliminatePlayer(Player player) {
    player.setAlive(false);
    TCPServer.broadcast("- " + player.getName() + " is out of the round.");
    TCPServer.sendDirect(player.getName(), "You are out of the round.");

    // Show remaining players and turn order
    List<Player> alivePlayers = getAlivePlayers();
    if (alivePlayers.size() > 1) {
      StringBuilder remainingPlayers = new StringBuilder("\nRemaining players: ");
      for (int i = 0; i < alivePlayers.size(); i++) {
        remainingPlayers.append(alivePlayers.get(i).getName());
        if (i < alivePlayers.size() - 1) {
          remainingPlayers.append(" → ");
        }
      }
      TCPServer.broadcast(remainingPlayers.toString());
    }
  }

  private void endGame() {
    // Find the winner (player with most tokens)
    Player winner = null;
    int maxTokens = -1;

    for (Player player : players) {
      int tokens = scores.get(player.getName());
      if (tokens > maxTokens) {
        maxTokens = tokens;
        winner = player;
      }
    }

    if (winner != null) {
      // Broadcast game end messages
      TCPServer.broadcast("🎉 GAME OVER! 🎉");
      TCPServer.broadcast(
          winner.getName() + " has won the game with " + maxTokens + " tokens of affection!");
      TCPServer.broadcast("Final Scores: " + scores.toString());
    }

    // Reset game state
    started = false;
    currentPlayerIndex = 0;
    round = 0;
    deck = null;

    // Clear player states
    for (Player p : players) {
      p.clearHand();
      p.clearDiscardPile();
      p.setAlive(true);
      p.jesterTarget = null;
    }

    // Clear scores
    scores.clear();

    // Broadcast final message
    TCPServer.broadcast("Type /end to end the game!");
    TCPServer.broadcast("Type /create to start a new game!");
  }

  /**
   * Checks and applies the Countess rule.
   *
   * <p>If a player holds the Countess (value 7) along with either the King (6) or Prince (5), the
   * Countess must be discarded.
   *
   * @param player the player to check for the Countess rule
   */
  public void checkCountessRule(Player player) {
    boolean hasCountess = false;
    boolean hasRoyal = false;
    Card countessCard = null;

    for (Card card : player.getHand()) {
      if (card.getValue() == 7) {
        hasCountess = true;
        countessCard = card;
      } else if (card.getValue() == 5 || card.getValue() == 6) {
        hasRoyal = true;
      }
    }

    if (hasCountess && hasRoyal) {
      // Discard the Countess without a target or guess (-1 indicates unused).
      discardCard(player, countessCard, null, -1, null);
    }
  }

  /** Returns a list of players still alive in the current round. */
  public List<Player> getAlivePlayers() {
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
    List<Player> roundWinners = new ArrayList<>(); // Changed to support multiple winners

    if (alive.size() == 1) {
      roundWinners.add(alive.get(0));
    } else {
      double highestValue = -1;
      // First pass: find highest value
      for (Player p : alive) {
        if (!p.getHand().isEmpty()) {
          Card card = p.getHand().get(0);
          double value = card.getValue();
          if (card == Card.BISHOP) {
            value = 7.5;
          }
          value += p.countIncrease;
          highestValue = Math.max(highestValue, value);
        }
      }

      // Second pass: find all players with highest value
      int highestDiscardSum = -1;
      for (Player p : alive) {
        if (!p.getHand().isEmpty()) {
          Card card = p.getHand().get(0);
          double value = card.getValue();
          if (card == Card.BISHOP) {
            value = 7.5;
          }
          value += p.countIncrease;

          if (value == highestValue) {
            int discardSum = p.getDiscardPileSum();
            if (discardSum > highestDiscardSum) {
              roundWinners.clear();
              roundWinners.add(p);
              highestDiscardSum = discardSum;
            } else if (discardSum == highestDiscardSum) {
              roundWinners.add(p); // Add tied players
            }
          }
        }
      }
    }

    // Award tokens to all winners
    for (Player winner : roundWinners) {
      TCPServer.broadcast("\n👑 Round " + round + " winner: " + winner.getName());
      awardToken(winner, false);

      // Handle Jester targets
      for (Player player : players) {
        if (player.jesterTarget == winner) {
          TCPServer.broadcast(
              "🃏 "
                  + player.getName()
                  + " gains a Token of Affection for correctly choosing "
                  + winner.getName()
                  + " with Jester!");
          awardToken(player, true); // true means check for game end
        }
      }
    }

    // Check for game winners
    int maxTokens = 0;
    List<Player> gameWinners = new ArrayList<>();
    for (Player p : players) {
      int tokens = scores.getOrDefault(p.getName(), 0);
      if (tokens >= tokensNeededToWin()) {
        if (tokens > maxTokens) {
          gameWinners.clear();
          gameWinners.add(p);
          maxTokens = tokens;
        } else if (tokens == maxTokens) {
          gameWinners.add(p);
        }
      }
    }

    if (!gameWinners.isEmpty()) {
      if (gameWinners.size() == 1) {
        endGame();
      } else {
        // Multiple winners - play tiebreaker round
        TCPServer.broadcast("\n🎭 Multiple players have reached winning tokens!");
        StringBuilder tiedPlayers = new StringBuilder("Tied players: ");
        for (int i = 0; i < gameWinners.size(); i++) {
          tiedPlayers.append(gameWinners.get(i).getName());
          if (i < gameWinners.size() - 1) {
            tiedPlayers.append(", ");
          }
        }
        TCPServer.broadcast(tiedPlayers.toString());
        TCPServer.broadcast("A tiebreaker round will be played between these players!");

        // Start tiebreaker round with only tied players
        startTiebreakerRound(gameWinners);
        return;
      }
    }

    // Continue with normal round end...
    startNextRound(roundWinners.isEmpty() ? null : roundWinners.get(0));
  }

  private void startTiebreakerRound(List<Player> tiebreakerPlayers) {
    // Save non-tiebreaker players
    List<Player> savedPlayers = new ArrayList<>(players);

    // Clear and set only tiebreaker players
    players.clear();
    players.addAll(tiebreakerPlayers);

    // Start new round with tiebreaker players
    round++;
    deck = new Deck(players.size());

    // Reset player states for tiebreaker
    for (Player p : players) {
      p.setAlive(true);
      p.clearHand();
      p.clearDiscardPile();
      deck.draw(p);
    }

    // Show tiebreaker status
    showGameStatus("Tiebreaker Round " + round);

    // Restore all players after tiebreaker
    players.clear();
    players.addAll(savedPlayers);
  }

  private void startNextRound(Player roundWinner) {
    round++;

    // Prepare for a new round: reset player statuses and clear hands/discard piles
    for (Player p : players) {
      p.setAlive(true);
      p.clearHand();
      p.clearDiscardPile();
    }
    deck = new Deck(players.size());

    // Deal one card to each player
    for (Player p : players) {
      Card dealtCard = deck.draw(p);
      TCPServer.sendDirect(p.getName(), "\n🃏 " + dealtCard.getName() + " was added to your hand.");
    }

    // Set the round winner as first player (or default to index 0 if no winner)
    currentPlayerIndex = (roundWinner != null) ? players.indexOf(roundWinner) : 0;

    // Show round status with turn order starting from winner
    StringBuilder status =
        new StringBuilder(
            String.format(
                "\n🎮 Starting Round %d (%d players), turn order: ", round, players.size()));
    for (int i = 0; i < players.size(); i++) {
      int index = (currentPlayerIndex + i) % players.size();
      status.append(players.get(index).getName());
      if (i < players.size() - 1) {
        status.append(" → ");
      }
    }
    TCPServer.broadcast(status.toString());

    // Show current scores
    for (Player p : players) {
      TCPServer.broadcast(
          "- " + p.getName() + ": " + scores.getOrDefault(p.getName(), 0) + " tokens");
    }
    TCPServer.broadcast("Tokens needed to win: " + tokensNeededToWin() + "\n");

    // Draw first card for the starting player
    Card firstPlayerCard = deck.draw(getCurrentPlayer());
    TCPServer.sendDirect(
        getCurrentPlayer().getName(),
        "🃏 " + firstPlayerCard.getName() + " was added to your hand.");
    TCPServer.broadcast("Current turn: " + getCurrentPlayer().getName());
  }

  private void showGameStatus(String header) {
    TCPServer.broadcast("\n🎮 " + header + " (" + players.size() + " players):");
    for (Player p : players) {
      TCPServer.broadcast(
          "- " + p.getName() + ": " + scores.getOrDefault(p.getName(), 0) + " tokens");
    }
    TCPServer.broadcast("Tokens needed to win: " + tokensNeededToWin() + "\n");
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
   * @param action the CardAction
   */
  void playCard(CardAction action) {
    Player currentPlayer = getPlayerByNickname(action.getPlayerName());
    if (currentPlayer == null) {
      throw new IllegalArgumentException("Player '" + action.getPlayerName() + "' not found");
    }
    if (!currentPlayer.isAlive()) {
      throw new IllegalArgumentException("Player '" + action.getPlayerName() + "' is not alive");
    }

    // Search for the card in the player's hand
    Card card = action.getCard();
    if (!currentPlayer.getHand().contains(card)) {
      throw new IllegalArgumentException(
          "Card '" + card.getName() + "' not found in player's hand");
    }

    Player targetPlayer = null;
    if (action.getTarget() != null) {
      targetPlayer = getPlayerByNickname(action.getTarget());
      if (targetPlayer == null) {
        throw new IllegalArgumentException("Target player '" + action.getTarget() + "' not found");
      }
    }

    Player secondTarget = null;
    if (action.getSecondTarget() != null) {
      secondTarget = getPlayerByNickname(action.getSecondTarget());
      if (secondTarget == null) {
        throw new IllegalArgumentException(
            "Second target player '" + action.getSecondTarget() + "' not found");
      }
    }

    // Validate the action based on card type
    if (card.getEffect().requiresSecondTarget() && secondTarget == null) {
      throw new IllegalArgumentException("This card requires two targets");
    }

    if (card == Card.GUARD && action.getGuess() == -1) {
      throw new IllegalArgumentException("Guard requires a guess in the form of a number");
    }

    // Check if card needs a target but has none available
    if (targetPlayer == null && !card.getEffect().hasValidTargets(this, currentPlayer)) {
      // Allow discard without effect if no valid targets exist
      TCPServer.broadcast(
          "- "
              + currentPlayer.getName()
              + " discards "
              + card.getName()
              + " with no effect (no valid targets due to Handmaid).");
      currentPlayer.discard(card);
      nextTurn();
      return;
    }

    // Apply the card effect
    boolean success =
        discardCard(currentPlayer, card, targetPlayer, action.getGuess(), secondTarget);
    if (!success) {
      throw new IllegalStateException("Failed to apply card effect");
    }

    // Advance the turn
    nextTurn();
  }

  String getHand(String nickname) {
    Player player = getPlayerByNickname(nickname);
    if (player == null) {
      return "Error: Player '" + nickname + "' not found.";
    }

    List<Card> hand = player.getHand();
    StringBuilder handRepresentation = new StringBuilder("[");

    for (int i = 0; i < hand.size(); i++) {
      Card card = hand.get(i);
      handRepresentation.append(card.getValue()).append(": ").append(card.getName());
      if (i < hand.size() - 1) {
        handRepresentation.append(", ");
      }
    }

    handRepresentation.append("]");
    return handRepresentation.toString();
  }

  /**
   * Sets a forced target for the next action.
   *
   * @param targetPlayer The player that must be targeted by the next effect.
   */
  public void setForcedTarget(Player targetPlayer) {
    this.forcedTarget = targetPlayer;
  }

  /**
   * Returns the forced target player.
   *
   * @return the forced target player, or {@code null} if no forced target is set
   */
  public Player getForcedTarget() {
    return forcedTarget;
  }

  /**
   * Reveals the target player's hand only to the current player.
   *
   * @param currentPlayer The player who played the effect.
   * @param targetPlayer The player whose hand will be revealed.
   */
  public void revealHandToPlayer(Player currentPlayer, Player targetPlayer) {
    TCPServer.sendDirect(
        currentPlayer.getName(),
        ("- " + targetPlayer.getName() + "'s hand: " + targetPlayer.getHand().toString()));
  }

  /**
   * Awards a Token of Affection to the specified player. Also handles Jester effects if this player
   * was targeted by a Jester.
   *
   * @param winner The player receiving the token.
   */
  public void awardToken(Player winner, boolean endGameIfEnough) {
    // Award normal token for winning
    scores.put(winner.getName(), scores.getOrDefault(winner.getName(), 0) + 1);

    // Check for Jester predictions
    for (Player player : players) {
      if (player.getJesterTarget() == winner) {
        TCPServer.broadcast(
            "- " + player.getName() + " gains a token from their Jester prediction!");
        scores.put(player.getName(), scores.getOrDefault(player.getName(), 0) + 1);
      }
    }

    if (endGameIfEnough && scores.get(winner.getName()) >= tokensNeededToWin()) {
      endGame();
    }
  }

  /**
   * Draws a new card for the target player if the deck is not empty.
   *
   * @param targetPlayer The player drawing the card.
   */
  public void drawCardFor(Player targetPlayer) {
    if (!deck.isEmpty()) {
      deck.draw(targetPlayer);
    }
  }

  /** Returns the number of tokens needed to win based on player count. */
  private int tokensNeededToWin() {
    switch (players.size()) {
      case 2 -> {
        return 7;
      }
      case 3 -> {
        return 5;
      }
      case 4, 5, 6, 7, 8 -> {
        return 4;
      }
      default -> throw new IllegalStateException("Unexpected number of players: " + players.size());
    }
  }

  /**
   * Checks if a player with the given nickname is in the game.
   *
   * @param nickname the nickname to check
   * @return true if the player is in the game, false otherwise
   */
  public boolean hasPlayer(String nickname) {
    return players.stream().anyMatch(p -> p.getName().equals(nickname));
  }

  /**
   * Gets a player's discarded cards as a string.
   *
   * @param nickname the player's nickname
   * @return the discarded cards string, or null if player not found
   */
  public String getPlayerDiscardPile(String nickname) {
    Player player = getPlayerByNickname(nickname);
    if (player != null) {
      return player.getDiscardPile().toString();
    }
    return null;
  }
}
