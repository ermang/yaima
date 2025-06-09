package com.eg.yaima.cli.client;

import com.eg.yaima.Constant;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

public class LoginWindow {

    private final CLI cli;
    private final ClientConnection clientConnection;

    public LoginWindow(CLI cli, ClientConnection clientConnection) {
        this.cli = cli;
        this.clientConnection = clientConnection;
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
            boolean loginResult = clientConnection.login(username);
            if (loginResult)
                cli.setNextWindow(Constant.WINDOW.MAIN);
            window.close();
        });
        loginButton.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.END));

        contentPanel.addComponent(loginButton);

        contentPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));
        contentPanel.addComponent(new Button("exit", window::close).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.END)));

        window.setComponent(contentPanel);

        return window;
    }
}
