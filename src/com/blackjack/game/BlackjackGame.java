package com.blackjack.game;

import com.blackjack.model.Dealer;
import com.blackjack.model.Player;
import com.blackjack.model.Shoe;
import com.blackjack.stats.GameStats;
import com.blackjack.stats.StatsManager;
import com.blackjack.ui.ConsoleUI;
import com.blackjack.ui.GameUI;
import com.blackjack.user.PlayerProfile;
import com.blackjack.user.UserManager;

public class BlackjackGame {
    private final GameUI ui;
    private final GameRules rules;
    private final Player player;
    private final Dealer dealer;
    private final GameStats stats;
    private final PlayerProfile profile;
    private final UserManager userManager;
    private final RoundEventListener roundListener;


    private Shoe shoe;
    private boolean keepPlaying;

    public BlackjackGame(GameUI ui, GameRules rules, PlayerProfile profile, Dealer dealer, GameStats stats, UserManager userManager, RoundEventListener roundListener) {
        this.ui = ui;
        this.rules = rules;
        this.profile = profile;
        this.player = new Player(profile.getUsername(), profile.getBalance()); // <== use profile's balance
        this.dealer = dealer;
        this.stats = stats;
        this.roundListener = roundListener;
        this.keepPlaying = true;
        this.userManager = userManager;
    }

    public void start() {
        ui.displayMessage("=== Welcome to Blackjack ===");
        ui.displayMessage("");
        ui.displayBalance(player.getBalance());
        while (keepPlaying) {
            int deckChoice = ui.promptDeckChoice(); // 2 or 6
            shoe = new Shoe(deckChoice);
            shoe.shuffle();
            ui.displayMessage(String.format("You chose %d deck type shoe to play!", deckChoice));

            playUntilPlayerQuitsOrReshuffles();
        }

        profile.setBalance(player.getBalance());
        userManager.saveProfile(profile);
        ui.displayMessage("Thanks for playing! Final balance: $" + player.getBalance());
    }

    private void playUntilPlayerQuitsOrReshuffles() {
        RoundManager roundManager = new RoundManager(player, dealer, shoe, rules, stats, roundListener);

        while (true) {
            ui.displayMessage("\n--- New Round ---");
            roundManager.startRound();
            profile.setBalance(player.getBalance());
            userManager.saveProfile(profile);
            StatsManager.saveStats(profile.getUsername(), stats);

            if (player.getBalance() < 10) {
                ui.displayMessage("You're out of money! Game over.");
                keepPlaying = false;
                break;
            }

            if (!ui.askYesNo("Play another round?")) {
                if (ui.askYesNo("Do you want to switch shoe (deck type)?")) {
                    break; // Exit to main menu to choose shoe
                } else {
                    keepPlaying = false; // Player chose to end the game
                    break;
                }
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerProfile getProfile(){
        return profile;
    }
}
