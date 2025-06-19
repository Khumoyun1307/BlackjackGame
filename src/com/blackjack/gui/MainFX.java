package com.blackjack.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the initial login view
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/blackjack/gui/Login_view.fxml")
        );
        Scene scene = new Scene(loader.load());

        // Apply global CSS styling
        scene.getStylesheets().add(
                getClass().getResource("/styles/application.css").toExternalForm()
        );

        // Configure the primary stage
        primaryStage.setTitle("Casino App - Login");
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}