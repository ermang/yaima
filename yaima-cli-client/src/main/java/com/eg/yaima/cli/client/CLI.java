package com.eg.yaima.cli.client;

import com.eg.yaima.client.Friend;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class CLI {
    private final ClientConnection clientConnection;
    private WindowBasedTextGUI textGUI;
    private Panel friendPanel;

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

        Label chatWithLabel = new Label("Chat With Dummy");
        contentPanel.addComponent(chatWithLabel);

        friendPanel = new Panel(new GridLayout(1));
        Label friendLabel = new Label("Friends");
        friendPanel.addComponent(friendLabel);
        Button friendButton = new Button("dummy-friend");
        friendPanel.addComponent(friendButton);
        contentPanel.addComponent(friendPanel);




        TextBox chatTextBox = new TextBox("select a friend from 'Friends' and start chatting");
        contentPanel.addComponent(chatTextBox);




        window.setComponent(contentPanel);

        return window;
    }

    public Window getLoginWindow() {
        Window window = new BasicWindow("YAIMA");

        Panel contentPanel = new Panel(new GridLayout(2));

        GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);
        gridLayout.setVerticalSpacing(1);

            /*
            One of the most basic components is the Label, which simply displays a static text. In the example below,
            we use the layout data field attached to each component to give the layout manager extra hints about how it
            should be placed. Obviously the layout data has to be created from the same layout manager as the container
            is using, otherwise it will be ignored.
             */
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

    public void updateWithFriend(Friend f) {
        friendPanel.removeAllComponents();

        Label friendLabel = new Label("Friends");
        friendPanel.addComponent(friendLabel);
        Button friendButton = new Button(f.username);
        friendPanel.addComponent(friendButton);
    }
}
