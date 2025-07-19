package com.blackjack.gui.controllers;

import com.blackjack.gui.ApplicationContext;
import com.blackjack.game.BlackjackGame;
import com.blackjack.CasinoApp;
import com.blackjack.game.GameRules;
import com.blackjack.gui.JavaFXGameUI;
import com.blackjack.model.Dealer;
import com.blackjack.model.Shoe;
import com.blackjack.stats.GameStats;
import com.blackjack.user.UserManager;
import com.blackjack.user.PlayerProfile;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class ShoeChoiceController {
    @FXML private Button twoDeckButton;
    @FXML private Button sixDeckButton;
    @FXML private Button backButton;

    private final GameRules rules = new GameRules();
    private final Dealer dealer = new Dealer();
    private final GameStats stats = new GameStats();
    private final UserManager userManager = ApplicationContext.getUserManager();
    private final PlayerProfile profile = ApplicationContext.getProfile();

    @FXML
    private void handleTwoDecks() throws Exception {
        startGameWithShoe(2);
    }

    @FXML
    private void handleSixDecks() throws Exception {
        startGameWithShoe(6);
    }

    private void startGameWithShoe(int decks) throws Exception {
        // in ShoeChoiceController.startGameWithShoe():
        Stage stage = (Stage) twoDeckButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/blackjack/gui/game_table_view.fxml"));
        Scene scene = new Scene(loader.load());
        GameTableController controller = loader.getController();
        controller.initGame(decks);
        controller.startRound();          // <- begin the first round
        stage.setScene(scene);

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