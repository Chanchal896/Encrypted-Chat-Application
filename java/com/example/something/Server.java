package com.example.something;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(){
        try {
            serverSocket = new ServerSocket(7777);
            System.out.println("Server is running");

            while(true){
                Socket s=serverSocket.accept();
                System.out.println("Client connected");

                Client_Handler  ch = new Client_Handler(s);

                Thread t = new Thread(ch);
                t.start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
