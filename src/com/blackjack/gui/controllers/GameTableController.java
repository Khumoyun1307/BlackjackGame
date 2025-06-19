package com.blackjack.gui.controllers;

import com.blackjack.game.RoundManager;
import com.blackjack.game.GameRules;
import com.blackjack.gui.JavaFXGameUI;
import com.blackjack.model.Card;
import com.blackjack.model.Dealer;
import com.blackjack.model.Hand;
import com.blackjack.model.Player;
import com.blackjack.model.Shoe;
import com.blackjack.stats.GameStats;
import com.blackjack.stats.StatsManager;
import com.blackjack.ui.GameUI;
import com.blackjack.user.PlayerProfile;
import com.blackjack.gui.ApplicationContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class GameTableController {
    // Inline bet controls
    @FXML private TextField betField;
    @FXML private Button placeBetButton;
    @FXML private HBox insuranceBox;
    @FXML private TextField insuranceField;
    @FXML private Button placeInsuranceButton;

    // Card display
    @FXML private HBox dealerCardsBox;
    @FXML private Label dealerValueLabel;
    @FXML private VBox playerHandsBox;

    // Action buttons
    @FXML private Button hitButton, stayButton, doubleButton, splitButton, surrenderButton, exitButton;

    // Round controls & status
    @FXML private Button newRoundButton, backMenuButton;
    @FXML private Label balanceLabel;
    @FXML private TextArea statusArea;

    private GameUI gameUI;
    private RoundManager roundManager;
    private Player player;
    private Dealer dealer;
    private GameRules rules;
    private Shoe shoe;
    private GameStats stats;

    /** Called once after loading to set up game state for a given deck count. */
    public void initGame(int decks) {
        // 1) Create your JavaFX-based UI impl:
        this.gameUI = new JavaFXGameUI();

        // 2) Existing setup:
        this.rules = new GameRules();
        PlayerProfile profile = ApplicationContext.getProfile();
        this.stats = StatsManager.loadStats(profile.getUsername());
        this.player = new Player(profile.getUsername(), profile.getBalance());
        this.dealer = new Dealer();
        this.shoe = new Shoe(decks);
        this.shoe.shuffle();

        // 3) Pass the real UI into RoundManager, not null:
        this.roundManager = new RoundManager(player, dealer, shoe, rules, gameUI, stats);

        // 4) Initialize controls:
        insuranceBox.setVisible(false);
        disableActionButtons(true);

        betField.setDisable(true);
        placeBetButton.setDisable(true);
        // Enable New Round once the scene loads:
        newRoundButton.setDisable(false);

        updateBalance();
    }

    @FXML
    private void initialize() {
        newRoundButton.setOnAction(e -> startNewRound());
        placeBetButton.setOnAction(e -> handlePlaceBet());
        placeInsuranceButton.setOnAction(e -> handlePlaceInsurance());
        hitButton.setOnAction(e -> playerHit());
        stayButton.setOnAction(e -> playerStay());
        doubleButton.setOnAction(e -> playerDouble());
        splitButton.setOnAction(e -> playerSplit());
        surrenderButton.setOnAction(e -> playerSurrender());
        exitButton.setOnAction(e -> endRound());
        backMenuButton.setOnAction(e -> backToMenu());
    }

    private void startNewRound() {
        statusArea.clear();
        clearCardBoxes();
        insuranceBox.setVisible(false);
        disableActionButtons(true);

        betField.clear();
        betField.setDisable(false);
        placeBetButton.setDisable(false);
    }

    private void handlePlaceBet() {
        double minBet = 10, maxBet = 1000;
        double bet;
        try {
            bet = Double.parseDouble(betField.getText());
        } catch (NumberFormatException ex) {
            appendStatus("Invalid bet amount.");
            return;
        }
        if (bet < minBet || bet > maxBet) {
            appendStatus(String.format("Bet must be between $%.2f and $%.2f.", minBet, maxBet));
            return;
        }

        // Set up player hand and bet
        player.resetHands();
        Hand playerHand = new Hand();
        player.addHand(playerHand);
        player.placeBet(playerHand, bet);
        appendStatus(String.format("Bet placed: $%.2f", bet));

        // Deal initial cards
        roundManager.dealInitialCards(playerHand);
        updateTableView();
        appendStatus("Initial deal: You " + playerHand.getDisplayValue());

        // Offer insurance if dealer hole card is Ace
        Card hole = dealer.getHand().getCards().get(0);
        if (hole.getRank() == Card.Rank.ACE && !rules.isBlackjack(playerHand)) {
            insuranceBox.setVisible(true);
        } else {
            proceedAfterInsurance(playerHand);
        }

        // Disable bet controls
        betField.setDisable(true);
        placeBetButton.setDisable(true);
    }

    private void handlePlaceInsurance() {
        double ins;
        try {
            ins = Double.parseDouble(insuranceField.getText());
        } catch (Exception ex) {
            appendStatus("Invalid insurance amount.");
            return;
        }
        Hand ph = player.getHands().get(0);
        player.adjustBalance(-ins);
        ph.setInsuranceBet(ins);
        appendStatus(String.format("Insurance bet placed: $%.2f", ins));
        insuranceBox.setVisible(false);
        proceedAfterInsurance(ph);
    }

    /** Checks for blackjack, otherwise enables player action buttons. */
    private void proceedAfterInsurance(Hand playerHand) {
        boolean playerBJ = rules.isBlackjack(playerHand);
        boolean dealerBJ = rules.isBlackjack(dealer.getHand());

        if (playerBJ || dealerBJ) {
            // Reveal dealer hand
            updateTableView();
            if (playerBJ && dealerBJ) {
                appendStatus("Push: both have Blackjack.");
                player.adjustBalance(playerHand.getBet());
            } else if (playerBJ) {
                appendStatus("Blackjack! You win.");
                player.adjustBalance(rules.getBlackjackPayout(playerHand));
            } else {
                appendStatus("Dealer has Blackjack. You lose.");
            }
            endRound();
        } else {
            disableActionButtons(false);
            appendStatus("Your turn.");
        }
        updateBalance();
    }

    private void playerHit() {
        Hand h = player.getHands().get(0);
        boolean bust = roundManager.drawAndAddToHand(h, "You");
        updateTableView();
        appendStatus("You hit. Value: " + h.getDisplayValue());
        if (bust) {
            appendStatus("You busted.");
            endRound();
        }
    }

    private void playerStay() {
        appendStatus("You stand at " + player.getHands().get(0).getDisplayValue());
        roundManager.dealerTurn();
        updateTableView();
        appendStatus("Dealer stands at " + dealer.getHand().getDisplayValue());
        roundManager.evaluateResults();
        endRound();
    }

    private void playerDouble() {
        Hand h = player.getHands().get(0);
        if (rules.canDouble(h, player.getBalance())) {
            player.adjustBalance(-h.getBet());
            roundManager.drawAndAddToHand(h, "You");
            h.setBet(h.getBet() * 2);
            updateTableView();
            appendStatus("You doubled. Bet now $" + String.format("%.2f", h.getBet()));
            if (!rules.isBust(h)) playerStay();
            else appendStatus("You busted on double.");
        }
    }

    private void playerSplit() {
        Hand h = player.getHands().get(0);
        roundManager.splitHands(h);
        updateTableView();
        appendStatus("Hand split.");
    }

    private void playerSurrender() {
        Hand h = player.getHands().get(0);
        roundManager.surrenderHand(h);
        updateTableView();
        appendStatus("You surrendered. Half bet returned.");
        endRound();
    }

    /** Ends the round: disables actions and persists state. */
    private void endRound() {
        disableActionButtons(true);

        // Persist balance & stats
        PlayerProfile profile = ApplicationContext.getProfile();
        profile.setBalance(player.getBalance());
        ApplicationContext.getUserManager().saveProfile(profile);
        StatsManager.saveStats(profile.getUsername(), stats);

        appendStatus("Round complete.");
        updateBalance();
    }

    private void appendStatus(String msg) {
        statusArea.appendText(msg + "\n");
    }

    private String imagePathFor(Card c) {
        String rank = c.getRank().toString().toLowerCase();
        // if you ever have numeric ranks, map "ten" -> "ten" etc.
        return String.format("/cards/%s_of_%s.png",
                rank,
                c.getSuit().toString().toLowerCase());
    }

    private void updateTableView() {
        dealerCardsBox.getChildren().clear();
        for (Card c : dealer.getHand().getCards()) {
            String path = imagePathFor(c);
            URL url = getClass().getResource(path);
            if (url == null) {
                appendStatus("⚠️ Resource not found: " + path);
                continue;
            }
            ImageView iv = new ImageView(new Image(url.toExternalForm()));
            iv.setFitWidth(80); iv.setPreserveRatio(true);
            dealerCardsBox.getChildren().add(iv);
        }
        dealerValueLabel.setText("Dealer: " + dealer.getHand().getDisplayValue());

        playerHandsBox.getChildren().clear();
        for (Hand hand : player.getHands()) {
            HBox hb = new HBox(5);
            for (Card c : hand.getCards()) {
                String path = imagePathFor(c);
                URL url = getClass().getResource(path);
                if (url == null) {
                    appendStatus("⚠️ Resource not found: " + path);
                    continue;
                }
                ImageView iv = new ImageView(new Image(url.toExternalForm()));
                iv.setFitWidth(80); iv.setPreserveRatio(true);
                hb.getChildren().add(iv);
            }
            hb.getChildren().add(new Label(String.format("Bet: $%.2f", hand.getBet())));
            playerHandsBox.getChildren().add(hb);
        }
    }

    private void disableActionButtons(boolean disable) {
        hitButton.setDisable(disable);
        stayButton.setDisable(disable);
        doubleButton.setDisable(disable);
        splitButton.setDisable(disable);
        surrenderButton.setDisable(disable);
        placeInsuranceButton.setDisable(disable);
    }

    private void clearCardBoxes() {
        dealerCardsBox.getChildren().clear();
        playerHandsBox.getChildren().clear();
    }

    private void updateBalance() {
        balanceLabel.setText(String.format("💰 Balance: $%.2f", player.getBalance()));
    }

    private void backToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/blackjack/gui/blackjack_menu_view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) backMenuButton.getScene().getWindow();
            stage.setTitle("Blackjack Room");
            stage.setScene(scene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
