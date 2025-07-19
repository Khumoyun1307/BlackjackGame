package com.blackjack.game;

import com.blackjack.model.Card;
import com.blackjack.model.Hand;
import com.blackjack.stats.RoundSummary;
import java.util.List;

/**
 * Listener interface for round events, decoupling game logic from UI.
 */
public interface RoundEventListener {

    /**
     * Fired once when the shoe is shuffled with the given number of decks.
     */
    default void onShoeShuffled(int decks) {}

    /**
     * Request the player to place a bet between minBet and maxBet.
     */
    void onBetRequested(double minBet, double maxBet);

    /**
     * After the player places a bet.
     */
    void onBetPlaced(double amount);

    /**
     * Initial deal: the player's cards and the dealer's up-card.
     */
    void onInitialDeal(List<Card> playerCards, Card dealerUpCard);

    /**
     * Offer insurance when dealer shows an Ace (maxInsurance is half the bet).
     */
    void onOfferInsurance(double maxInsurance);

    /**
     * After the player places an insurance bet (zero if declined).
     */
    void onInsurancePlaced(double amount);

    /**
     * Signifies the start of the player's turn on the specified hand.
     */
    void onPlayerTurnStart(int handIndex, Hand hand,
                           boolean canHit, boolean canDouble,
                           boolean canSplit, boolean canSurrender);

    /**
     * A card was drawn by the given actor for the specified hand.
     */
    void onCardDrawn(String actor, Card card, int handIndex, Hand updatedHand);

    /**
     * Fired once when the dealer's turn starts.
     */
    void onDealerTurnStart();

    /**
     * A card was drawn by the dealer.
     */
    void onDealerCardDrawn(Card card, Hand updatedDealerHand);

    /**
     * Fired for each hand when the round ends, with the summary.
     */
    void onRoundResult(RoundSummary summary);

    /**
     * Whenever the player's balance is updated.
     */
    void onBalanceUpdated(double newBalance);

    /**
     * Display a general log or status message.
     */
    default void onLog(String message) {}

    /**
     * Reveal the full dealer hand (e.g., for end-of-round or blackjack check).
     */
    default void onRevealDealerCard(List<Card> dealerCards) {}

    /**
     * Request UI to ask player whether to continue session (new round or exit).
     */
    void onSessionContinuationRequested();
}
