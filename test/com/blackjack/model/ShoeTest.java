package com.blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShoeTest {
    @Test
    void testShoeInitialization() {
        Shoe shoe = new Shoe(2);
        assertEquals(104, shoe.size());
    }

    @Test
    void testShuffleDoesNotChangeSize() {
        Shoe shoe = new Shoe(1);
        int size = shoe.size();
        shoe.shuffle();
        assertEquals(size, shoe.size());
    }

    @Test
    void testDrawCardReducesSize() {
        Shoe shoe = new Shoe(1);
        int before = shoe.size();
        Card card = shoe.drawCard();
        assertNotNull(card);
        assertEquals(before - 1, shoe.size());
    }

    @Test
    void testDrawCardFromEmptyThrows() {
        Shoe shoe = new Shoe(1);
        for (int i = 0; i < 52; i++) shoe.drawCard();
        assertThrows(IllegalStateException.class, shoe::drawCard);
    }

    @Test
    void testNeedsReshuffle() {
        Shoe shoe = new Shoe(1);
        while (shoe.size() > 13) shoe.drawCard(); // 52 * 0.25 = 13
        assertTrue(shoe.needsReshuffle());
    }

    @Test
    void testReshuffleRestoresCards() {
        Shoe shoe = new Shoe(1);
        shoe.drawCard();
        shoe.reshuffle();
        assertEquals(52, shoe.size());
    }

    @Test
    void testPrependCard() {
        Shoe shoe = new Shoe(1);
        Card card = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        shoe.prependCard(card);
        assertEquals(card, shoe.getCards().get(0));
    }
}
