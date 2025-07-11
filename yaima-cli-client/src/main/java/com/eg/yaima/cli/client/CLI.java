package com.eg.yaima.cli.client;

import com.eg.yaima.common.Constant;
import com.eg.yaima.common.SendMessageCommand;
import com.eg.yaima.client.Friend;
import com.eg.yaima.common.UIHandler;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class CLI implements UIHandler {
    private final CLIClientConnection CLIClientConnection;
    private LoginWindow loginWindow;
    private final MainWindow mainWindow;
    private WindowBasedTextGUI textGUI;
    private Window window;
    private Constant.WINDOW nextWindow = null;

    public CLI(CLIClientConnection CLIClientConnection) {

        this.CLIClientConnection = CLIClientConnection;

        this.mainWindow = new MainWindow(CLIClientConnection);
    }

    public void run() throws IOException {
        this.loginWindow = new LoginWindow(this, CLIClientConnection);

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = terminalFactory.createScreen();
        screen.startScreen();

        textGUI = new MultiWindowTextGUI(screen);

        window = loginWindow.getLoginWindow();
        textGUI.addWindowAndWait(window);

        if (nextWindow == Constant.WINDOW.MAIN) {
            window = mainWindow.getMainWindow();
            textGUI.addWindowAndWait(window);
        }

        screen.stopScreen();
    }

    public WindowBasedTextGUI getGUI() {
        return textGUI;
    }

    public void updateFriendListPanel(Friend f) {

        mainWindow.updateFriendListPanel(f);
    }

    public void updateChat(SendMessageCommand sendMessageCommand) {

        mainWindow.tryUpdateChat(sendMessageCommand);
    }

    public Constant.WINDOW getNextWindow() {
        return nextWindow;
    }

    public void setNextWindow(Constant.WINDOW nextWindow) {
        this.nextWindow = nextWindow;
    }

    public void stopClientConnection() {
        CLIClientConnection.stop();
    }

    public void showErrorPopup() {
        MessageDialogButton mdb = MessageDialog.showMessageDialog(textGUI, "ERROR", "something funny going on, try later", MessageDialogButton.OK);

        nextWindow = null;
        window.close();
    }

}