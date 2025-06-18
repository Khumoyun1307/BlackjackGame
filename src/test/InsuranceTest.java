package test;

import com.blackjack.game.GameRules;
import com.blackjack.game.RoundManager;
import com.blackjack.model.*;
import com.blackjack.stats.GameStats;
import com.blackjack.ui.ConsoleUI;
import com.blackjack.ui.GameUI;

public class InsuranceTest {

    public static void main(String[] args) {

        // Set up game state
        Player player = new Player("TestPlayer", 1000);
        Dealer dealer = new Dealer();
        Shoe shoe = new Shoe(1); // Not used here
        GameRules rules = new GameRules();
        GameUI dummyUI = new DummyUI();
        GameStats stats = new GameStats();
        RoundManager roundManager = new RoundManager(player, dealer, shoe, rules, dummyUI, stats);

        // Set up player hand
        Hand playerHand = new Hand(new Card(Card.Rank.TEN, Card.Suit.SPADES),
                new Card(Card.Rank.ACE, Card.Suit.HEARTS)); // Blackjack
        playerHand.setBet(100);
        playerHand.setInsuranceBet(50);
        player.addHand(playerHand);

        // Set up dealer hand with blackjack
        Hand dealerHand = new Hand(new Card(Card.Rank.TEN, Card.Suit.CLUBS),
                new Card(Card.Rank.ACE, Card.Suit.DIAMONDS)); // Blackjack
        dealer.resetHands();
        dealer.addHand(dealerHand);

        // Call method
        boolean blackjackDetected = roundManager.checkForBlackjack();

        System.out.println("Insurance Bet: " + playerHand.getInsuranceBet());
        System.out.println("Player Balance After: " + player.getBalance());
        System.out.println("Blackjack detected: " + blackjackDetected);
    }

    // Dummy UI to suppress console input/output
    static class DummyUI extends ConsoleUI implements GameUI {
        public void displayMessage(String message) {}
        public void displayMessageWithoutLn(String message) {}
        public int promptDeckChoice() { return 1; }
        public boolean askYesNo(String prompt) { return false; }
        public double getBet(double min, double max) { return min; }
        public Move getPlayerMove(boolean canDouble, boolean canSplit, boolean canSurrender) { return Move.STAY; }
        public void showPlayerHand(Player player) {}
        public void showDealerHand(Dealer dealer, boolean hideHoleCard) {}
        public void showOutcome(String result) {}
        public double askInsuranceBet(double maxInsurance) { return 0; }
        public void showHand(Hand hand) {}
        public void showStats(GameStats stats){}
    }
}
