package com.blackjack.game;

import com.blackjack.gui.controllers.GameTableController;
import com.blackjack.model.Dealer;
import com.blackjack.stats.GameStats;
import com.blackjack.stats.RoundSummary;
import com.blackjack.stats.StatsManager;
import com.blackjack.ui.GameUI;
import com.blackjack.user.PlayerProfile;
import com.blackjack.stats.RoundHistoryManager;
import com.blackjack.user.UserManager;

import java.util.List;

public class BlackjackRoom {

    private final GameUI ui;
    private final GameRules rules;
    private final Dealer dealer;
    private final GameStats stats;
    private PlayerProfile profile;
    private final UserManager userManager;
    private final RoundEventListener roundEventListener = new GameTableController();

    public BlackjackRoom(GameUI ui, GameRules rules, Dealer dealer, PlayerProfile profile, UserManager userManager, GameStats stats) {
        this.ui = ui;
        this.rules = rules;
        this.dealer = dealer;
        this.stats = stats;
        this.profile = profile;
        this.userManager = userManager;

    }

    public void launch() {
        boolean inBlackjackMenu = true;

        while (inBlackjackMenu) {
            printBlackjackMenu();
            int choice = ui.promptMenuChoice();

            switch (choice) {
                case 1 -> {
                    BlackjackGame game = new BlackjackGame(ui, rules, profile, dealer, stats, userManager, roundEventListener);
                    game.start();
                    profile.setBalance(game.getPlayer().getBalance());
                    userManager.saveProfile(profile);
                    StatsManager.saveStats(profile.getUsername(), stats);
                }
                case 2 -> ui.showStats(stats);
                case 3 -> ui.displayMessage("\n⚙️ Blackjack settings are under construction...\n");
                case 0 -> {
                    ui.displayMessage("Returning to Casino...");
                    inBlackjackMenu = false;
                }
                default -> ui.displayMessage("Invalid choice. Please try again.");
            }
        }
    }

    private void printBlackjackMenu() {
        System.out.println("\n=== ♠️ Blackjack Room ===");
        ui.displayBalance(profile.getBalance());
        System.out.println("1. ▶️  Play Blackjack");
        System.out.println("2. 📊  See Stats");
        System.out.println("3. ⚙️  Settings");
        System.out.println("0. 🔙  Back to Casino");
    }

    public PlayerProfile getBlackjackProfile(){
        return this.profile;
    }
}
