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
import javafx.scene.layout.HBox;

public class MainSceneController implements UIHandler {

    private ClientConnection clientConnection;

    @FXML
    private ListView<HBox> buttonListView;

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
                friendExists = true;
                break;
            }
        }

        if (!friendExists) {
            Label statusLabel = new Label("  ");
            statusLabel.setStyle(f.userStatus == UserStatus.ONLINE ? "-fx-background-color: green;" : "-fx-background-color: gray;");
            Button friendButton = new Button(f.username);
            friendButton.setDisable(f.userStatus == UserStatus.OFFLINE);
            HBox friendRow = new HBox(10, statusLabel, friendButton);
            buttonListView.getItems().add(friendRow);
        }

    }

    public void updateChat(SendMessageCommand sendMessageCommand) {


    }
}
