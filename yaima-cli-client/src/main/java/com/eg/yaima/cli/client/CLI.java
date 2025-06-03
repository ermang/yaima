package com.eg.yaima.cli.client;

import com.eg.yaima.SendMessageCommand;
import com.eg.yaima.UserStatus;
import com.eg.yaima.client.Friend;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class CLI {
    private final ClientConnection clientConnection;
    private WindowBasedTextGUI textGUI;
    private Panel friendPanel;
    private Panel friendListPanel;
    private TextBox chatTextBox;
    private TextBox sendTextBox;

    public CLI(ClientConnection clientConnection) {

        this.clientConnection = clientConnection;
    }

    public void doIt() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        screen = terminalFactory.createScreen();
        screen.startScreen();


        textGUI = new MultiWindowTextGUI(screen);

        textGUI.addWindowAndWait(getLoginWindow());
        textGUI.addWindowAndWait(getMainWindow());

        screen.stopScreen();
    }

    public WindowBasedTextGUI getGUI() {
        return textGUI;
    }

    private Window getMainWindow() {
        Window window = new BasicWindow("YAIMA");

        Panel contentPanel = new Panel(new GridLayout(2));

        GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);
        gridLayout.setVerticalSpacing(1);

        chatTextBox = new TextBox("""
                you: hi
                dummy: hi
                you: bye
                dummy: bye
                """, TextBox.Style.MULTI_LINE);
        chatTextBox.setReadOnly(true);
        chatTextBox.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.FILL, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
                GridLayout.Alignment.FILL, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
                true,       // Give the component extra horizontal space if available
                true,        // Give the component extra vertical space if available
                1,                  // Horizontal span
                1));                  // Vertical
        contentPanel.addComponent(chatTextBox);

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
        contentPanel.addComponent(friendPanel);

        sendTextBox = new TextBox("select a friend from 'Friends' and start chatting");
        contentPanel.addComponent(sendTextBox);

        window.setComponent(contentPanel);

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
        friendListPanel.removeAllComponents();

        Label statusLabel = new Label(" ");
        statusLabel.setBackgroundColor(f.userStatus == UserStatus.ONLINE ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
        statusLabel.setForegroundColor(f.userStatus == UserStatus.ONLINE ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE);
        friendListPanel.addComponent(statusLabel);

        Button friendButton = new Button(f.username);
        friendButton.addListener(button -> {
                    System.out.println("selected friend " + f.username);

                    sendTextBox.setInputFilter((interactable, key) -> {
                        if (key.getKeyType() == KeyType.Enter) {
                            System.out.println("enter pressed");
                            SendMessageCommand smc = new SendMessageCommand("dummy", friendButton.getLabel(), sendTextBox.getText());

                            chatTextBox.addLine("you: " + sendTextBox.getText());

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

    public void updateChat(SendMessageCommand sendMessageCommand) {
        chatTextBox.addLine(sendMessageCommand.from + ": " + sendMessageCommand.message);
    }
}

//
//// Move caret to end to auto-scroll
//int lastLine = logBox.getLineCount() - 1;
//                    logBox.setCaretPosition(lastLine, 0);