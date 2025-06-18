package com.blackjack.ui;

import com.blackjack.model.Dealer;
import com.blackjack.model.Hand;
import com.blackjack.model.Move;
import com.blackjack.model.Player;
import com.blackjack.stats.GameStats;
import com.blackjack.ui.GameUI;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class GameFxUI implements GameUI {
    private final Stage stage;

    public GameFxUI(Stage stage) {
        this.stage = stage;
        stage.setTitle("Blackjack Casino");
        // you might build your first Scene here or lazily when needed
    }

    @Override
    public void displayBalance(double balance) {

    }

    @Override
    public void displayMessageWithoutLn(String message) {

    }

    @Override
    public void displayMessage(String message) {
        Platform.runLater(() -> {
            Alert info = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            info.showAndWait();
        });
    }

    @Override
    public int promptDeckChoice() {
        return 0;
    }

    @Override
    public boolean askYesNo(String prompt) {
        return false;
    }

    @Override
    public double getBet(double min, double max) {
        return 0;
    }

    @Override
    public Move getPlayerMove(boolean canDouble, boolean canSplit, boolean canSurrender) {
        return null;
    }

    @Override
    public void showPlayerHand(Player player) {

    }

    @Override
    public void showDealerHand(Dealer dealer, boolean hideHoleCard) {

    }

    @Override
    public void showOutcome(String result) {

    }

    @Override
    public double askInsuranceBet(double maxInsurance) {
        return 0;
    }

    @Override
    public void showHand(Hand hand) {

    }

    @Override
    public int promptMenuChoice() {
        return 0;
    }

    @Override
    public void showStats(GameStats stats) {

    }

    @Override
    public String prompt(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(message);
        Optional<String> result = dialog.showAndWait();
        return result.orElse("");
    }

    // TODO: implement the rest of GameUI:
    //   displayBalance, getBet, getPlayerMove, showPlayerHand, etc.
    // Each should update JavaFX controls (on the FX thread).
}
