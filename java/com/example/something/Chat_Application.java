package com.example.something;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.net.Socket;

public class Chat_Application extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent loginRoot = loginLoader.load();
        Scene loginScene = new Scene(loginRoot);

        LoginController loginController = loginLoader.getController();
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

                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        stage.setTitle("Chat Client-Login");
        stage.setScene(loginScene);
        stage.show();
    }
    public static void main (String[]args){
        launch(args);
    }}
