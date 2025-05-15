package com.example.something;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Client_Handler implements Runnable {
    public static ArrayList<Client_Handler> Client_List = new ArrayList<>();

    private String username;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client_Handler(Socket s){

        try {
            this.socket = s;
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.username = in.readLine();
            broadcast("Server:"+username+" is connected");
            Client_List.add(this);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void broadcast(String message){
        for(Client_Handler c : Client_List){
            if( !c.username.equals(this.username)){
                c.out.println(message);

            }
        }
    }
    private void closeConnection() {
        try {
            Client_List.remove(this);
            broadcast("Server: " + username + " has left the chat.");
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("exit")) {
                    closeConnection();
                    break;
                }

                String[] parts = message.split("\\|");
                if (parts.length >= 4) {
                    String receiver = parts[3];
                    String encryptedMsg = parts[1];
                    String encMethod = parts[2];
                    String sender = parts[0];

                    storeOfflineMessage(sender,receiver,encryptedMsg,encMethod);

                    for (Client_Handler client : Client_List) {
                        if (client.username.equals(receiver)) {
                            client.out.println(message);
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Client_List.remove(this);
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void storeOfflineMessage(String sender, String receiver, String message,String encMethod) {
        String DB_URL = "jdbc:mysql://localhost:3306/chat_app";
        String DB_USER = "root";
        String DB_PASS = "Chanchal@896";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "INSERT INTO message (sender, receiver, message, encmethod,msgtime) VALUES (?, ?, ?, ?,NOW())";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, sender);
            pst.setString(2, receiver);
            pst.setString(3, message);
            pst.setString(4, encMethod);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
