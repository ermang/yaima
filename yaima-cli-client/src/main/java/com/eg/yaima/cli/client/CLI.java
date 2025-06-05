package com.eg.yaima.cli.client;

import com.eg.yaima.SendMessageCommand;
import com.eg.yaima.UserStatus;
import com.eg.yaima.client.Friend;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CLI {
    private final ClientConnection clientConnection;
    private WindowBasedTextGUI textGUI;
    private Panel friendPanel;
    private Panel friendListPanel;
    private TextBox chatTextBox;
    private TextBox sendTextBox;
    private Window window;
    private Label activeChatLabel;
    private String activeChat;
    private final Map<String, List<String>> friendChatHistory;

    public CLI(ClientConnection clientConnection) {

        this.clientConnection = clientConnection;
        friendChatHistory = new HashMap<>();
    }

    public void doIt() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        screen = terminalFactory.createScreen();
        screen.startScreen();


        textGUI = new MultiWindowTextGUI(screen);

        window = getLoginWindow();
        textGUI.addWindowAndWait(window);
        window = getMainWindow();
        textGUI.addWindowAndWait(window);

        screen.stopScreen();
    }

    public WindowBasedTextGUI getGUI() {
        return textGUI;
    }

    private Window getMainWindow() {
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

    public Window getLoginWindow() {
        Window window = new BasicWindow("YAIMA");

        Panel contentPanel = new Panel(new GridLayout(2));

        GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);
        gridLayout.setVerticalSpacing(1);

        Label usernameLabel = new Label("username:");
        usernameLabel.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.BEGINNING, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
                GridLayout.Alignment.BEGINNING, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
                true,       // Give the component extra horizontal space if available
                false,        // Give the component extra vertical space if available
                1,                  // Horizontal span
                1));                  // Vertical span
        contentPanel.addComponent(usernameLabel);

        TextBox usernameTextBox =  new TextBox();

        usernameTextBox.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER));
        contentPanel.addComponent(usernameTextBox);

        contentPanel.addComponent(new Label("password:"));
        contentPanel.addComponent(
                new TextBox()
                        .setMask('*')
                        .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));

        contentPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));

        Button loginButton = new Button("sign in", () -> {
            System.out.println("button clicked");
            String username = usernameTextBox.getText();
            clientConnection.login(username);
            window.close();
        });
        loginButton.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.END));

        contentPanel.addComponent(loginButton);

        contentPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));
        contentPanel.addComponent(new Button("exit", window::close).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.END)));

        window.setComponent(contentPanel);

        return window;
    }

    public void updateFriendListPanel(Friend f) {
        //friendListPanel.removeAllComponents();

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

    public void updateChat(SendMessageCommand sendMessageCommand) {
        if (activeChat != null && activeChat.equals(sendMessageCommand.from)) {
            chatTextBox.addLine(sendMessageCommand.from + ": " + sendMessageCommand.message);
            chatTextBox.setCaretPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
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
}