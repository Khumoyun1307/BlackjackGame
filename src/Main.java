import com.blackjack.CasinoApp;
import com.blackjack.ui.ConsoleUI;
import com.blackjack.ui.GameUI;

public class Main {

    public static void main(String[] args) {
        GameUI console = new ConsoleUI();
        CasinoApp app = new CasinoApp(console);
        app.run();
    }

}
