package com.blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    @Test
    void testCardConstructorAndGetters() {
        Card card = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        assertEquals(Card.Rank.ACE, card.getRank());
        assertEquals(Card.Suit.HEARTS, card.getSuit());
    }

    @Test
    void testCardValue() {
        assertEquals(11, new Card(Card.Rank.ACE, Card.Suit.CLUBS).getValue());
        assertEquals(10, new Card(Card.Rank.KING, Card.Suit.SPADES).getValue());
        assertEquals(2, new Card(Card.Rank.TWO, Card.Suit.DIAMONDS).getValue());
    }

    @Test
    void testToString() {
        Card card = new Card(Card.Rank.QUEEN, Card.Suit.DIAMONDS);
        String str = card.toString();
        assertTrue(str.contains("QUEEN"));
        assertTrue(str.contains("DIAMONDS"));
    }

    @Test
    void testShortDisplay() {
        Card card = new Card(Card.Rank.TEN, Card.Suit.SPADES);
        assertEquals("[10♠]", card.getShortDisplay());
    }

    @Test
    void testAsciiDisplay() {
        Card card = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        String[] ascii = card.getAsciiDisplay();
        assertEquals(5, ascii.length);
        assertTrue(ascii[0].contains("┌"));
        assertTrue(ascii[4].contains("┘"));
    }
}
