package com.blackjack.game;

import com.blackjack.model.Card;
import com.blackjack.model.Hand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameRulesTest {
    private final GameRules rules = new GameRules();

    @Test
    void testCanHit() {
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.FIVE, Card.Suit.CLUBS));
        assertTrue(rules.canHit(hand));
        hand.addCard(new Card(Card.Rank.KING, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.NINE, Card.Suit.SPADES));
        assertFalse(rules.canHit(hand));
    }

    @Test
    void testCanDouble() {
        Hand hand = new Hand(new Card(Card.Rank.FIVE, Card.Suit.CLUBS), new Card(Card.Rank.FIVE, Card.Suit.HEARTS));
        hand.setBet(20);
        assertTrue(rules.canDouble(hand, 20));
        assertFalse(rules.canDouble(hand, 10));
        hand.addCard(new Card(Card.Rank.TWO, Card.Suit.DIAMONDS));
        assertFalse(rules.canDouble(hand, 100));
    }

    @Test
    void testCanSplit() {
        Hand hand = new Hand(new Card(Card.Rank.EIGHT, Card.Suit.CLUBS), new Card(Card.Rank.EIGHT, Card.Suit.HEARTS));
        hand.setBet(30);
        assertTrue(rules.canSplit(hand, 30));
        hand = new Hand(new Card(Card.Rank.EIGHT, Card.Suit.CLUBS), new Card(Card.Rank.NINE, Card.Suit.HEARTS));
        hand.setBet(30);
        assertFalse(rules.canSplit(hand, 30));
    }

    @Test
    void testIsBlackjack() {
        Hand hand = new Hand(new Card(Card.Rank.ACE, Card.Suit.CLUBS), new Card(Card.Rank.KING, Card.Suit.HEARTS));
        assertTrue(rules.isBlackjack(hand));
        hand = new Hand(new Card(Card.Rank.NINE, Card.Suit.CLUBS), new Card(Card.Rank.KING, Card.Suit.HEARTS));
        assertFalse(rules.isBlackjack(hand));
    }

    @Test
    void testIsBust() {
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.KING, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.QUEEN, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.TWO, Card.Suit.DIAMONDS));
        assertTrue(rules.isBust(hand));
    }

    @Test
    void testCanSurrender() {
        Hand hand = new Hand(new Card(Card.Rank.FIVE, Card.Suit.CLUBS), new Card(Card.Rank.FIVE, Card.Suit.HEARTS));
        assertTrue(rules.canSurrender(hand, false));
        assertFalse(rules.canSurrender(hand, true));
        hand.addCard(new Card(Card.Rank.TWO, Card.Suit.DIAMONDS));
        assertFalse(rules.canSurrender(hand, false));
    }

    @Test
    void testGetBlackjackPayout() {
        Hand hand = new Hand(new Card(Card.Rank.ACE, Card.Suit.CLUBS), new Card(Card.Rank.KING, Card.Suit.HEARTS));
        hand.setBet(100);
        assertEquals(250, rules.getBlackjackPayout(hand));
    }
}
