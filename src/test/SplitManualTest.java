package test;

import com.blackjack.model.*;

public class SplitManualTest {

    public static void main(String[] args) {
        System.out.println("=== Split Functionality Manual Test ===");

        // Setup
        Player player = new Player("TestPlayer", 1000);
        Shoe shoe = new Shoe(1); // One deck is enough
        double initialBalance = player.getBalance();

        // Force a pair of 8s
        Card card1 = new Card(Card.Rank.EIGHT, Card.Suit.HEARTS);
        Card card2 = new Card(Card.Rank.EIGHT, Card.Suit.SPADES);
        Hand originalHand = new Hand(card1, card2);
        double betAmount = 100;
        originalHand.setBet(betAmount);

        player.addHand(originalHand);
        player.adjustBalance(-betAmount); // simulate placing the bet

        System.out.println("Player starting hand: " + originalHand.getCards());
        System.out.println("Can split? " + originalHand.canSplit(player.getBalance()));

        if (!originalHand.canSplit(player.getBalance())) {
            System.out.println("❌ Test failed: Player should be able to split.");
            return;
        }

        // Perform split logic manually
        player.removeHand(originalHand);
        System.out.println("Hands after removal: " + player.getHands().size());

        Hand hand1 = new Hand();
        hand1.addCard(card1);
        hand1.addCard(shoe.drawCard());
        hand1.setBet(betAmount);

        Hand hand2 = new Hand();
        hand2.addCard(card2);
        hand2.addCard(shoe.drawCard());
        hand2.setBet(betAmount);

        player.adjustBalance(-betAmount); // Pay for second hand
        player.addHand(hand1);
        player.addHand(hand2);

        // Show results
        System.out.println("\n✅ Split performed.");
        System.out.println("Player now has " + player.getHands().size() + " hands.");
        for (int i = 0; i < player.getHands().size(); i++) {
            Hand h = player.getHands().get(i);
            System.out.printf("Hand %d: %s (Value: %s)\n", i + 1, h.getCards(), h.getDisplayValue());
        }

        double expectedBalance = initialBalance - 2 * betAmount;
        System.out.printf("Expected balance: %.2f, Actual balance: %.2f\n", expectedBalance, player.getBalance());

        if (player.getHands().size() == 2 &&
                player.getBalance() == expectedBalance) {
            System.out.println("✅ Split test passed.");
        } else {
            System.out.println("❌ Split test failed.");
        }
    }
}
