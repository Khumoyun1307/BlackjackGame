package com.blackjack.gui.controllers;

import com.blackjack.gui.ApplicationContext;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class CasinoStatsController {
    @FXML private Button backButton;

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