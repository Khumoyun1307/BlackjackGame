package com.blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shoe {

    private final List<Card> cards;
    private final int totalDecks;;
    private final double reshuffleThreshhold = 0.25;

    public Shoe(int numberOfDecks) {
        this.totalDecks = numberOfDecks;
        this.cards = new ArrayList<>();
        initializeShoe();
    }

    private void initializeShoe() {
        cards.clear();
        for (int i = 0; i < totalDecks; i++) {
            Deck deck = new Deck();
            cards.addAll(deck.getCards());
        }
        shuffle();
    }

    public void shuffle(){
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (isEmpty()) {
            throw new IllegalStateException("Shoe is out of cards! ");
        }
        return cards.remove(0);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    public boolean needsReshuffle() {
        return size() <= (52 * totalDecks * reshuffleThreshhold);
    }

    public void reshuffle(){
        initializeShoe();
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public void prependCard(Card card) {
        cards.add(0, card); // Add to top of the deck
    }

    public int getTotalDecks() {
        return totalDecks;
    }
}

