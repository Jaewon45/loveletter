package com.example.loveletter;

import java.util.Locale;

import com.example.loveletter.effect.AssassinEffect;
import com.example.loveletter.effect.BaronEffect;
import com.example.loveletter.effect.BaronessEffect;
import com.example.loveletter.effect.BishopEffect;
import com.example.loveletter.effect.CardinalEffect;
import com.example.loveletter.effect.ConstableEffect;
import com.example.loveletter.effect.CountEffect;
import com.example.loveletter.effect.CountessEffect;
import com.example.loveletter.effect.DowagerQueenEffect;
import com.example.loveletter.effect.Effect;
import com.example.loveletter.effect.GuardEffect;
import com.example.loveletter.effect.HandmaidEffect;
import com.example.loveletter.effect.JesterEffect;
import com.example.loveletter.effect.KingEffect;
import com.example.loveletter.effect.PriestEffect;
import com.example.loveletter.effect.PrinceEffect;
import com.example.loveletter.effect.PrincessEffect;
import com.example.loveletter.effect.SycophantEffect;

/**
 * Enumeration representing the cards in the Love Letter game.
 *
 * <p>Each card has a value indicating its rank (closer to the Princess), a name, a description of
 * its effect, and an {@link Effect} implementation that is applied when the card is discarded.
 */
public enum Card {
  /**
   * Guard card.
   *
   * <p>When discarded, the player chooses another player and names a number (other than 1). If that
   * player holds a card with the chosen number, they are knocked out of the round.
   */
  GUARD(
      1,
      "Guard",
      "(Value: 1) When you discard the Guard, choose a player and name a number (other than 1). If"
          + " that player has that number in their hand, that player is knocked out of the round."
          + " If all other players still in the round cannot be chosen (eg. due to Handmaid or"
          + " Sycophant), this card is discarded without effect.",
      new GuardEffect()),
  /**
   * Priest card.
   *
   * <p>When discarded, the player may look at another player's hand without revealing it to others.
   */
  PRIEST(
      2,
      "Priest",
      "(Value: 2) When you discard the Priest, you can look at another player's hand privately. Do"
          + " not reveal the hand to any other players.",
      new PriestEffect()),
  /**
   * Baron card.
   *
   * <p>When discarded, the player compares the card in hand with another player; the lower value
   * loses.
   */
  BARON(
      3,
      "Baron",
      "(Value: 3) When you discard the Baron, choose another player still in the round. You and"
          + " that player secretly compare your hands. The player with the lower number is knocked"
          + " out of the round. In case of a tie, nothing happens.",
      new BaronEffect()),
  /**
   * Handmaid card.
   *
   * <p>When discarded, the player is protected from other players' card effects until their next
   * turn.
   */
  HANDMAID(
      4,
      "Handmaid",
      "(Value: 4) When you discard the Handmaid, you are immune to the effects of other players'"
          + " cards until the start of your next turn. If all players other than the player whose"
          + " turn it is are protected by the Handmaid, the player must choose him or herself for a"
          + " card's effects, if possible",
      new HandmaidEffect()),
  /**
   * Prince card.
   *
   * <p>When discarded, the chosen player (or yourself) must discard their hand and draw a new card.
   * If the Princess is discarded, the target is knocked out.
   */
  PRINCE(
      5,
      "Prince",
      "(Value: 5) When you discard Prince Arnaud, choose one player (including yourself) to discard"
          + " their hand and draw a new card. If the Princess is discarded this way, that player is"
          + " eliminated. If the deck is empty, the player draws the face-down card from the start"
          + " of the round. If all other players are protected by the Handmaid, you must choose"
          + " yourself.",
      new PrinceEffect()),
  /**
   * King card.
   *
   * <p>When discarded, the player trades the card in their hand with that of another player.
   */
  KING(
      6,
      "King",
      "(Value: 6) When you discard King Arnaud IV, trade the card in your hand with the card held"
          + " by another player of your choice. You cannot trade with a player who is out of the"
          + " round",
      new KingEffect()),
  /**
   * Countess card.
   *
   * <p>The Countess's effect is only active while in hand. However, if a player holds the Countess
   * together with either the King or Prince, they must discard the Countess.
   */
  COUNTESS(
      7,
      "Countess",
      "(Value: 7) The Countess must be discarded if you have either the King or Prince in your"
          + " hand. This rule applies while she is in your hand, not when she is played. You don't"
          + " need to show the other card when discarding her. You may also choose to discard the"
          + " Countess even without holding the King or Prince.",
      new CountessEffect()),
  /**
   * Princess card.
   *
   * <p>If the Princess is discarded, regardless of reason, the player is immediately knocked out of
   * the round. Any additional effects from the discarding card are canceled.
   */
  PRINCESS(
      8,
      "Princess",
      "(Value: 8) If you discard the Princess—no matter how or why—she has tossed your letter into"
          + " the fire. You are immediately knocked out of the round. If the Princess was discarded"
          + " by a card effect, any remaining effects of that card do not apply (you do not draw a"
          + " card from the Prince, for example). Effects tied to being knocked out the round still"
          + " apply (e.g., Constable, Jester), however.",
      new PrincessEffect()),

  /**
   * Bishop card.
   *
   * <p>When discarded, the player names a number and a player. If the player has that number in
   * their hand, the player gets a Token of Affection. If this would give the player enough Tokens
   * to win the game, then the player wins immediately and the game ends. If the player gained a
   * Token of Affection from this effect, then the player whose card was revealed with the Bishop
   * may discard their card (but doesn't apply its effects, unless it is the Princess) and draw a
   * new one.
   */
  BISHOP(
      9,
      "Bishop",
      "When you discard the Bishop, name a number and a player. If the player has that number in"
          + " their hand, you get a Token of Affection. If this would give you enough Tokens to win"
          + " the game, then you win immediately and the game ends. If you gained a Token of"
          + " Affection from this effect, then the player whose card you effectively revealed with"
          + " the Bishop may discard their card (but doesn't apply its effects, unless it is the"
          + " Princess) and draw a new one.",
      new BishopEffect()),

  /**
   * DowagerQueen card.
   *
   * <p>When discarded, the player compares the card in hand with another player; the higher value
   * loses.
   */
  DOWAGERQUEEN(
      7,
      "DowagerQueen",
      "When you discard the DowagerQueen, choose another player still in the round. You and that"
          + " player secretly compare your hands. The player with the higher number is knocked out"
          + " of the round. In case of a tie, nothing happens.",
      new DowagerQueenEffect()),

  /**
   * Constable card.
   *
   * <p>The Constable's effect applies when the player is knocked out of the round with it in their
   * discard pile.
   */
  CONSTABLE(
      6,
      "Constable",
      "The Constable's effect applies when you are knocked out of the round with it in your discard"
          + " pile. Show the Constable, then claim a Token of Affection. If this would give you"
          + " enough Tokens to win the game, then you win immediately and the game ends.",
      new ConstableEffect()),

  /**
   * Count card.
   *
   * <p>When the round ends, if it is necessary to check the number in the players' hands to
   * determine a winner, the Count will increase that number by 1.
   */
  COUNT(
      5,
      "Count",
      "When the round ends, if it is necessary to check the number in the players' hands to"
          + " determine a winner, the Count will increase that number by 1. Note that this stacks,"
          + " so if you have both copies of the Count in your discard pile, the number will"
          + " increase by 2.",
      new CountEffect()),

  /**
   * Sycophant card.
   *
   * <p>When discarded, the player chooses a player. The next card played must choose the chosen
   * player if it has an effect that chooses one or more players.
   */
  SYCOPHANT(
      4,
      "Sycophant",
      "When you discard the Sycophant, choose a player (including yourself). Then, as long as the"
          + " next card played has an effect that chooses one or more players, it has to at least"
          + " choose the player you chose with the Sycophant.",
      new SycophantEffect()),

  /**
   * Baroness card.
   *
   * <p>When discarded, the player may look at the hands of either 1 or 2 other players without
   * revealing them to others.
   */
  BARONESS(
      3,
      "Baroness",
      "When you discard the Baroness, you can look at the hands of either 1 or 2 other players. Do"
          + " not reveal them to any other players.",
      new BaronessEffect()),

  /**
   * Cardinal card.
   *
   * <p>When discarded, the player chooses exactly 2 players who will switch hands. The player may
   * then look at one of the hands without revealing it to others.
   */
  CARDINAL(
      10,
      "Cardinal",
      "(Value: 10) When you discard the Cardinal, choose exactly 2 players (you may"
          + "include yourself), who will switch hands. Then, once the hands are switched, you may"
          + "look at the first target's hand without revealing it to any other players. If less than 2 players"
          + "still in the round can be chosen, (eg. due to Handmaid or Sycophant), this card is"
          + "discarded without effect.",
      new CardinalEffect()),

  /**
   * Jester card.
   *
   * <p>When discarded, the player chooses another player. If the chosen player wins the round, the
   * player gains a Token of Affection.
   */
  JESTER(
      0,
      "Jester",
      "(Value: 0) When you discard the Jester, choose two players (can include yourself). "
          + "Guess which of them will win this round. If you're right, gain a token.",
      new JesterEffect()),

  /**
   * Assassin card.
   *
   * <p>The Assassin's effect applies while it is in the player's hand. If another player chooses
   * the player when playing a Guard, the Guard's player is eliminated from the round.
   */
  ASSASSIN(
      0,
      "Assassin",
      "If another player chooses you when playing a Guard, then regardless of what number that"
          + " player named (even 0!), when you reveal the Assassin (to all players), the Guard's"
          + " player is eliminated from the round, while you are not. After you've resolved the"
          + " Assassin's effect, you must discard him and draw a new card.",
      new AssassinEffect());

  private final int value;
  private final String name;
  private final String description;
  private final Effect effect;

  /**
   * Constructs a card with the specified attributes.
   *
   * @param value the numerical value of the card (indicating its rank)
   * @param name the name of the card
   * @param description a textual description of the card's effect
   * @param effect the effect associated with discarding this card
   */
  Card(int value, String name, String description, Effect effect) {
    this.value = value;
    this.name = name;
    this.description = description;
    this.effect = effect;
  }

  public static Card getCard(String name) {
    return switch (name.toLowerCase(Locale.ENGLISH)) {
      case "guard" -> GUARD;
      case "priest" -> PRIEST;
      case "baron" -> BARON;
      case "handmaid" -> HANDMAID;
      case "prince" -> PRINCE;
      case "king" -> KING;
      case "countess" -> COUNTESS;
      case "princess" -> PRINCESS;
      case "bishop" -> BISHOP;
      case "dowagerqueen", "dowager" -> DOWAGERQUEEN;
      case "constable" -> CONSTABLE;
      case "count" -> COUNT;
      case "sycophant" -> SYCOPHANT;
      case "baroness" -> BARONESS;
      case "cardinal" -> CARDINAL;
      case "jester" -> JESTER;
      case "assassin" -> ASSASSIN;
      default -> null;
    };
  }

  /**
   * Returns the card's value.
   *
   * @return the numerical value of the card
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns the card's name.
   *
   * @return the name of the card
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the card's description.
   *
   * @return the description of the card's effect
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the effect associated with the card.
   *
   * @return the {@link Effect} implementation for this card
   */
  public Effect getEffect() {
    return effect;
  }
}
