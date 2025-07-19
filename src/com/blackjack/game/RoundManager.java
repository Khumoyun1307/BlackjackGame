// RoundManager.java
package com.blackjack.game;

import com.blackjack.model.*;
import com.blackjack.stats.GameStats;
import com.blackjack.stats.RoundSummary;

import java.util.ArrayList;
import java.util.List;

public class RoundManager {
    public static final double MIN_BET = 10;
    public static final double MAX_BET = 1000;

    private enum State {
        AWAIT_BET,
        AWAIT_INSURANCE,
        PLAYER_TURN,
        DEALER_TURN,
        ROUND_END
    }

    private State state = State.AWAIT_BET;
    private final Player player;
    private final Dealer dealer;
    private final Shoe shoe;
    private final GameRules rules;
    private final GameStats stats;
    private final RoundEventListener listener;

    private double currentBet;
    private int currentHandIndex;
    private List<Hand> hands;

    public RoundManager(Player player, Dealer dealer, Shoe shoe,
                        GameRules rules, GameStats stats,
                        RoundEventListener listener) {
        this.player = player;
        this.dealer = dealer;
        this.shoe = shoe;
        this.rules = rules;
        this.stats = stats;
        this.listener = listener;
        listener.onShoeShuffled(shoe.getTotalDecks());
    }

    /** Kick off a new round (called by controller). */
    public void startRound() {
        // reshuffle if needed
        if (shoe.needsReshuffle()) {
            shoe.reshuffle();
            listener.onLog("Shoe reshuffled.");
            listener.onShoeShuffled(shoe.getTotalDecks());
        }

        // prepare new round
        player.resetHands();
        dealer.resetHands();
        listener.onLog("=== New Round ===");
        listener.onBetRequested(MIN_BET, MAX_BET);
        state = State.AWAIT_BET;
    }

    /** Called by UI when the player places a bet (or cancel with <0). */
    public void placeBet(double bet) {
        if (state != State.AWAIT_BET) return;
        if (bet < 0) {
            listener.onLog("Player exited round.");
            endRound();
            return;
        }
        this.currentBet = bet;
        Hand ph = new Hand();
        ph.setBet(bet);
        player.addHand(ph);
        player.placeBet(ph, bet);
        listener.onBetPlaced(bet);

        // deal four cards
        Card p1 = shoe.drawCard();
        Card dHole = shoe.drawCard();
        Card p2 = shoe.drawCard();
        Card dUp = shoe.drawCard();

        ph.addCard(p1);
        ph.addCard(p2);
        dealer.addHand(new Hand(dHole, dUp));

        listener.onInitialDeal(List.of(p1, p2), dUp);

        // insurance if dealer up-card is Ace and no player blackjack
        if (dUp.getRank() == Card.Rank.ACE && !rules.isBlackjack(ph)) {
            listener.onOfferInsurance(bet/2);
            state = State.AWAIT_INSURANCE;
        } else {
            listener.onInsurancePlaced(0);
            afterInsurance();
        }
    }

    /** Called by UI when insurance amount is entered. */
    public void placeInsurance(double amount) {
        if (state != State.AWAIT_INSURANCE) return;
        Hand ph = player.getHands().get(0);
        ph.setInsuranceBet(amount);
        player.adjustBalance(-amount);
        listener.onInsurancePlaced(amount);
        afterInsurance();
    }

    private void afterInsurance() {
        // check natural blackjack
        Hand ph = player.getHands().get(0);
        Hand dh = dealer.getHand();
        boolean pBJ = rules.isBlackjack(ph);
        boolean dBJ = rules.isBlackjack(dh);
        if (pBJ || dBJ) {
            listener.onRevealDealerCard(dh.getCards());
            resolveBlackjack(ph, dh, pBJ, dBJ);
            endRound();
            return;
        }
        // start player turn
        this.hands = new ArrayList<>(player.getHands());
        currentHandIndex = 0;
        state = State.PLAYER_TURN;
        promptPlayerMove();
    }

    private void resolveBlackjack(Hand ph, Hand dh, boolean pBJ, boolean dBJ) {
        if (pBJ && dBJ) {
            listener.onLog("Push on Blackjack.");
            player.adjustBalance(ph.getBet());
        } else if (pBJ) {
            listener.onLog("Player Blackjack! Payout 3:2.");
            player.adjustBalance(rules.getBlackjackPayout(ph));
        } else {
            listener.onLog("Dealer Blackjack. Player loses.");
        }
        // insurance resolution
        double ins = ph.getInsuranceBet();
        if (ins > 0) {
            listener.onLog("Insurance pays 2:1.");
            player.adjustBalance(ins*3);
        }
        listener.onBalanceUpdated(player.getBalance());
    }

    /** Called by UI when player clicks Hit/Stay/etc. */
    public void playerMove(Move move) {
        if (state != State.PLAYER_TURN) return;
        Hand h = hands.get(currentHandIndex);
        switch(move) {
            case HIT:
                Card c = shoe.drawCard();
                h.addCard(c);
                listener.onCardDrawn("You", c, currentHandIndex, h);
                if (rules.isBust(h)) {
                    listener.onLog("Busted!");
                    advanceHand();
                } else {
                    promptPlayerMove();
                }
                break;
            case STAY:
                listener.onLog("Player stays.");
                advanceHand();
                break;
            case DOUBLE:
                if (rules.canDouble(h, player.getBalance())) {
                    player.adjustBalance(-h.getBet());
                    h.setBet(h.getBet()*2);
                    Card c2 = shoe.drawCard();
                    h.addCard(c2);
                    listener.onCardDrawn("You", c2, currentHandIndex, h);
                    listener.onLog("Doubled.");
                }
                advanceHand();
                break;
            case SPLIT:
                if (rules.canSplit(h, player.getBalance())) {
                    splitHand(h);
                    listener.onLog("Hand split.");
                    promptPlayerMove();
                }

                break;
            case SURRENDER:
                if (rules.canSurrender(h, false)) {
                    player.adjustBalance(h.getBet()/2);
                    listener.onLog("Surrendered, half returned.");
                }
                advanceHand();
                break;
            case EXIT:
                listener.onLog("Player exited round.");
                endRound();
                break;
        }
    }

    private void promptPlayerMove() {
        Hand h = hands.get(currentHandIndex);
        listener.onPlayerTurnStart(
                currentHandIndex, h,
                rules.canHit(h),
                rules.canDouble(h, player.getBalance()),
                rules.canSplit(h, player.getBalance()),
                rules.canSurrender(h, false)
        );
    }

    private void advanceHand() {
        currentHandIndex++;
        if (currentHandIndex < hands.size()) {
            promptPlayerMove();
        } else {
            state = State.DEALER_TURN;
            listener.onDealerTurnStart();
            playDealer();
            evaluateAll();
            endRound();
        }
    }

    private void playDealer() {
        Hand dh = dealer.getHand();
        listener.onRevealDealerCard(dh.getCards());
        while (!rules.isBust(dh) && dh.getValue() < 17) {
            Card c = shoe.drawCard();
            dh.addCard(c);
            listener.onDealerCardDrawn(c, dh);
        }
        listener.onLog(rules.isBust(dh)
                ? "Dealer busts!"
                : "Dealer stands at " + dh.getValue());
    }

    private void evaluateAll() {
        Hand dh = dealer.getHand();
        for (int i = 0; i < hands.size(); i++) {
            evaluateHand(hands.get(i), dh, i);
        }
    }

    private void evaluateHand(Hand ph, Hand dh, int idx) {
        int pv = ph.getValue(), dv = dh.getValue();
        double bet = ph.getBet(), payout = 0;
        String res;
        if (rules.isBust(ph)) {
            res = "Bust";
        } else if (rules.isBust(dh) || pv > dv) {
            res = "Win"; payout = bet*2; player.adjustBalance(payout);
        } else if (pv == dv) {
            res = "Push"; payout = bet; player.adjustBalance(payout);
        } else {
            res = "Lose";
        }
        RoundSummary sum = new RoundSummary(idx+1, bet, payout, res, pv, dv);
        stats.recordRound(sum);
        listener.onRoundResult(sum);
        listener.onBalanceUpdated(player.getBalance());
    }

    private void splitHand(Hand h) {
        Card c1 = h.getCards().get(0), c2 = h.getCards().get(1);
        double bet = h.getBet();
        player.adjustBalance(-bet);

        Hand h1 = new Hand(c1, shoe.drawCard()); h1.setBet(bet);
        Hand h2 = new Hand(c2, shoe.drawCard()); h2.setBet(bet);

        List<Hand> list = new ArrayList<>(hands);
        list.remove(h);
        list.add(h1);
        list.add(h2);
        this.hands = list;

        // ←—— **NEW**: Push the split hands back into the Player
        player.resetHands();
        for (Hand newHand : hands) {
            player.addHand(newHand);
        }
    }

    private void endRound() {
        state = State.ROUND_END;
        listener.onSessionContinuationRequested();
    }

    public Player getPlayer() {
        return player;
    }
}