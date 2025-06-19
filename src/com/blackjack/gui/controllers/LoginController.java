package com.blackjack.gui.controllers;

import com.blackjack.gui.ApplicationContext;
import com.blackjack.user.PlayerProfile;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin() throws Exception {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        PlayerProfile profile = ApplicationContext.getUserManager().login(username, password);
        if (profile != null) {
            ApplicationContext.setProfile(profile);
            // Load Dashboard
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/blackjack/gui/dashboard_view.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Casino Dashboard");
            stage.setScene(scene);
        } else {
            messageLabel.setText("❌ Incorrect username or password.");
        }
    }

    @FXML
    private void handleSignup() throws Exception {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (ApplicationContext.getUserManager().usernameExists(username)) {
            messageLabel.setText("❌ Username already taken.");
            return;
        }
        PlayerProfile profile = ApplicationContext.getUserManager().register(username, password);
        if (profile != null) {
            ApplicationContext.setProfile(profile);
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/blackjack/gui/dashboard_view.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Casino Dashboard");
            stage.setScene(scene);
        } else {
            messageLabel.setText("❌ Sign-up failed. Try again.");
        }
    }
}