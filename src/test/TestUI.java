package test;

import com.blackjack.model.Move;
import com.blackjack.ui.ConsoleUI;


public class TestUI extends ConsoleUI {
    private boolean splitRequested = false;

    public TestUI(){

    }

    @Override
    public double getBet(double min, double max) {
        return 100; // Always bet 100 for testing
    }

    @Override
    public Move getPlayerMove(boolean canDouble, boolean canSplit, boolean canSurrender) {
        if (canSplit && !splitRequested) {
            splitRequested = true;
            return Move.SPLIT;
        }
        return Move.STAY; // Stay after split
    }

    @Override
    public void displayMessageWithoutLn(String message) {
        System.out.print(message);
    }

    @Override
    public void displayMessage(String message) {
        System.out.println("[MESSAGE] " + message);
    }

    @Override
    public int promptDeckChoice() {
        return 0;
    }

    @Override
    public boolean askYesNo(String prompt) {
        return false;
    }

    @Override
    public void showOutcome(String result) {
        System.out.println("[RESULT] " + result);
    }
}
