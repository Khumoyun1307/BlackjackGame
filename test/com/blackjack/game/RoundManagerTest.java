package com.blackjack.game;

import com.blackjack.model.*;
import com.blackjack.stats.GameStats;
import com.blackjack.ui.GameUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoundManagerTest {
    private Player player;
    private Dealer dealer;
    private Shoe shoe;
    private GameRules rules;
    private GameStats stats;
    private DummyUI ui;
    private RoundManager manager;

    @BeforeEach
    void setup() {
        player = new Player("Tester", 1000);
        dealer = new Dealer();
        shoe = new Shoe(1);
        rules = new GameRules();
        stats = new GameStats();
        ui = new DummyUI();
        manager = new RoundManager(player, dealer, shoe, rules, ui, stats);
    }

    @Test
    void testDealInitialCardsGivesTwoCardsEach() {
        Hand playerHand = new Hand();
        manager.dealInitialCards(playerHand);
        assertEquals(2, playerHand.getCards().size());
        assertEquals(2, dealer.getHand().getCards().size());
    }

    @Test
    void testPlayerTurnExitsOnExitMove() {
        ui.nextMove = Move.EXIT;
        player.addHand(new Hand(new Card(Card.Rank.FIVE, Card.Suit.CLUBS), new Card(Card.Rank.SIX, Card.Suit.HEARTS)));
        assertTrue(manager.playerTurn());
    }

    @Test
    void testDealerTurnStopsAt17() {
        Hand dealerHand = dealer.getHand();
        dealerHand.addCard(new Card(Card.Rank.TEN, Card.Suit.CLUBS));
        dealerHand.addCard(new Card(Card.Rank.SEVEN, Card.Suit.HEARTS));
        manager.dealerTurn();
        assertTrue(dealerHand.getValue() >= 17);
    }

    @Test
    void testCheckForBlackjackDetectsPlayerBlackjack() {
        Hand playerHand = new Hand(new Card(Card.Rank.ACE, Card.Suit.CLUBS), new Card(Card.Rank.KING, Card.Suit.HEARTS));
        player.addHand(playerHand);
        Hand dealerHand = new Hand(new Card(Card.Rank.FIVE, Card.Suit.CLUBS), new Card(Card.Rank.SIX, Card.Suit.HEARTS));
        dealer.resetHands();
        dealer.addHand(dealerHand);
        assertTrue(manager.checkForBlackjack());
    }

    // Dummy UI for testing
    static class DummyUI implements GameUI {
        Move nextMove = Move.STAY;
        public void displayBalance(double balance) {}
        public void displayMessageWithoutLn(String message) {}
        public void displayMessage(String message) {}
        public int promptDeckChoice() { return 1; }
        public boolean askYesNo(String prompt) { return false; }
        public double getBet(double min, double max) { return min; }
        public Move getPlayerMove(boolean canDouble, boolean canSplit, boolean canSurrender) { return nextMove; }
        public void showPlayerHand(Player player) {}
        public void showDealerHand(Dealer dealer, boolean hideHoleCard) {}
        public void showOutcome(String result) {}
        public double askInsuranceBet(double maxInsurance) { return 0; }
        public void showHand(Hand hand) {}
        public int promptMenuChoice() { return 0; }
        public void showStats(com.blackjack.stats.GameStats stats) {}
        public String prompt(String message) { return ""; }
    }
}
