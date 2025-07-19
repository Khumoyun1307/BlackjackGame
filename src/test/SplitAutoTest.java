package test;

import com.blackjack.game.GameRules;
import com.blackjack.game.RoundEventListener;
import com.blackjack.game.RoundManager;
import com.blackjack.gui.controllers.GameTableController;
import com.blackjack.model.Card;
import com.blackjack.model.Dealer;
import com.blackjack.model.Player;
import com.blackjack.model.Shoe;
import com.blackjack.stats.GameStats;
import com.blackjack.ui.GameUI;

public class SplitAutoTest {
    public static void main(String[] args) {
        System.out.println("=== Split Test via Full Engine ===");

        Player player = new Player("AutoTester", 1000);
        Dealer dealer = new Dealer();
        Shoe shoe = new Shoe(1);
        GameRules rules = new GameRules();
        GameStats stats = new GameStats();

        // Add forced cards to shoe (dealt in reverse order!)
        shoe.prependCard(new Card(Card.Rank.EIGHT, Card.Suit.HEARTS)); // Dealer shown
        shoe.prependCard(new Card(Card.Rank.EIGHT, Card.Suit.CLUBS));  // Player 2nd
        shoe.prependCard(new Card(Card.Rank.EIGHT, Card.Suit.SPADES)); // Dealer hidden
        shoe.prependCard(new Card(Card.Rank.EIGHT, Card.Suit.DIAMONDS)); // Player 1st

        GameUI ui = new TestUI();
        RoundManager manager = new RoundManager(player, dealer, shoe, rules, stats, new GameTableController());

        manager.startRound(); // This will simulate the split
    }
}
