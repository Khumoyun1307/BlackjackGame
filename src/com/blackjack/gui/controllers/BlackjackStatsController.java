package com.blackjack.gui.controllers;

import com.blackjack.gui.ApplicationContext;
import com.blackjack.stats.StatsManager;
import com.blackjack.stats.GameStats;
import com.blackjack.stats.RoundSummary;
import com.blackjack.user.PlayerProfile;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class BlackjackStatsController {
    @FXML private Label totalRoundsLabel;
    @FXML private Label winsLabel;
    @FXML private Label lossesLabel;
    @FXML private Label pushesLabel;
    @FXML private Label winRateLabel;
    @FXML private Label avgBetLabel;
    @FXML private Label netProfitLabel;
    @FXML private ListView<String> historyListView;
    @FXML private Button backButton;

    @FXML
    private void initialize() {
        PlayerProfile profile = ApplicationContext.getProfile();
        GameStats stats = StatsManager.loadStats(profile.getUsername());

        totalRoundsLabel.setText(String.valueOf(stats.getTotalRounds()));
        winsLabel.setText(String.valueOf(stats.getWins()));
        lossesLabel.setText(String.valueOf(stats.getLosses()));
        pushesLabel.setText(String.valueOf(stats.getPushes()));
        winRateLabel.setText(String.format("%.2f%%", stats.getWinRate()));
        avgBetLabel.setText(String.format("$%.2f", stats.getAverageBet()));
        netProfitLabel.setText(String.format("$%.2f", stats.getNetProfit()));

        for (RoundSummary r : stats.getHistory()) {
            String entry = String.format("Hand %d | %s | Bet: $%.2f | Payout: $%.2f | You: %d | Dealer: %d",
                    r.getHandNumber(), r.getResult(), r.getBet(), r.getPayout(), r.getPlayerValue(), r.getDealerValue());
            historyListView.getItems().add(entry);
        }
    }

    @FXML
    private void handleBack() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/blackjack/gui/blackjack_menu_view.fxml")
        );
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setTitle("Blackjack Room");
        stage.setScene(scene);
    }
}
