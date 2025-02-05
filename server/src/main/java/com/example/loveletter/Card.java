package com.example.loveletter;

import com.example.loveletter.effect.BaronEffect;
import com.example.loveletter.effect.CountessEffect;
import com.example.loveletter.effect.Effect;
import com.example.loveletter.effect.GuardEffect;
import com.example.loveletter.effect.HandmaidEffect;
import com.example.loveletter.effect.KingEffect;
import com.example.loveletter.effect.PriestEffect;
import com.example.loveletter.effect.PrinceEffect;
import com.example.loveletter.effect.PrincessEffect;

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
      "When you discard the Guard, choose a player and name a number (other than 1). If that player"
          + " has that number in their hand, that player is knocked out of the round. If all other"
          + " players still in the round cannot be chosen (eg. due to Handmaid or Sycophant), this"
          + " card is discarded without effect.",
      new GuardEffect()),
  /**
   * Priest card.
   *
   * <p>When discarded, the player may look at another player's hand without revealing it to others.
   */
  PRIEST(
      2,
      "Priest",
      "When you discard the Priest, you can look at another player's hand. Do not reveal the hand"
          + " to any other players.",
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
      "When you discard the Baron, choose another player still in the round. You and that player"
          + " secretly compare your hands. The player with the lower number is knocked out of the"
          + " round. In case of a tie, nothing happens.",
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
      "When you discard the Handmaid, you are immune to the effects of other players' cards until"
          + " the start of your next turn. If all players other than the player whose turn it is"
          + " are protected by the Handmaid, the player must choose him or herself for a card's"
          + " effects, if possible",
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
      "When you discard Prince Arnaud, choose one player still in the round (including yourself)."
          + " That player discards his or her hand (but doesn't apply its effect, unless it is the"
          + " Princess, see page 8) and draws a new one. If the deck is empty and the player cannot"
          + " draw a card, that player draws the card that was removed at the start of the round."
          + " If all other players are protected by the Handmaid, you must choose yourself.",
      new PrinceEffect()),
  /**
   * King card.
   *
   * <p>When discarded, the player trades the card in their hand with that of another player.
   */
  KING(
      6,
      "King",
      "When you discard King Arnaud IV, trade the card in your hand with the card held by another"
          + " player of your choice. You cannot trade with a player who is out of the round",
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
      "like other cards, which take effect when discarded, the text on the Countess applies while"
          + " she is in your hand. In fact, the only time it doesn't apply is when you discard her."
          + " If you ever have the Countess and either the King or Prince in your hand, you must"
          + " discard the Countess. You do not have to reveal the other card in your hand. Of"
          + " course, you can also discard the Countess even if you do not have a royal family"
          + " member in your hand. The Countess likes to play mind games....",
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
      "If you discard the Princess—no matter how or why—she has tossed your letter into the fire."
          + " You are immediately knocked out of the round. If the Princess was discarded by a card"
          + " effect, any remaining effects of that card do not apply (you do not draw a card from"
          + " the Prince, for example). Effects tied to being knocked out the round still apply"
          + " (e.g., Constable, Jester), however.",
      new PrincessEffect());

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
