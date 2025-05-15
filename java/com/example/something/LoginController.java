package com.example.something;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final String DB_URL = "jdbc:mysql://localhost:3306/chat_app";
    private final String DB_USER = "root";
    private final String DB_PASS = "Chanchal@896";
    private Runnable onLoginSuccess;

    public void setLoginSuccessHandler(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    @FXML
    public void handleLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()){
            errorLabel.setText("Please enter both username and password.");
            return;
        }
        if (validateLogin(username, password)) {
            errorLabel.setText("Login successful!");
                onLoginSuccess.run();

        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }

    public boolean validateLogin(String username, String password){
        String query = "SELECT Password FROM user WHERE Username = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("Password");
                return password.equals(storedPassword);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            errorLabel.setText("Database error.");
        }
        return false;
    }

    public void handleSignup(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Signup.fxml"));
            Parent signupRoot = loader.load();
            Scene signupScene = new Scene(signupRoot);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(signupScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
