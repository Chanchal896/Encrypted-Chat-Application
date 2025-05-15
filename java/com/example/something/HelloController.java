package com.example.something;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloController {
    @FXML public Button Send_Btn;
    @FXML private TextField Write_text;
    @FXML private TextArea Chat_display;
    @FXML private ToggleButton Tgl;
    @FXML private ComboBox<String> Combo;
    @FXML private ListView<User> userListView;
    @FXML private ObservableList<User> users = FXCollections.observableArrayList();
    @FXML private User selectedUser;
    @FXML  private Map<String, String> chatHistories = new HashMap<>();
    @FXML private Client client;
    @FXML private String username;
    @FXML private String CurrText = "";
    @FXML private boolean currentlyShowingEncrypted = false;
    @FXML private Label labeluser;
    public HelloController() throws Exception {

    }


    public void setClient(Client client) {
        this.client = client;
        if (this.username != null) {
            client.sendUsername(this.username);
        }
    }

    public void setUsername(String username) {
        this.username=username;
        if (client != null) {
            client.sendUsername(username);

        }
        labeluser.setText("User logged in:"+username);
        loadUsersFromDatabase();
        setupUserList();
    }

    @FXML
    public void initialize() {
        Chat_display.setText("Select a user from the list to chat with.\n");
        Combo.getItems().addAll( "ROT13", "CAESAR","RailFenceCipher","ColumnarTranspositionCipher","VigenereCipher");
        Combo.setValue("ROT13");

        Timeline userRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            loadUsersFromDatabase();}));
        userRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        userRefreshTimeline.play();
        

    }

    private void setupUserList() {
        userListView.setItems(users);
        userListView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    ImageView imageView = new ImageView(
                            new Image(getClass().getResourceAsStream("/images/user_icon.png")));
                    imageView.setFitHeight(30);
                    imageView.setFitWidth(30);

                    Label nameLabel = new Label(user.getUsername());
                    nameLabel.setStyle(user.isOnline() ? "-fx-text-fill: green;" : "-fx-text-fill: gray;");

                    hbox.getChildren().addAll(imageView, nameLabel);
                    setGraphic(hbox);
                }
            }
        });

        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedUser = newVal;
            loadChatHistory(newVal.getUsername());
        });
    }

    private void loadChatHistory(String username) {
        Chat_display.clear();
        chatHistories.remove(username);

        String DB_URL = "jdbc:mysql://localhost:3306/chat_app";
        String DB_USER = "root";
        String DB_PASS = "Chanchal@896";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT * FROM message WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, this.username);
            pst.setString(2, username);
            pst.setString(3, username);
            pst.setString(4, this.username);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String msg = rs.getString("message");
                String encmethod = rs.getString("encmethod");
                String sender = rs.getString("sender");

                String decrypted;
                switch (encmethod) {
                    case "ROT13":
                        decrypted = ROT13.decrypt(msg);
                        break;
                    case "CAESAR":
                        decrypted = CaesarCipher.decrypt(msg);
                        break;
                    case "RailFenceCipher":
                        decrypted = RailFenceCipher.decrypt(msg);
                        break;
                    case "ColumnarTranspositionCipher":
                        decrypted = ColumnarTranspositionCipher.decrypt(msg);
                        break;
                    case "VigenereCipher":
                        decrypted = VigenereCipher.decrypt(msg);
                        break;
                    default:
                        decrypted = msg;
                }
                Chat_display.appendText(sender + ": " + decrypted + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUsersFromDatabase() {
        String DB_URL = "jdbc:mysql://localhost:3306/chat_app";
        String DB_USER = "root";
        String DB_PASS = "Chanchal@896";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT Username FROM user WHERE Username != ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, this.username);
            ResultSet rs = pst.executeQuery();

            List<User> userList = new ArrayList<>();
            while (rs.next()) {
                userList.add(new User(rs.getString("Username"), false));
            }
            User selected = userListView.getSelectionModel().getSelectedItem();
            users.setAll(userList);
            if (selected != null) {
                for (User user : users) {
                    if (user.getUsername().equals(selected.getUsername())) {
                        userListView.getSelectionModel().select(user);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void updateChat(String message) {
        if (selectedUser != null) {
            Chat_display.appendText(message + "\n");
        }
    }

    @FXML
    public void onEnter(ActionEvent e) throws Exception {
        onHelloButtonClick(e);
    }

    public void onToggle() {
        String selectedEnc = Combo.getValue();

        if (Tgl.isSelected()) {
            Write_text.setEditable(false);
            Tgl.setText("Encrypted");
            CurrText = Write_text.getText();
            switch (selectedEnc) {
                case "ROT13":
                    Write_text.setText(ROT13.encrypt(CurrText));
                    break;
                case "CAESAR":
                    Write_text.setText(CaesarCipher.encrypt(CurrText));
                    break;
                case "RailFenceCipher":
                    Write_text.setText(RailFenceCipher.encrypt(CurrText));
                    break;
                case "ColumnarTranspositionCipher":
                    Write_text.setText(ColumnarTranspositionCipher.encrypt(CurrText));
                    break;
                case "VigenereCipher":
                    Write_text.setText(VigenereCipher.encrypt(CurrText));
                    break;
                default:
                    Write_text.setText(CurrText);
            }
            currentlyShowingEncrypted = true;
        } else {
            Tgl.setText("Encrypt");
            Write_text.setEditable(true);
            if (currentlyShowingEncrypted) {
                String encryptedText = Write_text.getText();
                switch (selectedEnc) {
                    case "ROT13":
                        Write_text.setText(ROT13.decrypt(encryptedText));
                        break;
                    case "CAESAR":
                        Write_text.setText(CaesarCipher.decrypt(encryptedText));
                        break;
                    case "RailFenceCipher":
                        Write_text.setText(RailFenceCipher.decrypt(encryptedText));
                        break;
                    case "ColumnarTranspositionCipher":
                        Write_text.setText(ColumnarTranspositionCipher.decrypt(encryptedText));
                        break;
                    case "VigenereCipher":
                        Write_text.setText(VigenereCipher.decrypt(encryptedText));
                        break;
                    default: Write_text.setText(encryptedText);
                }
            }
            currentlyShowingEncrypted = false;
        }
    }

    @FXML
    public void onHelloButtonClick(ActionEvent e) throws Exception {
        if (selectedUser == null) {
            updateChat("System: Please select a user to chat with");
            return;
        }
        if (client == null) {
            updateChat("System: Not connected to the server.");
            return;
        }
            String Message = Write_text.getText();
            String name = username;
            if (Message.equals("exit")) {
                client.close();
                System.exit(0);
            }
            if(!Message.isEmpty()){
                if(currentlyShowingEncrypted==false){
                    String selectedEnc = Combo.getValue();
                    try {
                        switch (selectedEnc) {
                            case "ROT13":
                                String ROT13enc = ROT13.encrypt(Message);
                                client.Write(name,ROT13enc, selectedEnc,selectedUser.getUsername());
                                storeMessageInDatabase(ROT13enc, selectedUser.getUsername(), selectedEnc);
                                updateChat(username+":"+Message);
                                break;
                            case "CAESAR":
                                String CaeserE = CaesarCipher.encrypt(Message);
                                client.Write(name,CaeserE, selectedEnc,selectedUser.getUsername());
                                storeMessageInDatabase(CaeserE, selectedUser.getUsername(), selectedEnc);
                                updateChat(username+":"+Message);
                                break;
                            case "RailFenceCipher":
                                String rf = RailFenceCipher.encrypt(Message);
                                client.Write(name,rf, selectedEnc,selectedUser.getUsername());
                                storeMessageInDatabase(rf, selectedUser.getUsername(), selectedEnc);
                                updateChat(username+":"+Message);
                                break;
                            case "ColumnarTranspositionCipher":
                                String ct = ColumnarTranspositionCipher.encrypt(Message);
                                client.Write(name,ct, selectedEnc,selectedUser.getUsername());
                                storeMessageInDatabase(ct, selectedUser.getUsername(), selectedEnc);
                                updateChat(username+":"+Message);
                                break;
                            case "VigenereCipher":
                                String vc = VigenereCipher.encrypt(Message);
                                client.Write(name,vc, selectedEnc,selectedUser.getUsername());
                                storeMessageInDatabase(vc, selectedUser.getUsername(), selectedEnc);
                                updateChat(username+":"+Message);
                                break;
                            default:
                                client.Write(name,Message, selectedEnc,selectedUser.getUsername());
                                storeMessageInDatabase(Message, selectedUser.getUsername(), selectedEnc);
                                updateChat(username+":"+Message);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else if(currentlyShowingEncrypted==true){
                    Message = Write_text.getText();
                    String selectedEnc = Combo.getValue();
                    try {
                        switch (selectedEnc) {
                            case "ROT13":
                                client.Write(name,Message, selectedEnc,selectedUser.getUsername());
                                storeMessageInDatabase(Message, selectedUser.getUsername(), selectedEnc);
                                String me = ROT13.decrypt(Message);
                                updateChat(username+":"+me);
                                break;
                            case "CAESAR":
                                client.Write(name,Message, selectedEnc,selectedUser.getUsername());
                                storeMessageInDatabase(Message, selectedUser.getUsername(), selectedEnc);
                                String a = CaesarCipher.decrypt(Message);
                                updateChat(username+":"+a);
                                break;
                            case "RailFenceCipher":
                                client.Write(name,Message, selectedEnc,selectedUser.getUsername());
                                storeMessageInDatabase(Message, selectedUser.getUsername(), selectedEnc);
                                String railfence = RailFenceCipher.decrypt(Message);
                                updateChat(username+":"+railfence);
                                break;
                            case "ColumnarTranspositionCipher":
                                client.Write(name,Message, selectedEnc,selectedUser.getUsername());
                                storeMessageInDatabase(Message, selectedUser.getUsername(), selectedEnc);
                                String coltran = ColumnarTranspositionCipher.decrypt(Message);
                                updateChat(username+":"+coltran);
                                break;
                            case "VigenereCipher":
                                client.Write(name,Message, selectedEnc,selectedUser.getUsername());
                                storeMessageInDatabase(Message, selectedUser.getUsername(), selectedEnc);
                                String vignci = VigenereCipher.decrypt(Message);
                                updateChat(username+":"+vignci);
                                break;
                            default:
                                client.Write(name,Message, null,selectedUser.getUsername());
                                storeMessageInDatabase(Message, selectedUser.getUsername(), selectedEnc);
                                updateChat(username+":"+Message);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                Write_text.clear();
            }
            }

    private void storeMessageInDatabase(String message, String recipient,String encmethod) {
        String DB_URL = "jdbc:mysql://localhost:3306/chat_app";
        String DB_USER = "root";
        String DB_PASS = "Chanchal@896";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "INSERT INTO message (sender, receiver, message,encmethod ,msgtime) VALUES (?, ?, ?, ?,NOW())";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, this.username);
            pst.setString(2, recipient);
            pst.setString(3, message);
            pst.setString(4, encmethod);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }

