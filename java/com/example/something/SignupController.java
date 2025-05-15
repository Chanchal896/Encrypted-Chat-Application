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

import java.net.Socket;
import java.sql.*;

public class SignupController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField Confirmpassword;
    @FXML private Label errorLabel;

    private final String DB_URL = "jdbc:mysql://localhost:3306/chat_app";
    private final String DB_USER = "root";
    private final String DB_PASS = "Chanchal@896";

    @FXML
    public void handleSignup(ActionEvent e) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = Confirmpassword.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill all fields.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String checkQuery = "SELECT Username FROM user WHERE Username = ?";
            try (PreparedStatement pst = con.prepareStatement(checkQuery)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    errorLabel.setText("Username already exists.");
                    return;
                }
            }

            String insertQuery = "INSERT INTO user (Username, Password) VALUES (?, ?)";
            try (PreparedStatement pst = con.prepareStatement(insertQuery)) {
                pst.setString(1, username);
                pst.setString(2, password);
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    errorLabel.setText("User Registered! Please log in.");
                } else {
                    errorLabel.setText("Signup failed. Please try again.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            errorLabel.setText("Database error: " + ex.getMessage());
        }
    }

    public void backtologin(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent loginRoot = loader.load();
            LoginController loginController = loader.getController();

            loginController.setLoginSuccessHandler(() -> {
                try {
                    String username = loginController.getUsername();

                    FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                    Parent chatRoot = chatLoader.load();
                    HelloController chatController = chatLoader.getController();
                    chatController.setUsername(username);

                    Socket s = new Socket("localhost", 7777);
                    Client client = new Client(s, chatController);
                    chatController.setClient(client);

                    Stage chatStage = new Stage();
                    chatStage.setScene(new Scene(chatRoot));
                    chatStage.setOnCloseRequest(event -> {
                        if (client != null) {
                            client.close();
                        }
                        System.exit(0);
                    });
                    chatStage.show();
                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    stage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
    }
