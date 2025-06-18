package com.blackjack;

import javafx.application.Application;
import javafx.stage.Stage;
import com.blackjack.ui.GameFxUI;   // ← our soon-to-be class

public class MainFx extends Application {
    @Override
    public void start(Stage primaryStage) {
        GameFxUI fxUI = new GameFxUI(primaryStage);
        CasinoApp app = new CasinoApp(fxUI);
        app.run();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
