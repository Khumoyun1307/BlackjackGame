package com.blackjack.ui;

import com.blackjack.model.*;
import com.blackjack.stats.GameStats;
import com.blackjack.stats.RoundSummary;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI implements GameUI{

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayMessageWithoutLn(String message){
        System.out.print(message);
    }

    public void displayBalance(double balance) {
        System.out.printf("💰 Current Balance: $%.2f%n", balance);
    }

    @Override
    public int promptDeckChoice() {
        while (true) {
            System.out.print("Choose deck size (2 or 6): ");
            String input = scanner.nextLine();

            try {
                int deckChoice = Integer.parseInt(input);
                if (deckChoice == 2 || deckChoice == 6) {
                    return deckChoice;
                }
                displayMessage("Invalid choice. Please choose either 2 or 6.");
            } catch (NumberFormatException e) {
                displayMessage("Invalid input. Please enter a number.");
            }
        }
    }

    @Override
    public boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'Y' or 'N'.");
            }
        }
    }

    @Override
    public int promptMenuChoice() {
        System.out.print("Enter your choice: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; // Let controller handle invalid input
        }
    }

    @Override
    public double askInsuranceBet(double maxInsurance) {
        boolean wantsInsurance = askYesNo(
                String.format("Dealer shows an Ace. Do you want to place an insurance bet (up to %.2f)?", maxInsurance)
        );

        if (!wantsInsurance) return 0;

        while (true) {
            System.out.printf("Enter insurance bet (max %.2f): ", maxInsurance);
            try {
                double bet = Double.parseDouble(scanner.nextLine());
                if (bet >= 0 && bet <= maxInsurance) {
                    return bet;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid amount. Try again.");
        }
    }

    @Override
    public double getBet(double minBet, double maxBet) {

        while (true) {
            System.out.printf("Place your bet (%.2f - %.2f) or type '[e]xit' to [q]uit: ", minBet, maxBet);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("e") ||
            input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")  ) {
                return -1; // Use -1 as signal to quit mid-game
            }
            try {
                double bet = Double.parseDouble(input);
                if (bet >= minBet && bet <= maxBet) {
                    return bet;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Invalid bet. Try again.");
        }
    }

    @Override
//    public Move getPlayerMove(boolean canDouble, boolean canSplit) {
//        System.out.print("\nChoose your move [hit / stay");
//        if (canDouble) System.out.print(" / double");
//        if (canSplit) System.out.print(" / split");
//        System.out.print("]: ");
//
//        return scanner.nextLine().trim().toLowerCase();
//    }

    // With enum update

    public Move getPlayerMove(boolean canDouble, boolean canSplit, boolean canSurrender) {
        System.out.print("\nChoose your move [hit / stay");
        if (canDouble) System.out.print(" / double");
        if (canSplit) System.out.print(" / split");
        if (canSurrender) System.out.print(" / surrender");
        System.out.print(" / [e]xit or [q]uit");
        System.out.print("]: ");

        String input = scanner.nextLine().trim().toLowerCase();
        try {
            return Move.fromString(input);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input. Try again.");
            return getPlayerMove(canDouble, canSplit, canSurrender);
        }
    }

    @Override
    public String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }


    public void showHand(Hand hand) {
        System.out.printf("(value: %s)%n", hand.getDisplayValue());
        System.out.print(formatAsciiCards(hand.getCards()));
    }

    @Override
    public void showPlayerHand(Player player) {
        System.out.println("\nYour hand(s):");
        int i = 1;
        for(Hand hand : player.getHands()) {
            // Previous statement to show player's card with just rank and suit
//            System.out.printf("Hand %d: %s (value: %s)%n",
//                    i++, formatCards(hand.getCards()) , hand.getDisplayValue()); // you can use toString as well
            System.out.printf("Hand %d: (value: %s)%n", i++, hand.getDisplayValue());
            System.out.print(formatAsciiCards(hand.getCards()));
        }
    }

    // Previous function to show dealer's card with just rank and suit

//    public void showDealerHand(Dealer dealer, boolean hideHoleCard) {
//        Hand hand = dealer.getHand();
//        List<Card> cards = hand.getCards();
//        System.out.print("\nDealer's hand: ");
//        if (hideHoleCard && cards.size() > 1) {
//            System.out.printf("[%s, HIDDEN]%n", cards.get(0).getShortDisplay()); // can remove
//        } else {
//            System.out.printf("%s (Value: %s)%n", formatCards(hand.getCards()), hand.getDisplayValue());
//        }
//    }


    // Function to display dealer's hand using formatAsciiCards function

    @Override
    public void showDealerHand(Dealer dealer, boolean hideHoleCard) {
        Hand hand = dealer.getHand();
        List<Card> cards = hand.getCards();

        System.out.println("\nDealer's hand: ");
        if (hideHoleCard && cards.size() > 1) {
            Card shown = cards.get(0);
            String[] shownAscii = shown.getAsciiDisplay();
            String[] hiddenAscii = {
                    "┌─────┐",
                    "│░░░░░│",
                    "│░░░░░│",
                    "│░░░░░│",
                    "└─────┘"
            };

            for (int i = 0; i < 5; i++) {
                System.out.println(shownAscii[i] + " " + hiddenAscii[i]);
            }
        } else {
            System.out.print(formatAsciiCards(cards));
            System.out.printf(" (Value: %d)%n", hand.getValue());
        }
    }

    @Override
    public void showStats(GameStats stats) {
        System.out.println("\n📈 === Blackjack Game Stats ===");

        System.out.println("📋 Total Rounds Played: " + stats.getTotalRounds());
        System.out.println("✅ Wins: " + stats.getWins());
        System.out.println("❌ Losses: " + stats.getLosses());
        System.out.println("➖ Pushes: " + stats.getPushes());

        System.out.printf("🏆 Win Rate: %.2f%%\n", stats.getWinRate());
        System.out.printf("💰 Average Bet: $%.2f\n", stats.getAverageBet());
        System.out.printf("📊 Net Profit: $%.2f\n", stats.getNetProfit());

        System.out.println("\n🕒 Last 10 Rounds:");
        List<RoundSummary> history = stats.getHistory();
        int start = Math.max(0, history.size() - 10);
        List<RoundSummary> last10 = history.subList(start, history.size());

        if (last10.isEmpty()) {
            System.out.println("No rounds played yet.");
        } else {
            for (RoundSummary r : last10) {
                System.out.printf("Hand %d | %s | Bet: $%.2f | Payout: $%.2f | You: %d | Dealer: %d\n",
                        r.getHandNumber(), r.getResult(), r.getBet(), r.getPayout(), r.getPlayerValue(), r.getDealerValue());
            }
        }
    }

    @Override
    public void showOutcome(String result) {
        System.out.println("\n=== Round Result ===");
        System.out.println(result);
        System.out.println("====================\n");
    }

    // Previous statement to format cards with its name

//    private StringBuilder formatCards(List<Card> cards) {
//        StringBuilder sb = new StringBuilder("[");
//        for (int i = 0; i < cards.size(); i++) {
//            sb.append(cards.get(i));
//            if (i < cards.size() - 1) sb.append(", ");
//        }
//        sb.append("]");
//        return sb;
//    }
    // formatting cards display


    // Function to format cards with rank and suit

    private StringBuilder formatCards(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card.getShortDisplay()).append(" ");
        }
        return sb;
    }


    private String formatAsciiCards(List<Card> cards) {
        StringBuilder[] lines = new StringBuilder[5];
        for (int i = 0; i < lines.length; i++) lines[i] = new StringBuilder();

        for (Card card : cards) {
            String[] ascii = card.getAsciiDisplay();
            for (int i = 0; i < ascii.length; i++) {
                lines[i].append(ascii[i]).append(" ");
            }
        }

        StringBuilder result = new StringBuilder();
        for (StringBuilder line : lines) {
            result.append(line).append("\n");
        }

        return result.toString();
    }

    public void askInsuranceBet() {

    }

}
