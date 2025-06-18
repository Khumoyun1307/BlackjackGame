package test;

import com.blackjack.model.Card;
import com.blackjack.model.Hand;

public class ManualTestHand {

    public static void main(String[] args) {
        testAceHandling();
        testCanSplit();
        testBust();
    }

    static void testAceHandling() {
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.SIX, Card.Suit.DIAMONDS));

        System.out.println("Test Ace Handling:");
        System.out.println("Expected: 17, Actual: " + hand.getValue());
        assert hand.getValue() == 17;
    }

    static void testCanSplit() {
        Hand hand = new Hand(new Card(Card.Rank.EIGHT, Card.Suit.HEARTS),
                new Card(Card.Rank.EIGHT, Card.Suit.SPADES));
        hand.setBet(100);
        boolean canSplit = hand.canSplit(200);

        System.out.println("\nTest Can Split:");
        System.out.println("Expected: true, Actual: " + canSplit);
        assert canSplit;
    }

    static void testBust() {
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.TEN, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.KING, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.THREE, Card.Suit.SPADES));

        System.out.println("\nTest Bust Logic:");
        System.out.println("Expected: true, Actual: " + hand.isBust());
        assert hand.isBust();
    }
}
