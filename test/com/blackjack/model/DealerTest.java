package com.blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DealerTest {
    @Test
    void testDealerInitialHand() {
        Dealer dealer = new Dealer();
        assertEquals(1, dealer.getHands().size());
        assertNotNull(dealer.getHand());
    }

    @Test
    void testNewRoundResetsHand() {
        Dealer dealer = new Dealer();
        dealer.getHand().addCard(new Card(Card.Rank.FIVE, Card.Suit.CLUBS));
        dealer.newRound();
        assertEquals(0, dealer.getHand().getCards().size());
    }

    @Test
    void testShouldHit() {
        Dealer dealer = new Dealer();
        Hand hand = dealer.getHand();
        hand.addCard(new Card(Card.Rank.FIVE, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.SIX, Card.Suit.HEARTS));
        assertTrue(dealer.shouldHit());
        hand.addCard(new Card(Card.Rank.SIX, Card.Suit.SPADES));
        assertFalse(dealer.shouldHit());
    }

    @Test
    void testAdjustBalanceNoOp() {
        Dealer dealer = new Dealer();
        double before = dealer.getBalance();
        dealer.adjustBalance(-1000);
        assertEquals(before, dealer.getBalance());
    }
}
