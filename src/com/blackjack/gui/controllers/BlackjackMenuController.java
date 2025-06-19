package com.blackjack.gui.controllers;

import com.blackjack.gui.ApplicationContext;
import com.blackjack.user.PlayerProfile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class BlackjackMenuController {
    @FXML private Label balanceLabel;
    @FXML private Button playButton;
    @FXML private Button statsButton;
    @FXML private Button settingsButton;
    @FXML private Button backButton;

    @FXML
    private void initialize() {
        // Display current balance
        PlayerProfile profile = ApplicationContext.getProfile();
        balanceLabel.setText(String.format("💰 Balance: $%.2f", profile.getBalance()));
    }

    @FXML
    private void handlePlay() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/blackjack/gui/shoe_choice_view.fxml")
        );
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) playButton.getScene().getWindow();
        stage.setTitle("Select Shoe");
        stage.setScene(scene);
    }

    @FXML
    private void handleStats() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/blackjack/gui/blackjack_stats_view.fxml")
        );
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) statsButton.getScene().getWindow();
        stage.setTitle("Blackjack Stats");
        stage.setScene(scene);
    }

    @FXML
    private void handleSettings() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/blackjack/gui/blackjack_settings_view.fxml")
        );
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) settingsButton.getScene().getWindow();
        stage.setTitle("Blackjack Settings");
        stage.setScene(scene);
    }

    @FXML
    private void handleBack() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/blackjack/gui/dashboard_view.fxml")
        );
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setTitle("Casino Dashboard");
        stage.setScene(scene);
    }
}
