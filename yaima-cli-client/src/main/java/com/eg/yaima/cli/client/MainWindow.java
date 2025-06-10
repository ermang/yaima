package com.eg.yaima.cli.client;

import com.eg.yaima.SendMessageCommand;
import com.eg.yaima.UserStatus;
import com.eg.yaima.client.Friend;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainWindow {
    private final ClientConnection clientConnection;
    private final Map<String, List<String>> friendChatHistory;
    private String activeChat;
    private Label activeChatLabel;
    private TextBox chatTextBox;
    private TextBox sendTextBox;
    private Panel friendPanel;
    private Panel friendListPanel;

    public MainWindow(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
        friendChatHistory = new HashMap<>();
    }

    public Window getMainWindow() {
        Window window = new BasicWindow("YAIMA");

        Panel parentPanel = new Panel(new GridLayout(2));

        GridLayout gridLayout = (GridLayout)parentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);
        gridLayout.setVerticalSpacing(1);

        Panel chatPanel = new Panel(new GridLayout(1));

        GridLayout chatPanelLayout = (GridLayout)chatPanel.getLayoutManager();
        chatPanelLayout.setHorizontalSpacing(3);
        chatPanelLayout.setVerticalSpacing(1);

        activeChatLabel = new Label("Active Chat: ");
        activeChatLabel.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.FILL, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
                GridLayout.Alignment.FILL, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
                true,       // Give the component extra horizontal space if available
                true,        // Give the component extra vertical space if available
                1,                  // Horizontal span
                1));
        chatPanel.addComponent(activeChatLabel);

        chatTextBox = new TextBox("""
                you: hi
                dummy: hi
                you: bye
                dummy: bye
                """, TextBox.Style.MULTI_LINE);

        chatTextBox.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.FILL, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
                GridLayout.Alignment.FILL, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
                true,       // Give the component extra horizontal space if available
                true,        // Give the component extra vertical space if available
                2,                  // Horizontal span
                1));                  // Vertical
        chatPanel.addComponent(chatTextBox);



        sendTextBox = new TextBox("select a friend from 'Friends' and start chatting");

        chatPanel.addComponent(sendTextBox);

        friendPanel = new Panel(new GridLayout(1));
        Label friendLabel = new Label("Friends");
        friendPanel.addComponent(friendLabel);

        friendListPanel = new Panel(new GridLayout(2));

        Label statusLabel = new Label(" ");
        statusLabel.setBackgroundColor(TextColor.ANSI.WHITE);
        statusLabel.setForegroundColor(TextColor.ANSI.WHITE);
        friendListPanel.addComponent(statusLabel);

        Button friendButton = new Button("dummy-friend");
        friendListPanel.addComponent(friendButton);

        friendPanel.addComponent(friendListPanel);

        parentPanel.addComponent(chatPanel);
        parentPanel.addComponent(friendPanel);


        window.setComponent(parentPanel);

        return window;
    }

    public void updateFriendListPanel(Friend f) {

        boolean friendExistsInPanel = false;

        for (int i = 0; i < friendListPanel.getChildrenList().size(); i++) {
            Component c = friendListPanel.getChildrenList().get(i);
            if (c instanceof Button && ((Button) c).getLabel().equals(f.username)) {
                Button friendButton = (Button) friendListPanel.getChildrenList().get(i);
                friendButton.setEnabled(f.userStatus == UserStatus.ONLINE);
                Label statusLabel = (Label) friendListPanel.getChildrenList().get(i - 1);
                statusLabel.setBackgroundColor(f.userStatus == UserStatus.ONLINE ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
                statusLabel.setForegroundColor(f.userStatus == UserStatus.ONLINE ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
                friendExistsInPanel = true;
                break;
            }
        }

        if (!friendExistsInPanel) {
            Label statusLabel = new Label(" ");
            statusLabel.setBackgroundColor(f.userStatus == UserStatus.ONLINE ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
            statusLabel.setForegroundColor(f.userStatus == UserStatus.ONLINE ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
            friendListPanel.addComponent(statusLabel);

            Button friendButton = new Button(f.username);
            friendButton.addListener(button -> {
                        System.out.println("selected friend " + f.username);
                        activeChat = f.username;
                        reloadChatFromHistory();

                        sendTextBox.setText("");
                        sendTextBox.takeFocus();

                        activeChatLabel.setText("Active Chat: " + f.username);

                        chatTextBox.setInputFilter((interactable, keyStroke) -> { //TODO: this should always be active not related to friend button interaction
                            switch (keyStroke.getKeyType()) {
                                case Tab:
                                case ReverseTab:
                                case ArrowUp:
                                case ArrowDown:
                                case ArrowLeft:
                                case ArrowRight:
                                case PageUp:
                                case PageDown:
                                    return true; // Allow navigation
                                default:
                                    return false; // Block everything else (e.g., typing, Enter, Backspace)
                            }
                        });


                        sendTextBox.setInputFilter((interactable, key) -> {
                            if (key.getKeyType() == KeyType.Enter) {
                                System.out.println("enter pressed");
                                SendMessageCommand smc = new SendMessageCommand(clientConnection.getUsername(), friendButton.getLabel(), sendTextBox.getText());

                                chatTextBox.addLine("you: " + sendTextBox.getText());
                                updateFriendChatHistory(activeChat, "you: " + sendTextBox.getText());

                                chatTextBox.setCaretPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);

                                sendTextBox.setText("");

                                clientConnection.sendMessage(smc);
                                return false;
                            }
                            return true;
                        });
                    }
            );

            friendListPanel.addComponent(friendButton);
        }

    }

    private void reloadChatFromHistory() {
        List<String> chatHistory = friendChatHistory.get(activeChat);

        if (chatHistory == null)
            chatTextBox.setText("");
        else {
            chatTextBox.setText("");
            for (String s : chatHistory)
                chatTextBox.addLine(s);

            chatTextBox.setCaretPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
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

    public void tryUpdateChat(SendMessageCommand sendMessageCommand) {
        if (activeChat != null && activeChat.equals(sendMessageCommand.from)) {
            chatTextBox.addLine(sendMessageCommand.from + ": " + sendMessageCommand.message);
            chatTextBox.setCaretPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        updateFriendChatHistory(sendMessageCommand.from, sendMessageCommand.from + ": " + sendMessageCommand.message);
    }
}
