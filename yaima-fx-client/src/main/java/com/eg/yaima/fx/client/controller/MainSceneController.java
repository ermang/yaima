package com.eg.yaima.fx.client.controller;

import com.eg.yaima.client.Friend;
import com.eg.yaima.common.ClientConnection;
import com.eg.yaima.common.SendMessageCommand;
import com.eg.yaima.common.UIHandler;
import com.eg.yaima.common.UserStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainSceneController implements UIHandler {

    private ClientConnection clientConnection;
    private String activeChat;
    private Map<String, List<String>> friendChatHistory;

    @FXML
    private ListView<HBox> buttonListView;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private TextArea sendTextArea;

    @FXML
    public void initialize() {
        friendChatHistory = new HashMap<>();

        sendTextArea.setOnKeyPressed(event -> {
            if (activeChat != null && event.getCode() == KeyCode.ENTER) {
                System.out.println("Enter key pressed!");
                event.consume(); // optional: prevent newline from being inserted

                SendMessageCommand smc = new SendMessageCommand(clientConnection.getUsername(), activeChat, sendTextArea.getText());
                clientConnection.sendMessage(smc);

                updateFriendChatHistory(activeChat, "you: " + sendTextArea.getText());
                chatTextArea.appendText("you: " + sendTextArea.getText()+ "\n");
                sendTextArea.setText("");
            }
        });
    }

    public void setClientConnection(ClientConnection cc) {
        this.clientConnection = cc;
    }

    public void updateFriendListPanel(Friend f) {

        boolean friendExists = false;

        for(HBox hbox: buttonListView.getItems()) {
            Button existingFriendButton = (Button) hbox.getChildren().get(1);
            if (f.username.equals(existingFriendButton.getText())) {
                Label existingFriendLabel = (Label) hbox.getChildren().get(0);
                existingFriendLabel.setStyle(f.userStatus == UserStatus.ONLINE ? "-fx-background-color: green;" : "-fx-background-color: gray;" );
                existingFriendButton.setDisable(f.userStatus == UserStatus.OFFLINE);
                friendExists = true;
                break;
            }
        }

        if (!friendExists) {
            Label statusLabel = new Label("  ");
            statusLabel.setStyle(f.userStatus == UserStatus.ONLINE ? "-fx-background-color: green;" : "-fx-background-color: gray;");
            Button friendButton = new Button(f.username);
            friendButton.setDisable(f.userStatus == UserStatus.OFFLINE);

            friendButton.setOnAction(event -> {
                activeChat = f.username;
                reloadChatFromHistory();
                sendTextArea.setText("");
            });

            HBox friendRow = new HBox(10, statusLabel, friendButton);
            buttonListView.getItems().add(friendRow);
        }

    }

    public void updateChat(SendMessageCommand sendMessageCommand) {
        if (activeChat != null && activeChat.equals(sendMessageCommand.from)) {
            chatTextArea.appendText(sendMessageCommand.from + ": " + sendMessageCommand.message + "\n");
        }

        updateFriendChatHistory(sendMessageCommand.from, sendMessageCommand.from + ": " + sendMessageCommand.message);
    }

    private void updateFriendChatHistory(String from, String message) {
        List<String> chatHistory = friendChatHistory.get(from);

        if (chatHistory == null) {
            chatHistory = new ArrayList<>();
            friendChatHistory.put(from, chatHistory);
        }

        chatHistory.add(message);
    }

    private void reloadChatFromHistory() {
        List<String> chatHistory = friendChatHistory.get(activeChat);

        if (chatHistory == null)
            chatTextArea.setText("");
        else {
            chatTextArea.setText("");
            for (String s : chatHistory)
                chatTextArea.appendText(s + "\n");
        }
    }
}
