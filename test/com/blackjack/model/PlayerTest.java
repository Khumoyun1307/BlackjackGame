package com.blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    @Test
    void testPlayerInitialBalance() {
        Player player = new Player("Alice", 100);
        assertEquals(100, player.getBalance());
    }

    @Test
    void testAddAndResetHands() {
        Player player = new Player("Bob", 200);
        Hand hand = new Hand();
        player.addHand(hand);
        assertEquals(1, player.getHands().size());
        player.resetHands();
        assertEquals(0, player.getHands().size());
    }

    @Test
    void testAdjustBalance() {
        Player player = new Player("Charlie", 100);
        player.adjustBalance(50);
        assertEquals(150, player.getBalance());
        player.adjustBalance(-30);
        assertEquals(120, player.getBalance());
    }

    @Test
    void testPlaceBetReducesBalance() {
        Player player = new Player("Dana", 100);
        Hand hand = new Hand();
        player.addHand(hand);
        player.placeBet(hand, 40);
        assertEquals(60, player.getBalance());
        assertEquals(40, hand.getBet());
    }

    @Test
    void testPlaceBetInsufficientFundsThrows() {
        Player player = new Player("Eve", 20);
        Hand hand = new Hand();
        player.addHand(hand);
        assertThrows(IllegalArgumentException.class, () -> player.placeBet(hand, 50));
    }

    @Test
    void testRemoveHand() {
        Player player = new Player("Frank", 100);
        Hand hand = new Hand();
        player.addHand(hand);
        player.removeHand(hand);
        assertEquals(0, player.getHands().size());
    }

    @Test
    void testGetName() {
        Player player = new Player("Grace", 100);
        assertEquals("Grace", player.getName());
    }
}
