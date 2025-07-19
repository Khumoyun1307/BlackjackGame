package com.blackjack.game;

import com.blackjack.model.Card;
import com.blackjack.model.Hand;

import java.util.List;

public class GameRules {

    public static final int MIN_BET = 10;
    public static final int MAX_BET = 1000;

    public boolean canHit(Hand playerHand) {
        return !isBust(playerHand);
    }

    public boolean canStay() {
        return true;
    }

    public boolean canDouble(Hand playerHand, double playerBalance) {
        return ( playerHand.getCards().size() == 2 && playerBalance >= playerHand.getBet() );
    }

    public boolean canSplit(Hand playerHand, double playerBalance) {
        List<Card> cards = playerHand.getCards();
        return ( cards.size() == 2 && cards.get(0).getRank() == cards.get(1).getRank()
            &&  playerBalance >= playerHand.getBet() );
    }

    public boolean isBlackjack(Hand playerHand) {
        return (playerHand.getValue() == 21);
    }

    public boolean isBust(Hand playerHand) {
        return playerHand.getValue() > 21;
    }

    public boolean canSurrender(Hand hand, boolean hasActed) {
        return hand.getCards().size() == 2 && !hasActed;
    }

    public double getBlackjackPayout(Hand playerHand) {
        return ( playerHand.getBet() * 1.5 + playerHand.getBet());
    }

}
