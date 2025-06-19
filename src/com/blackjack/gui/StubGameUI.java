package com.blackjack.gui;

import com.blackjack.model.*;
import com.blackjack.stats.GameStats;
import com.blackjack.ui.GameUI;

public class StubGameUI implements GameUI {
    @Override public void displayMessage(String message) {}
    @Override public void displayMessageWithoutLn(String message) {}
    @Override public void displayBalance(double balance) {}
    @Override public int promptDeckChoice() { return 0; }
    @Override public boolean askYesNo(String prompt) { return false; }
    @Override public double askInsuranceBet(double maxInsurance) { return 0; }
    @Override public double getBet(double min, double max) { return 0; }
    @Override public Move getPlayerMove(boolean canDouble, boolean canSplit, boolean canSurrender) { return Move.EXIT; }
    @Override public void showPlayerHand(Player player) {}
    @Override public void showDealerHand(Dealer dealer, boolean hideHoleCard) {}
    @Override public void showOutcome(String result) {}
    @Override public void showStats(GameStats stats) {}
    @Override public void showHand(Hand hand) {}
    @Override public int promptMenuChoice() { return 0; }
    @Override public String prompt(String message) { return ""; }
}
