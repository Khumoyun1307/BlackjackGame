package test;
import com.blackjack.model.Card;
import com.blackjack.model.Hand;

public class HandTestManual {
    public static void main(String[] args) {
        Hand hand = new Hand();

        // Test Ace + Ace
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        System.out.println("Expected: 2/12 → Got: " + hand.getDisplayValue());

        // Test Ace + 5
        hand.reset();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.FIVE, Card.Suit.DIAMONDS));
        System.out.println("Expected: 6/16 → Got: " + hand.getDisplayValue());

        // Test Ace + 5 + 10
        hand.addCard(new Card(Card.Rank.TEN, Card.Suit.SPADES));
        System.out.println("Expected: 16 → Got: " + hand.getDisplayValue());

        // Test Ace + King (Blackjack)
        hand.reset();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.KING, Card.Suit.HEARTS));
        System.out.println("Expected: 21 → Got: " + hand.getDisplayValue());
    }
}