package com.blackjack.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class BlackjackSettingsController {
    @FXML private Button backButton;

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
