package com.blackjack;
import com.blackjack.game.BlackjackRoom;
import com.blackjack.game.GameRules;
import com.blackjack.model.Dealer;
import com.blackjack.stats.GameStats;
import com.blackjack.stats.StatsManager;
import com.blackjack.ui.GameUI;
import com.blackjack.user.PlayerProfile;
import com.blackjack.user.UserManager;

public class CasinoApp {

    private final GameUI ui;
    private final GameRules rules;
    // private final Player player = new Player("Player", 1000);
    private final Dealer dealer;

    private final UserManager userManager;
    private PlayerProfile loggedInProfile;

    public CasinoApp(GameUI ui) {
        this.ui = ui;
        this.rules = new GameRules();
        this.dealer = new Dealer();
        this.userManager = new UserManager();
        this.loggedInProfile = null;
    }

    public void run() {
        boolean running = true;
        while (running) {
            if (!showLoginMenu()) {
                running = false; // user chose to exit from login menu
            } else {
                showDashboard(); // includes logout handling
            }
        }
    }

    private void showDashboard() {
        boolean running = true;
        while (running) {
            printWelcomeMessage(); // includes username
            int choice = ui.promptMenuChoice();
            running = handleMenuChoice(choice); // previously defined
        }
    }

    private boolean showLoginMenu() {
        while (true) {
            System.out.println("\n=== 🎰 Welcome to Our Game Room / Casino! ===");
            System.out.println("1. 🔐 Login");
            System.out.println("2. 📝 Sign Up");
            System.out.println("0. 🚪 Exit");

            int choice = ui.promptMenuChoice();
            switch (choice) {
                case 1:
                    if (handleLogin()) return true;
                    break;
                case 2:
                    if (handleSignup()) return true;
                    break;
                case 0:
                    ui.displayMessage("Goodbye!");
                    return false;
                default:
                    ui.displayMessage("Invalid option.");
            }
        }
    }

    private boolean handleLogin() {
        String username = ui.prompt("Enter username: ");
        String password = ui.prompt("Enter password: ");

        PlayerProfile profile = userManager.login(username, password);

        if (profile != null) {
            loggedInProfile = profile;
            ui.displayMessage("✅ Login successful. Welcome, " + username + "!");
            return true;
        } else {
            ui.displayMessage("❌ Incorrect username or password.");
            return false;
        }
    }

    private boolean handleSignup() {
        String username = ui.prompt("Choose a username: ");

        if (userManager.usernameExists(username)) {
            ui.displayMessage("❌ That username is already taken.");
            return false;
        }

        String password = ui.prompt("Choose a password: ");

        PlayerProfile profile = userManager.register(username, password);
        if (profile != null) {
            loggedInProfile = profile;
            ui.displayMessage("✅ Account created. Welcome, " + username + "!");
            return true;
        } else {
            ui.displayMessage("Something went wrong. Try again.");
            return false;
        }
    }

    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1 -> {
                loggedInProfile = userManager.getBlackjackProfile(loggedInProfile.getUsername());
                GameStats stats = StatsManager.loadStats(loggedInProfile.getUsername());
                BlackjackRoom blackjackRoom = new BlackjackRoom(ui, rules, dealer, loggedInProfile, userManager, stats);
                blackjackRoom.launch();
                loggedInProfile = blackjackRoom.getBlackjackProfile();
                userManager.saveProfile(loggedInProfile);
                StatsManager.saveStats(loggedInProfile.getUsername(), stats);
                return true;
            }
            case 2 -> {
                ui.displayMessage("\n📊 Casino-level stats are not available yet.\n");
                return true;
            }
            case 3 -> {
                ui.displayMessage("\n⚙️ Casino settings are under construction.\n");
                return true;
            }
            case 4 -> {
                ui.displayMessage("\n🔓 Logged out successfully.");
                loggedInProfile = null;
                return false;
            }
            case 0 -> {
                ui.displayMessage("\n👋 Thanks for visiting our Game Room! Goodbye.");
                System.exit(0);
                return false;
            }
            default -> {
                ui.displayMessage("❌ Invalid choice. Please select a valid option (0–4).");
                return true;
            }
        }
    }



    private void printWelcomeMessage() {
        System.out.println("\n=== 🎮 Welcome, " + loggedInProfile.getUsername() + "! ===\n");
        System.out.println("1. 🎲 Blackjack");
        System.out.println("2. 📊 Casino Stats");
        System.out.println("3. ⚙️ Casino Settings");
        System.out.println("4. 🔓 Log Out");
        System.out.println("0. 🚪 Exit");
    }
}
