package com.blackjack.ui;

import com.blackjack.model.Dealer;
import com.blackjack.model.Hand;
import com.blackjack.model.Move;
import com.blackjack.model.Player;
import com.blackjack.stats.GameStats;

public interface GameUI {
    void displayBalance(double balance);
    void displayMessageWithoutLn(String message);
    void displayMessage(String message);
    int promptDeckChoice(); // 2 or 6
    boolean askYesNo(String prompt);
    double getBet(double min, double max);
    Move getPlayerMove(boolean canDouble, boolean canSplit, boolean canSurrender); // "hit", "stay", etc.
    void showPlayerHand(Player player);
    void showDealerHand(Dealer dealer, boolean hideHoleCard);
    void showOutcome(String result);
    double askInsuranceBet(double maxInsurance);
    void showHand(Hand hand);
    int promptMenuChoice();
    void showStats(GameStats stats);
    String prompt(String message);
}
