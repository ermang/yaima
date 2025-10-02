package com.eg.yaima.fx.client.controller;

import com.eg.yaima.client.Friend;
import com.eg.yaima.common.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainSceneController implements UIHandler {



    private ClientConnection clientConnection;
    private String activeChat;
    private Map<String, List<String>> friendChatHistory;
    private Map<String, Timeline>  usernameBlinkAnimationMap;

    @FXML
    private ListView<HBox> buttonListView;
    @FXML
    private Label chatWithLabel;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private TextArea sendTextArea;
    @FXML
    private ComboBox<String> usernameComboBox;
    @FXML
    private Button sendFriendRequestButton;
    @FXML
    private ListView<HBox>  waitingRequestsListView;

    @FXML
    public void initialize() {
        friendChatHistory = new HashMap<>();
        usernameBlinkAnimationMap = new HashMap<>();

        sendTextArea.setOnKeyPressed(event -> {
            if (activeChat != null && event.getCode() == KeyCode.ENTER) {

                event.consume(); // optional: prevent newline from being inserted

                SendMessageCommand smc = new SendMessageCommand(clientConnection.getUsername(), activeChat, sendTextArea.getText());
                clientConnection.sendMessage(smc);

                updateFriendChatHistory(activeChat, "you: " + sendTextArea.getText());
                chatTextArea.appendText("you: " + sendTextArea.getText()+ "\n");
                sendTextArea.setText("");
            }
        });
    }

    @FXML
    public void onSendFriendRequest(ActionEvent actionEvent) {
        String friendRequestUsername = usernameComboBox.getValue();
        SendFriendRequestCommand sfc = new SendFriendRequestCommand(clientConnection.getUsername(), friendRequestUsername);
        clientConnection.sendFriendRequest(sfc);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Friend Request Sent");
        alert.setHeaderText(null);
        alert.setContentText("to: " + friendRequestUsername);
        alert.showAndWait();

        usernameComboBox.setValue("");
    }

    public void setClientConnection(ClientConnection cc) {
        this.clientConnection = cc;
    }

    public void updateFriendListPanel(Friend f) {

        //first is Label statusLabel
        //second is Button friendButton
        //third is blinking thingy if there are unread messages in chat
        boolean friendExists = false;

        for(HBox hbox: buttonListView.getItems()) {
            Button existingFriendButton = (Button) hbox.getChildren().get(1);
            if (f.username.equals(existingFriendButton.getText())) {
                Label existingFriendLabel = (Label) hbox.getChildren().get(0);
                existingFriendLabel.setStyle(f.userStatus == UserStatus.ONLINE ? "-fx-background-color: green;" : "-fx-background-color: gray;" );
                existingFriendButton.setDisable(f.userStatus == UserStatus.OFFLINE);
//                //
//                if (hbox.getChildren().size() == 3)
//                    hbox.getChildren().remove(hbox.getChildren().get(2));
//                //
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
                chatWithLabel.setText("--- Chat With " + activeChat + " ---");
                //
                HBox hbox = (HBox)friendButton.getParent();
                if (hbox.getChildren().size() == 3) {
                    hbox.getChildren().remove(hbox.getChildren().get(2));
                    usernameBlinkAnimationMap.remove(friendButton.getText());
                }
                //
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
        } else {
            addBlinkingLabelForUnreadMessage(sendMessageCommand);
        }

        updateFriendChatHistory(sendMessageCommand.from, sendMessageCommand.from + ": " + sendMessageCommand.message);
    }

    private void addBlinkingLabelForUnreadMessage(SendMessageCommand sendMessageCommand) {

        if (usernameBlinkAnimationMap.containsKey(sendMessageCommand.from))
            return;

        for(HBox hbox: buttonListView.getItems()) {
            Button existingFriendButton = (Button) hbox.getChildren().get(1);
            if (existingFriendButton.getText().equals(sendMessageCommand.from)) {
                //
                Label unreadLabel = new Label("!");
                unreadLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");

                Timeline blinkTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0.5), e -> unreadLabel.setVisible(!unreadLabel.isVisible()))
                );
                blinkTimeline.setCycleCount(Timeline.INDEFINITE);
                blinkTimeline.play();
                 //

                usernameBlinkAnimationMap.put(sendMessageCommand.from, blinkTimeline);
                hbox.getChildren().add(unreadLabel);
            }



        }
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


    public void updateWaitingFriendRequests(SendFriendRequestCommand sfc) {
        Label label = new Label(sfc.from);
        Button yesButton = new Button("Yes");
        yesButton.setOnAction(event -> {
            SendFriendAnswerCommand sfa = new SendFriendAnswerCommand(sfc.from, sfc.to, true);
            clientConnection.senfFriendAnswer(sfa);
            for (int i=0;i < waitingRequestsListView.getItems().size();i++) {
                HBox hbox = waitingRequestsListView.getItems().get(i);
                Label dynamicLabel = (Label) hbox.getChildren().get(0);
                if (dynamicLabel.getText().equals(sfc.from)) {
                    waitingRequestsListView.getItems().remove(i);
                    break;
                }
            }
        });
        Button noButton = new Button("No");
        noButton.setOnAction(event -> {
            SendFriendAnswerCommand sfa = new SendFriendAnswerCommand(sfc.from, sfc.to, false);
            clientConnection.senfFriendAnswer(sfa);
            for (int i=0;i < waitingRequestsListView.getItems().size();i++) {
                HBox hbox = waitingRequestsListView.getItems().get(i);
                Label dynamicLabel = (Label) hbox.getChildren().get(0);
                if (dynamicLabel.getText().equals(sfc.from)) {
                    waitingRequestsListView.getItems().remove(i);
                    break;
                }
            }
        });
        HBox hBox = new HBox(10, label, yesButton, noButton);
        waitingRequestsListView.getItems().add(hBox);
    }
}
