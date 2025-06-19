package com.blackjack.game;

import com.blackjack.model.*;
import com.blackjack.stats.GameStats;
import com.blackjack.stats.RoundHistoryManager;
import com.blackjack.stats.RoundSummary;
import com.blackjack.ui.GameUI;
import java.util.List;

public class RoundManager {

    private final Player player;
    private final Dealer dealer;
    private final Shoe shoe;
    private final GameRules rules;
    private final GameUI ui;
    private final GameStats stats;

    public RoundManager(Player player, Dealer dealer, Shoe shoe, GameRules rules, GameUI ui, GameStats stats) {
        this.player = player;
        this.dealer = dealer;
        this.shoe = shoe;
        this.rules = rules;
        this.ui = ui;
        this.stats = stats;
    }

    public void playRound(){

        if (shoe.needsReshuffle()) {
            ui.displayMessage("Shoe is low on cards. Reshuffling...");
            shoe.reshuffle();
        }

        double minBet = 10;
        double maxBet = 1000;
        double bet = ui.getBet(minBet, maxBet);
        if (bet == -1) {
            ui.displayMessage("Exiting round...");
            return;
        }


        player.resetHands();
        Hand playerHand = new Hand();
        player.addHand(playerHand);

        try {
            player.placeBet(playerHand, bet);
        } catch (IllegalArgumentException e) {
            ui.displayMessage(e.getMessage());
            return;
        }

        dealInitialCards(playerHand);

        // 1. Offer insurance if Ace is shown
        Card dealerHiddenCard = dealer.getHand().getCards().get(0);

        if (dealerHiddenCard.getRank() == Card.Rank.ACE && !playerHand.isBlackjack() )  {
            double maxInsurance = playerHand.getBet() / 2;
            double insurance = ui.askInsuranceBet(maxInsurance);
            if (insurance > 0) {
                player.adjustBalance(-insurance);
                playerHand.setInsuranceBet(insurance);
                ui.displayMessage(String.format("Insurance bet of %.2f placed.", insurance));
            }
        }

        // 2. Always check for blackjack if dealer shows Ace or 10

        boolean mustCheckForBlackjack =
                dealer.getHand().getCards().get(0).getRank() == Card.Rank.ACE ||
                        dealer.getHand().getCards().get(0).getValue() == 10;
        boolean blackjackResult = checkForBlackjack();
        if (mustCheckForBlackjack) {
            if (!blackjackResult) {
                ui.displayMessage("Dealer does not have Blackjack. Proceed with game.");

                if (playerHand.getInsuranceBet() > 0) {
                    ui.displayMessage("Insurance bet lost.");
                    // Insurance bet already subtracted earlier, no refund
                    playerHand.setInsuranceBet(0); // Clear it to avoid reuse
                }

            } else {
                return; // game ended from dealer blackjack
            }
        }
        if (blackjackResult) return;

        //  player turn; if they exit, stop immediately:
        boolean playerExited = playerTurn();
        if (playerExited) {
            // we could also reset any partial bets or state here if desired
            return;
        }

        boolean allPlayerHandsBusted = player.getHands().stream().allMatch(rules::isBust);

        if (allPlayerHandsBusted) {
            ui.displayMessage("Your hands are busted.");
        } else {
            dealerTurn();
            evaluateResults();
        }

//        dealerTurn();
//        evaluateResults();

        ui.displayBalance(player.getBalance());
    }

    public void dealInitialCards(Hand playerHand){
        Card playerCard1 = shoe.drawCard();
        Card dealerHiddenCard = shoe.drawCard();
        Card playerCard2 = shoe.drawCard();
        Card dealerShownCard = shoe.drawCard();

        playerHand.addCard(playerCard1);
        playerHand.addCard(playerCard2);
        Hand dealerHand = new Hand(dealerHiddenCard, dealerShownCard);

        dealer.resetHands();
        dealer.addHand(dealerHand);

        ui.showPlayerHand(player);
        ui.showDealerHand(dealer, true);

    }

    public boolean playerTurn(){

        List<Hand> playerHands = player.getHands();
        int handIndex = 0;
        boolean hasActed = false;

        while (handIndex < playerHands.size()) {
            Hand currentHand = playerHands.get(handIndex);

            // Printing for clarification

            ui.displayMessage(String.format("\n--- Playing Hand %d of %d ---", handIndex + 1, playerHands.size()));
            ui.displayMessage(String.format("Current Bet: $%.2f", currentHand.getBet()));
            ui.showHand(currentHand);

            boolean turnOver = false;

            while (!turnOver && !rules.isBust(currentHand)) {
                double playerBalance = player.getBalance();
                boolean canDouble = rules.canDouble(currentHand, playerBalance);
                boolean canSplit = rules.canSplit(currentHand, playerBalance);
                boolean canSurrender = rules.canSurrender(currentHand, hasActed);

                // ui.showPlayerHand(player);
                // String move = ui.getPlayerMove(canDouble, canSplit);
                // move = move.toLowerCase();
                //String move = getValidatedMove(canDouble, canSplit);
                Move move = ui.getPlayerMove(canDouble, canSplit, canSurrender);
                if (move == Move.EXIT) {
                    ui.displayMessage("Exiting round early...");
                    return true;
                }

                switch (move) {
                    case HIT:
                        // Card newCard;
                        // newCard = shoe.drawCard();
                        // currentHand.addCard(newCard);
                        // String formattedMessage = String.format("You drawed: %s", newCard.toString());
                        // ui.displayMessage(formattedMessage);
                        // ui.showPlayerHand(player);
                        // if (rules.isBust(currentHand)) {
                        //     ui.displayMessage("You busted!");
                        //     turnOver = true;
                        // }

                        turnOver = drawAndAddToHand(currentHand, "You");
                        hasActed = true;
                        ui.showPlayerHand(player);
                        break;

                    case STAY:
                        turnOver = true;
                        break;

                    case DOUBLE:
                        if (canDouble) {
                            player.adjustBalance(-currentHand.getBet());
                            drawAndAddToHand(currentHand, "You");
                            currentHand.setBet(currentHand.getBet() * 2);
                            ui.displayMessage(String.format("You have doubled! Bet is %f now", currentHand.getBet()));
                            ui.showPlayerHand(player);
                            hasActed = true;
                            //if (rules.isBust(currentHand)) {
                            //    ui.displayMessage("You busted!");
                            //    return ;
                            //}
                            turnOver = true;
                        }else {
                            ui.displayMessage("You can not double");
                        }
                        break;
                    case SPLIT:
                        if (canSplit) {
                            splitHands(currentHand);
                            ui.showPlayerHand(player);

                            // Decrement `i` so that the loop picks up the new first hand next
                            //i--; // So on next loop, `i++` will hit the new hand1
                            handIndex = -1;
                            hasActed = true;
                            turnOver = true;
                        } else {
                            ui.displayMessage("You can not split");
                        }
                        break;
                    case SURRENDER:
                        if (canSurrender) {
                            surrenderHand(currentHand);
                            turnOver = true;
                            hasActed = true;
                        } else {
                            ui.displayMessage("You can't surrender now.");
                        }
                        break;
                    default:
                        ui.displayMessage("Invalid move, try again.");
                        break;
                }
            }
            handIndex++;
        }

        return false;
    }
    public void dealerTurn(){
        Hand dealerHand = dealer.getHand();
        ui.displayMessage("\nDealer's turn...");
        ui.showDealerHand(dealer, false);

        while (!rules.isBust(dealerHand) && dealerHand.getValue() < 17) {
            // Card newCard;
            // newCard = shoe.drawCard();
            // dealerHand.addCard(newCard);
            // ui.displayMessage("Dealer draws: " + newCard);
            // ui.showDealerHand(dealer, false);
            if (drawAndAddToHand(dealerHand, "Dealer")) break;
            ui.showDealerHand(dealer, false);
        }

        if (dealerHand.getValue() > 21) {
            ui.displayMessage("Dealer busts!");
        }else {
            ui.displayMessage("Dealer stands with " + dealerHand.getValue());
        }
    }

    public void splitHands(Hand currentHand) {
        if (!rules.canSplit(currentHand, player.getBalance())) {
            ui.displayMessage("Cannot split this hand.");
            return;
        }
        List<Hand> hands = player.getHands();
        // Remove original hand
        hands.remove(currentHand);
        double originalBet = currentHand.getBet();
        Card c1 = currentHand.getCards().get(0);
        Card c2 = currentHand.getCards().get(1);
        // Create hand1
        Hand hand1 = new Hand();
        hand1.addCard(c1);
        hand1.addCard(shoe.drawCard());
        hand1.setBet(originalBet);
        // Create hand2
        Hand hand2 = new Hand();
        hand2.addCard(c2);
        hand2.addCard(shoe.drawCard());
        hand2.setBet(originalBet);
        // Deduct second bet
        player.adjustBalance(-originalBet);
        // Add new hands
        hands.add(hand1);
        hands.add(hand2);
        ui.displayMessage("Hand split into two hands.");
    }

    public void surrenderHand(Hand currentHand) {
        if (!rules.canSurrender(currentHand,false)) { ui.displayMessage("Cannot surrender now."); return; }
        double refund=currentHand.getBet()/2; player.adjustBalance(refund);
        ui.displayMessage(String.format("You surrendered. Refunded: $%.2f", refund));
    }

    public void evaluateResults() {
        Hand dealerHand = dealer.getHand();
        List<Hand> playerHands = player.getHands();

        for (int i = 0; i < playerHands.size(); i++) {
            evaluateResult(playerHands.get(i), dealerHand, i);
        }
    }

    private void evaluateResult(Hand playerHand, Hand dealerHand, int handIndex) {
        int playerValue = playerHand.getValue();
        int dealerValue = dealerHand.getValue();
        double bet = playerHand.getBet();
        double payout = 0;
        String result;

        if (rules.isBust(playerHand)) {
            result = "Bust";
        } else if (rules.isBust(dealerHand)) {
            result = "Win";
            payout = bet * 2;
            player.adjustBalance(payout);
        } else if (playerValue > dealerValue) {
            result = "Win";
            payout = bet * 2;
            player.adjustBalance(payout);
        } else if (playerValue == dealerValue) {
            result = "Push";
            payout = bet;
            player.adjustBalance(payout);
        } else {
            result = "Lose";
        }


        stats.recordRound(new RoundSummary(
                handIndex + 1, bet, payout, result, playerValue, dealerValue
        ));

        ui.showOutcome("Hand " + (handIndex + 1) + ": " + result +
                " | You: " + playerValue + " | Dealer: " + dealerValue +
                " | Bet: $" + bet + " | Payout: $" + payout);

        String summaryLog = String.format("Hand %d | %s | Bet: $%.2f | Payout: $%.2f | You: %d | Dealer: %d",
                handIndex + 1, result, bet, payout, playerValue, dealerValue);
        RoundHistoryManager.saveRound(player.getName(), summaryLog);
    }

    public boolean checkForBlackjack(){
        List<Hand> hands = player.getHands();
        if (hands.isEmpty()) return false;

        Hand playerHand = hands.get(0);
        Hand dealerHand = dealer.getHand();

        boolean playerBJ = rules.isBlackjack(playerHand);
        boolean dealerBJ = rules.isBlackjack(dealerHand);


        if (playerBJ && dealerBJ) {
            ui.showDealerHand(dealer, false); // reveal both cards
            ui.showOutcome("Push! Both you and the dealer have Blackjack.");
            player.adjustBalance(playerHand.getBet());
            ui.displayBalance(player.getBalance());

            if (playerHand.getInsuranceBet() > 0) {
                player.adjustBalance(playerHand.getInsuranceBet() * 3);
                ui.displayMessage("Insurance bet pays 2:1.");
            }
            return true;
        } else if (playerBJ) {
            ui.showOutcome("Blackjack! You win with a natural 21.");
            player.adjustBalance(rules.getBlackjackPayout(playerHand)); // or 2.5x depending on payout rules
            ui.displayBalance(player.getBalance());
            return true;
        } else if (dealerBJ){
            ui.showDealerHand(dealer, false);
            ui.showOutcome("Dealer has Blackjack. You lose.");
            ui.displayBalance(player.getBalance());

            if (playerHand.getInsuranceBet() > 0) {
                player.adjustBalance(playerHand.getInsuranceBet() * 3);
                ui.displayMessage("Insurance bet pays 2:1.");
            }


            return true;
        }

        return false; // no Blackjack — proceed with game

    }

    public boolean drawAndAddToHand(Hand hand, String actor) {
        Card card = shoe.drawCard();
        hand.addCard(card);
        ui.displayMessage(String.format("%s drew: %s", actor, card.getShortDisplay()));

        // ui.displayMessage(actor + " busted!");
        return rules.isBust(hand);
    }

//    private String getValidatedMove(boolean canDouble, boolean canSplit) {
//        while (true) {
//            String move = ui.getPlayerMove(canDouble, canSplit).toLowerCase();
//
//            switch (move) {
//                case "hit":
//                case "h":
//                case "stay":
//                case "st":
//                    return move;
//                case "double":
//                case "d":
//                    if (canDouble) return move;
//                    ui.displayMessage("Double not allowed right now.");
//                    break;
//                case "split":
//                case "sp":
//                    if (canSplit) return move;
//                    ui.displayMessage("Split not allowed right now.");
//                    break;
//                default:
//                    ui.displayMessage("Invalid move. Please try again.");
//            }
//        }
//    }

}
