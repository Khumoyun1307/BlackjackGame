package com.blackjack.gui.controllers;

import com.blackjack.gui.ApplicationContext;
import com.blackjack.user.PlayerProfile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardController {
    @FXML private Button playBlackjackButton;
    @FXML private Button viewStatsButton;
    @FXML private Button settingsButton;
    @FXML private Button logoutButton;
    @FXML private Label welcomeLabel;

    @FXML
    private void initialize() {
        PlayerProfile profile = ApplicationContext.getProfile();
        welcomeLabel.setText("Welcome, " + profile.getUsername() + "!");
    }

    @FXML
    private void handlePlayBlackjack() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/blackjack/gui/blackjack_menu_view.fxml")
        );
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) playBlackjackButton.getScene().getWindow();
        stage.setTitle("Blackjack Room");
        stage.setScene(scene);
    }

    @FXML
    private void handleViewStats() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/blackjack/gui/casino_stats_view.fxml")
        );
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) viewStatsButton.getScene().getWindow();
        stage.setTitle("Casino Stats");
        stage.setScene(scene);
    }

    @FXML
    private void handleSettings() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/blackjack/gui/casino_settings_view.fxml")
        );
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) settingsButton.getScene().getWindow();
        stage.setTitle("Casino Settings");
        stage.setScene(scene);
    }

    @FXML
    private void handleLogout() throws Exception {
        ApplicationContext.setProfile(null);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/blackjack/gui/login_view.fxml")
        );
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setTitle("Casino App – Login");
        stage.setScene(scene);
    }
}
