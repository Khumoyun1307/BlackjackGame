package com.blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HandTest {
    @Test
    void testAddCardAndGetCards() {
        Hand hand = new Hand();
        Card card = new Card(Card.Rank.FIVE, Card.Suit.CLUBS);
        hand.addCard(card);
        assertEquals(1, hand.getCards().size());
        assertEquals(card, hand.getCards().get(0));
    }

    @Test
    void testGetValueWithAces() {
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.SIX, Card.Suit.SPADES));
        assertEquals(17, hand.getValue());
        hand.addCard(new Card(Card.Rank.SEVEN, Card.Suit.CLUBS));
        assertEquals(14, hand.getValue());
    }

    @Test
    void testGetDisplayValueSoftAndHard() {
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.SIX, Card.Suit.SPADES));
        assertTrue(hand.getDisplayValue().contains("/"));
    }

    @Test
    void testIsBlackjack() {
        Hand hand = new Hand(new Card(Card.Rank.ACE, Card.Suit.HEARTS), new Card(Card.Rank.KING, Card.Suit.CLUBS));
        assertTrue(hand.isBlackjack());
    }

    @Test
    void testIsBust() {
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.KING, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.QUEEN, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.TWO, Card.Suit.DIAMONDS));
        assertTrue(hand.isBust());
    }

    @Test
    void testBetAndInsurance() {
        Hand hand = new Hand();
        hand.setBet(50);
        assertEquals(50, hand.getBet());
        hand.setInsuranceBet(10);
        assertEquals(10, hand.getInsuranceBet());
    }

    @Test
    void testSplitLogic() {
        Hand hand = new Hand(new Card(Card.Rank.EIGHT, Card.Suit.HEARTS), new Card(Card.Rank.EIGHT, Card.Suit.CLUBS));
        hand.setBet(20);
        assertTrue(hand.canSplit(20));
        hand = new Hand(new Card(Card.Rank.EIGHT, Card.Suit.HEARTS), new Card(Card.Rank.NINE, Card.Suit.CLUBS));
        hand.setBet(20);
        assertFalse(hand.canSplit(20));
    }

    @Test
    void testReset() {
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.FIVE, Card.Suit.CLUBS));
        hand.setBet(10);
        hand.setInsuranceBet(5);
        hand.setSplitHand(true);
        hand.reset();
        assertEquals(0, hand.getCards().size());
        assertEquals(0, hand.getBet());
        assertEquals(0, hand.getInsuranceBet());
        assertFalse(hand.isSplitHand());
    }
}
