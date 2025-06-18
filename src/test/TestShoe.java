package test;

import com.blackjack.model.Card;
import com.blackjack.model.Shoe;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TestShoe extends Shoe {
    private final Queue<Card> forcedCards;

    public TestShoe(List<Card> presetCards) {
        super(1);

        this.forcedCards = new LinkedList<>(presetCards);
    }

    @Override
    public Card drawCard() {
        if (!forcedCards.isEmpty()) {
            return forcedCards.poll();
        }
        return super.drawCard(); // fallback to real shoe
    }

    @Override
    public boolean needsReshuffle() {
        return false;
    }
}