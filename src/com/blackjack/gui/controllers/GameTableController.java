package com.blackjack.gui.controllers;

import com.blackjack.game.RoundEventListener;
import com.blackjack.game.RoundManager;
import com.blackjack.model.Card;
import com.blackjack.model.Hand;
import com.blackjack.model.Move;
import com.blackjack.stats.RoundSummary;
import com.blackjack.user.PlayerProfile;
import com.blackjack.model.Player;
import com.blackjack.model.Dealer;
import com.blackjack.model.Shoe;
import com.blackjack.game.GameRules;
import com.blackjack.stats.GameStats;
import com.blackjack.stats.StatsManager;

import com.blackjack.gui.ApplicationContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

public class GameTableController implements RoundEventListener {
    public ScrollPane playerHandsScroll;
    @FXML private HBox dealerCardsBox;
    @FXML private VBox playerHandsContainer;
    @FXML private Label balanceLabel, dealerValueLabel;
    @FXML private TextArea statusArea;
    @FXML private TextField betField, insuranceField;
    @FXML private Button placeBetButton, placeInsuranceButton;
    @FXML private HBox insuranceBox;
    @FXML private Button hitButton, stayButton, doubleButton, splitButton, surrenderButton, exitButton;
    @FXML private Button continueButton, backMenuButton;

    private int activeHandIndex = -1;

    private RoundManager roundManager;

    @FXML
    public void initialize() {
        disableAll();
        insuranceBox.setVisible(false);

        placeBetButton.setOnAction(e -> roundManager.placeBet(parse(betField)));
        placeInsuranceButton.setOnAction(e -> {
            insuranceBox.setVisible(false);
            roundManager.placeInsurance(parse(insuranceField));
        });

        hitButton.setOnAction(e -> roundManager.playerMove(Move.HIT));
        stayButton.setOnAction(e -> roundManager.playerMove(Move.STAY));
        doubleButton.setOnAction(e -> roundManager.playerMove(Move.DOUBLE));
        splitButton.setOnAction(e -> roundManager.playerMove(Move.SPLIT));
        surrenderButton.setOnAction(e -> roundManager.playerMove(Move.SURRENDER));
        exitButton.setOnAction(e -> roundManager.playerMove(Move.EXIT));

        continueButton.setOnAction(e -> startRound());

        backMenuButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/com/blackjack/gui/blackjack_menu_view.fxml"));
                Scene s = new Scene(loader.load());
                Stage st = (Stage) backMenuButton.getScene().getWindow();
                st.setTitle("Blackjack Room");
                st.setScene(s);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void initGame(int decks) {
        PlayerProfile profile = ApplicationContext.getProfile();
        Player player = new Player(profile.getUsername(), profile.getBalance());
        Dealer dealer = new Dealer();
        Shoe shoe = new Shoe(decks);
        shoe.shuffle();

        shoe.prependCard(new Card(Card.Rank.EIGHT, Card.Suit.DIAMONDS)); // re-split 2nd card
        shoe.prependCard(new Card(Card.Rank.EIGHT, Card.Suit.HEARTS));   // re-split 1st card
        shoe.prependCard(new Card(Card.Rank.EIGHT, Card.Suit.DIAMONDS)); // split 2nd card
        shoe.prependCard(new Card(Card.Rank.EIGHT, Card.Suit.CLUBS));    // split 1st card

        shoe.prependCard(new Card(Card.Rank.KING,  Card.Suit.HEARTS));   // dealer up-card
        shoe.prependCard(new Card(Card.Rank.EIGHT, Card.Suit.HEARTS));   // player 2nd card
        shoe.prependCard(new Card(Card.Rank.FIVE,  Card.Suit.CLUBS));    // dealer hole-card
        shoe.prependCard(new Card(Card.Rank.EIGHT, Card.Suit.SPADES));   // player 1st card

        GameRules rules = new GameRules();
        GameStats stats = StatsManager.loadStats(profile.getUsername());
        updateBalance(player.getBalance());

        this.roundManager = new RoundManager(player, dealer, shoe, rules, stats, this);
    }

    void startRound() {
        // 1) Disable all controls & clear status
        disableAll();
        statusArea.clear();

        // 2) **CLEAR OUT EVERY CARD** before we deal again
        //    Do this directly, not inside Platform.runLater,
        //    so we don’t race with render(...)’s own clear.
        playerHandsContainer.getChildren().clear();
        dealerCardsBox.getChildren().clear();
        dealerValueLabel.setText("");

        // 3) Now kick off the new round
        roundManager.startRound();
    }



    private double parse(TextField tf) {
        try {
            return Double.parseDouble(tf.getText().trim());
        } catch (Exception e) {
            append("Invalid number");
            return -1;
        }
    }

    private void disableAll() {
        Platform.runLater(() -> {
            betField.setDisable(true);
            placeBetButton.setDisable(true);
            insuranceBox.setVisible(false);
            insuranceField.setDisable(true);
            placeInsuranceButton.setDisable(true);
            hitButton.setDisable(true);
            stayButton.setDisable(true);
            doubleButton.setDisable(true);
            splitButton.setDisable(true);
            surrenderButton.setDisable(true);
            exitButton.setDisable(true);
            continueButton.setDisable(true);
        });
    }

    private void append(String msg) {
        Platform.runLater(() -> statusArea.appendText(msg + "\n"));
    }

    private void updateBalance(double bal) {
        Platform.runLater(() -> balanceLabel.setText(String.format("💰 $%.2f", bal)));
    }

    private void renderPlayerHands(List<Hand> hands) {
        int active = activeHandIndex;
        Platform.runLater(() -> {
            playerHandsContainer.getChildren().clear();
            for (int i = 0; i < hands.size(); i++) {
                HBox handBox = new HBox(10);
                handBox.setAlignment(Pos.CENTER);

                // give every row a light border...
                handBox.setStyle("-fx-border-color: #ccc; -fx-border-radius: 4; -fx-padding: 5;");
                // ...and highlight the active one
                if (i == active) {
                    handBox.setStyle(
                            "-fx-border-color: #448aff; -fx-border-width: 2; "
                                    + "-fx-border-radius: 4; -fx-padding: 5;"
                    );
                }

                for (Card c : hands.get(i).getCards()) {
                    String path = String.format(
                            "/cards/%s_of_%s.png",
                            c.getRank().toString().toLowerCase(),
                            c.getSuit().toString().toLowerCase()
                    );
                    URL imgUrl = getClass().getResource(path);
                    if (imgUrl == null) {
                        append("⚠️ Missing image: " + path);
                        continue;
                    }
                    ImageView iv = new ImageView(new Image(imgUrl.toExternalForm()));
                    iv.setFitWidth(80);
                    iv.setPreserveRatio(true);
                    handBox.getChildren().add(iv);
                }
                playerHandsContainer.getChildren().add(handBox);
            }
        });
    }

    private void render(HBox box, List<Card> cards) { 
        Platform.runLater(() -> {
            box.getChildren().clear();
            for (Card c : cards) {
                String path = String.format("/cards/%s_of_%s.png",
                        c.getRank().toString().toLowerCase(),
                        c.getSuit().toString().toLowerCase());

                // --- begin safe-loading patch ---
                URL imgUrl = getClass().getResource(path);
                if (imgUrl == null) {
                    // log in the status area so you can see what file it's looking for
                    append("⚠️ Missing image: " + path);
                    continue;
                }
                ImageView iv = new ImageView(new Image(imgUrl.toExternalForm()));
                // --- end safe-loading patch ---

                iv.setFitWidth(80);
                iv.setPreserveRatio(true);
                box.getChildren().add(iv);
            }
        });
    }

    // --- RoundEventListener callbacks ---

    @Override
    public void onShoeShuffled(int decks) {
        append("Shuffled " + decks + " decks.");
    }

    @Override
    public void onBetRequested(double min, double max) {
        Platform.runLater(() -> {
            betField.clear();
            betField.setDisable(false);
            placeBetButton.setDisable(false);
            append(String.format("Place bet: $%.2f - $%.2f", min, max));
        });
    }

    @Override
    public void onBetPlaced(double amt) {
        append("Bet: $" + String.format("%.2f", amt));
        updateBalance(roundManager.getPlayer().getBalance());
    }

    @Override
    public void onInitialDeal(List<Card> playerCards, Card dealerUpCard) {
        renderPlayerHands(roundManager.getPlayer().getHands());
        render(dealerCardsBox, List.of(dealerUpCard));
        // Show dealer’s up-card value
        dealerValueLabel.setText("Dealer: " + dealerUpCard.getValue());
        append("Initial deal done.");
    }

    @Override
    public void onOfferInsurance(double max) {
        Platform.runLater(() -> {
            insuranceBox.setVisible(true);
            insuranceField.clear();
            insuranceField.setDisable(false);
            placeInsuranceButton.setDisable(false);
            append("Offer insurance up to $" + String.format("%.2f", max));
        });
    }

    @Override
    public void onInsurancePlaced(double amt) {
        append("Insurance: $" + String.format("%.2f", amt));
    }

    @Override
    public void onPlayerTurnStart(int idx, Hand hand,
                                  boolean canHit, boolean canDouble,
                                  boolean canSplit, boolean canSurrender) {
        // 1) Record which hand is now active
        this.activeHandIndex = idx;

        // 2) Re-draw all of the hands (so you see the new highlight)
        renderPlayerHands(roundManager.getPlayer().getHands());

        // 3) Enable/disable your buttons _and_ scroll to the active hand
        Platform.runLater(() -> {
            // enable the controls for the current hand
            hitButton.setDisable(!canHit);
            stayButton.setDisable(false);
            doubleButton.setDisable(!canDouble);
            splitButton.setDisable(!canSplit);
            surrenderButton.setDisable(!canSurrender);
            exitButton.setDisable(false);

            // now auto-scroll so the active hand is in view
            if (playerHandsContainer.getChildren().size() > idx) {
                Node activeNode = playerHandsContainer.getChildren().get(idx);
                Bounds contentBounds = playerHandsContainer.getBoundsInLocal();
                Bounds nodeBounds    = activeNode.getBoundsInParent();
                Bounds viewportBounds= playerHandsScroll.getViewportBounds();

                // compute a vvalue between 0 and 1
                double v = (nodeBounds.getMinY())
                        / (contentBounds.getHeight() - viewportBounds.getHeight());
                playerHandsScroll.setVvalue(Math.min(Math.max(v, 0), 1));
            }
        });

        // 4) Log for the player
        append("Your turn: Hand " + (idx+1) + " (value " + hand.getValue() + ")");
    }

    @Override
    public void onCardDrawn(String actor, Card card, int idx, Hand updatedHand) {
        if (actor.equals("You")) {
            renderPlayerHands(roundManager.getPlayer().getHands());
        } else {
            // updatedHand is dealer's hand here
            render(dealerCardsBox, updatedHand.getCards());
            dealerValueLabel.setText("Dealer: " + updatedHand.getValue());
        }
        append(actor + " drew: " + card.getShortDisplay());
    }

    @Override
    public void onDealerTurnStart() {
        append("Dealer's turn");
        // disable all action buttons at dealer turn
        Platform.runLater(() -> {
            hitButton.setDisable(true);
            stayButton.setDisable(true);
            doubleButton.setDisable(true);
            splitButton.setDisable(true);
            surrenderButton.setDisable(true);
            exitButton.setDisable(true);
        });
    }

    @Override
    public void onDealerCardDrawn(Card card, Hand updatedDealerHand) {
        render(dealerCardsBox, updatedDealerHand.getCards());
        dealerValueLabel.setText("Dealer: " + updatedDealerHand.getValue());
        append("Dealer drew: " + card.getShortDisplay());
    }

    @Override
    public void onRoundResult(RoundSummary summary) {
        append(String.format(
                "Hand %d %s (bet $%.2f → $%.2f). You:%d D:%d",
                summary.getHandNumber(),
                summary.getResult(),
                summary.getBet(),
                summary.getPayout(),
                summary.getPlayerValue(),
                summary.getDealerValue()
        ));
    }

    @Override
    public void onBalanceUpdated(double newBalance) {
        updateBalance(newBalance);
    }

    @Override
    public void onLog(String message) {
        append(message);
    }


    @Override
    public void onRevealDealerCard(List<Card> dealerCards) {
        render(dealerCardsBox, dealerCards);
        int total = dealerCards.stream().mapToInt(Card::getValue).sum();
        dealerValueLabel.setText("Dealer: " + total);
        append("Dealer reveals hole card.");
    }

    @Override
    public void onSessionContinuationRequested() {
        Platform.runLater(() -> continueButton.setDisable(false));
        append("Click Continue to play next round or exit.");
    }
}
