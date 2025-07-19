package com.blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @Test
    void testDeckInitialization() {
        Deck deck = new Deck();
        assertEquals(52, deck.getSize());
    }

    @Test
    void testShuffleChangesOrder() {
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        deck2.shuffle();
        boolean different = false;
        for (int i = 0; i < 5; i++) {
            if (!deck1.getCards().get(i).equals(deck2.getCards().get(i))) {
                different = true;
                break;
            }
        }
        assertTrue(different);
    }

    @Test
    void testDrawCardReducesSize() {
        Deck deck = new Deck();
        int initial = deck.getSize();
        Card card = deck.drawCard();
        assertNotNull(card);
        assertEquals(initial - 1, deck.getSize());
    }

    @Test
    void testDrawCardFromEmptyDeckThrows() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) deck.drawCard();
        assertThrows(IllegalStateException.class, deck::drawCard);
    }

    @Test
    void testResetRestoresDeck() {
        Deck deck = new Deck();
        deck.drawCard();
        deck.reset();
        assertEquals(52, deck.getSize());
    }
}
