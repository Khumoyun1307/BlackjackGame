package com.blackjack.game;

import com.blackjack.model.Dealer;
import com.blackjack.model.Player;
import com.blackjack.stats.GameStats;
import com.blackjack.ui.GameUI;
import com.blackjack.user.PlayerProfile;
import com.blackjack.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BlackjackGameTest {
    private GameUI dummyUI;
    private GameRules rules;
    private PlayerProfile profile;
    private Dealer dealer;
    private GameStats stats;
    private UserManager userManager;

    @BeforeEach
    void setup() {
        dummyUI = new DummyUI();
        rules = new GameRules();
        profile = new PlayerProfile("tester", "pass", 1000);
        dealer = new Dealer();
        stats = new GameStats();
        userManager = new UserManager();
    }

    @Test
    void testGameInitialization() {
        BlackjackGame game = new BlackjackGame(dummyUI, rules, profile, dealer, stats, userManager);
        assertEquals("tester", game.getPlayer().getName());
        assertEquals(1000, game.getPlayer().getBalance());
    }

    @Test
    void testProfileBalanceSyncAfterGame() {
        BlackjackGame game = new BlackjackGame(dummyUI, rules, profile, dealer, stats, userManager);
        game.getPlayer().adjustBalance(-500);
        profile.setBalance(game.getPlayer().getBalance());
        assertEquals(500, profile.getBalance());
    }

    // Dummy UI implementation for testing
    static class DummyUI implements GameUI {
        // ...implement all methods as no-ops or return defaults...
        public void displayBalance(double balance) {}
        public void displayMessageWithoutLn(String message) {}
        public void displayMessage(String message) {}
        public int promptDeckChoice() { return 2; }
        public boolean askYesNo(String prompt) { return false; }
        public double getBet(double min, double max) { return min; }
        public com.blackjack.model.Move getPlayerMove(boolean canDouble, boolean canSplit, boolean canSurrender) { return com.blackjack.model.Move.STAY; }
        public void showPlayerHand(com.blackjack.model.Player player) {}
        public void showDealerHand(com.blackjack.model.Dealer dealer, boolean hideHoleCard) {}
        public void showOutcome(String result) {}
        public double askInsuranceBet(double maxInsurance) { return 0; }
        public void showHand(com.blackjack.model.Hand hand) {}
        public int promptMenuChoice() { return 0; }
        public void showStats(com.blackjack.stats.GameStats stats) {}
        public String prompt(String message) { return ""; }
    }
}
