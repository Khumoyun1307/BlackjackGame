package test;

import com.blackjack.model.*;
import com.blackjack.game.GameRules;

public class ManualTestSplit {

    public static void main(String[] args) {
        Player player = new Player("Tester", 1000);
        GameRules rules = new GameRules();

        System.out.println("=== Manual Test: Split and Re-split ===");

        // Step 1: Set up the first hand with a pair of 8s
        Hand original = new Hand(new Card(Card.Rank.EIGHT, Card.Suit.HEARTS),
                new Card(Card.Rank.EIGHT, Card.Suit.SPADES));
        original.setBet(100);
        player.addHand(original);
        player.placeBet(original, 100); // initial balance becomes 900

        System.out.println("Initial balance: " + player.getBalance());

        // Step 2: Split the hand manually
        if (original.canSplit(player.getBalance())) {
            player.removeHand(original);

            Card c1 = original.getCards().get(0);
            Card c2 = original.getCards().get(1);

            Hand h1 = new Hand();
            h1.addCard(c1);
            h1.addCard(new Card(Card.Rank.EIGHT, Card.Suit.CLUBS)); // Force another 8 for re-split
            h1.setBet(original.getBet());

            Hand h2 = new Hand();
            h2.addCard(c2);
            h2.addCard(new Card(Card.Rank.TWO, Card.Suit.DIAMONDS));
            h2.setBet(original.getBet());

            player.adjustBalance(-original.getBet()); // balance: 800
            player.addHand(h1);
            player.addHand(h2);

            System.out.println("After first split, hand count: " + player.getHands().size());
            System.out.println("Balance: " + player.getBalance());
        }

        // Step 3: Re-split h1 if possible
        Hand handToResplit = player.getHands().get(0);
        if (handToResplit.canSplit(player.getBalance())) {
            player.removeHand(handToResplit);

            Card c1 = handToResplit.getCards().get(0);
            Card c2 = handToResplit.getCards().get(1);

            Hand h1a = new Hand();
            h1a.addCard(c1);
            h1a.addCard(new Card(Card.Rank.THREE, Card.Suit.HEARTS));
            h1a.setBet(handToResplit.getBet());

            Hand h1b = new Hand();
            h1b.addCard(c2);
            h1b.addCard(new Card(Card.Rank.SIX, Card.Suit.HEARTS));
            h1b.setBet(handToResplit.getBet());

            player.adjustBalance(-handToResplit.getBet()); // balance: 700
            player.addHand(0, h1a);
            player.addHand(1, h1b);

            System.out.println("After re-split, hand count: " + player.getHands().size());
            System.out.println("Balance: " + player.getBalance());
        }

        // Final summary
        int i = 1;
        for (Hand hand : player.getHands()) {
            System.out.printf("Hand %d: %s (value: %s, bet: %.2f)\n", i++, hand.getCards(), hand.getValue(), hand.getBet());
        }

        System.out.println("✅ Manual split/re-split logic test completed.");
    }
}
