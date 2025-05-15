package com.example.something;

import javafx.application.Platform;
import javax.crypto.spec.IvParameterSpec;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client  {

    public Socket cs;
    public BufferedReader br;
    public PrintWriter pw;
    public String name;
    private HelloController controller;


    public Client(Socket socket, HelloController controller) {
        try {
            this.cs = socket;
            this.controller = controller;
            br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            pw = new PrintWriter(cs.getOutputStream(), true);

            Read();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendUsername(String username) {
        if (pw != null) {
            this.name = username;
            pw.println(username);
            Platform.runLater(() -> controller.updateChat(username + " Connected"));
        }
    }

    public void Read() {
        Runnable r1 = () -> {
            try {
                while (!cs.isClosed()) {
                    String re = br.readLine();
                    if (re.equals("exit")) {
                        System.out.println(name+" has left the chat");
                        close();
                        break;
                    }
                    else if (re.startsWith("Server:")) {
                        Platform.runLater(() -> controller.updateChat(re));
                    }
                    else {
                        String[] parts = re.split("\u001F");
                        if(parts.length == 4){
                            String uname = parts[0];
                            String encrmsg = parts[1];
                            String encMethod = parts[2];
                            String decrypted;
                            switch (encMethod){
                                case "ROT13":
                                    decrypted = ROT13.decrypt(encrmsg);
                                    break;
                                case "CAESAR":
                                    decrypted = CaesarCipher.decrypt(encrmsg);
                                    break;
                                case "RailFenceCipher":
                                    decrypted = RailFenceCipher.decrypt(encrmsg);
                                    break;
                                case "ColumnarTranspositionCipher":
                                    decrypted = ColumnarTranspositionCipher.decrypt(encrmsg);
                                    break;
                                case "VigenereCipher":
                                    decrypted = VigenereCipher.decrypt(encrmsg);
                                    break;
                                default:
                                    decrypted = encrmsg;
                            }
                                Platform.runLater(() -> controller.updateChat(uname+": "+decrypted));
                        }
                        else {
                            Platform.runLater(() -> controller.updateChat(re));
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Connection is closed");
            }
        };
        new Thread(r1).start();
    }

    public void Write(String name,String encmsg, String encMethod, String receipent) {
        Runnable r1 = () -> {
            if (pw != null) {
                try {
                    char delimiter = '\u001F';
                    String data = name + delimiter + encmsg + delimiter + encMethod + delimiter + receipent;
                    pw.println(data);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Cannot send empty message or writer is null.");
            }
        };
        new Thread(r1).start();
    }

    public void close() {
        if (pw != null) {
            pw.println("exit");
        }
        try {
            if (pw != null) {
                pw.close();
            }
            if (br != null) {
                br.close();
            }
            if (pw != null && br != null) {
                cs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
