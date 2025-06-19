package com.blackjack.gui;

import com.blackjack.model.Hand;
import com.blackjack.model.Player;
import com.blackjack.model.Dealer;
import com.blackjack.model.Move;
import com.blackjack.stats.GameStats;
import com.blackjack.stats.RoundSummary;
import com.blackjack.ui.GameUI;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.Optional;

public class JavaFXGameUI implements GameUI {
    
    @Override
    public void displayMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        if (Platform.isFxApplicationThread()) {
            alert.showAndWait();
        } else {
            Platform.runLater(alert::showAndWait);
        }
    }

    @Override
    public void displayMessageWithoutLn(String message) {
        displayMessage(message);
    }

    @Override
    public void displayBalance(double balance) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Balance");
        alert.setHeaderText("Current Balance");
        alert.setContentText(String.format("$%.2f", balance));
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        if (Platform.isFxApplicationThread()) {
            alert.showAndWait();
        } else {
            Platform.runLater(alert::showAndWait);
        }
    }

    @Override
    public int promptDeckChoice() {
        throw new UnsupportedOperationException("Use shoe choice screen");
    }

    @Override
    public boolean askYesNo(String prompt) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(prompt);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        return result.filter(bt -> bt == ButtonType.YES).isPresent();
    }

    @Override
    public double askInsuranceBet(double maxInsurance) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Insurance Bet");
        dialog.setHeaderText(String.format("Dealer shows an Ace. Place insurance bet (max %.2f)", maxInsurance));
        dialog.setContentText("Bet:");
        while (true) {
            Optional<String> res = dialog.showAndWait();
            if (!res.isPresent()) return 0;
            try {
                double bet = Double.parseDouble(res.get());
                if (bet >= 0 && bet <= maxInsurance) {
                    return bet;
                }
            } catch (Exception ignored) {}
            dialog.setHeaderText("Invalid amount. Try again:");
        }
    }

    @Override
    public double getBet(double min, double max) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Place Bet");
        dialog.setHeaderText(String.format("Place your bet (%.2f - %.2f)", min, max));
        dialog.setContentText("Bet:");
        while (true) {
            Optional<String> res = dialog.showAndWait();
            if (!res.isPresent()) return -1;
            String s = res.get();
            if (s.equalsIgnoreCase("exit") || s.equalsIgnoreCase("e") || s.equalsIgnoreCase("q") || s.equalsIgnoreCase("quit")) return -1;
            try {
                double bet = Double.parseDouble(s);
                if (bet >= min && bet <= max) {
                    return bet;
                }
            } catch (Exception ignored) {}
            dialog.setHeaderText("Invalid bet. Try again:");
        }
    }

    @Override
    public Move getPlayerMove(boolean canDouble, boolean canSplit, boolean canSurrender) {
        List<String> options = new java.util.ArrayList<>();
        options.add("Hit");
        options.add("Stay");
        if (canDouble) options.add("Double");
        if (canSplit) options.add("Split");
        if (canSurrender) options.add("Surrender");
        options.add("Exit");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Your Move");
        dialog.setHeaderText("Choose your move:");
        Optional<String> res = dialog.showAndWait();
        if (!res.isPresent()) return Move.EXIT;
        try {
            return Move.fromString(res.get());
        } catch (Exception e) {
            return Move.EXIT;
        }
    }

    @Override
    public void showPlayerHand(Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your hand(s):\n");
        int i = 1;
        for (Hand hand : player.getHands()) {
            sb.append(String.format("Hand %d (%s): %s\n", i++, hand.getDisplayValue(), hand.getCards()));
        }
        displayMessage(sb.toString());
    }

    @Override
    public void showDealerHand(Dealer dealer, boolean hideHoleCard) {
        Hand hand = dealer.getHand();
        StringBuilder sb = new StringBuilder();
        sb.append("Dealer's hand:\n");
        if (hideHoleCard && hand.getCards().size() > 1) {
            sb.append(hand.getCards().get(0)).append(" [HIDDEN]\n");
        } else {
            sb.append(hand.getCards()).append(" (Value: ").append(hand.getDisplayValue()).append(")\n");
        }
        displayMessage(sb.toString());
    }

    @Override
    public void showOutcome(String result) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Round Result");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        if (Platform.isFxApplicationThread()) {
            alert.showAndWait();
        } else {
            Platform.runLater(alert::showAndWait);
        }
    }

    @Override
    public void showStats(GameStats stats) {
        StringBuilder sb = new StringBuilder();
        sb.append("Total Rounds: ").append(stats.getTotalRounds()).append("\n")
                .append("Wins: ").append(stats.getWins()).append("\n")
                .append("Losses: ").append(stats.getLosses()).append("\n")
                .append("Pushes: ").append(stats.getPushes()).append("\n")
                .append(String.format("Win Rate: %.2f%%\n", stats.getWinRate()))
                .append(String.format("Avg Bet: %.2f\n", stats.getAverageBet()))
                .append(String.format("Net Profit: %.2f\n\n", stats.getNetProfit()))
                .append("Last 10 Rounds:\n");
        for (RoundSummary r : stats.getHistory()) {
            sb.append(String.format("%d %s Bet:%.2f Payout:%.2f You:%d Dealer:%d\n",
                    r.getHandNumber(), r.getResult(), r.getBet(), r.getPayout(), r.getPlayerValue(), r.getDealerValue()));
        }
        displayMessage(sb.toString());
    }

    @Override
    public void showHand(Hand hand) {
        displayMessage(String.format("(value: %s) %s", hand.getDisplayValue(), hand.getCards()));
    }

    @Override
    public int promptMenuChoice() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String prompt(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input");
        dialog.setHeaderText(message);
        Optional<String> res = dialog.showAndWait();
        return res.orElse("");
    }
}
