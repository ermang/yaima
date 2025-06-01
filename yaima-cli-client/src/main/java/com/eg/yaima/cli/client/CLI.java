package com.eg.yaima.cli.client;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class CLI {
//    private  TextGUIThreadFactory textGUIThreadFactory;

    public CLI() {
//        textGUIThreadFactory = new SeparateTextGUIThread.Factory();
    }

    public void doIt() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        screen = terminalFactory.createScreen();
        screen.startScreen();


        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(/*textGUIThreadFactory,*/
                screen);

        final Window window = new BasicWindow("YAIMA");

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
        contentPanel.addComponent(
                new TextBox()
                        .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));

        contentPanel.addComponent(new Label("password:"));
        contentPanel.addComponent(
                new TextBox()
                        .setMask('*')
                        .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));

        contentPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));

        Button signInButton = new Button("sign in", () -> {
            System.out.println("button clicked");
        });
        signInButton.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.END));

        contentPanel.addComponent(signInButton);

        contentPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));
        contentPanel.addComponent(new Button("exit", window::close).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.END)));

//            /*
//            While we are not going to demonstrate all components here, here is an example of combo-boxes, one that is
//            read-only and one that is editable.
//             */
//        contentPanel.addComponent(new Label("Read-only Combo Box (forced size)"));
//        List<String> timezonesAsStrings = new ArrayList<>(Arrays.asList(TimeZone.getAvailableIDs()));
//        ComboBox<String> readOnlyComboBox = new ComboBox<>(timezonesAsStrings);
//        readOnlyComboBox.setReadOnly(true);
//        readOnlyComboBox.setPreferredSize(new TerminalSize(20, 1));
//        contentPanel.addComponent(readOnlyComboBox);
//
//        contentPanel.addComponent(new Label("Editable Combo Box (filled)"));
//        contentPanel.addComponent(
//                new ComboBox<>("Item #1", "Item #2", "Item #3", "Item #4")
//                        .setReadOnly(false)
//                        .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)));
//
//            /*
//            Some user interactions, like buttons, work by registering callback methods. In this example here, we're
//            using one of the pre-defined dialogs when the button is triggered.
//             */
//        contentPanel.addComponent(new Label("Button (centered)"));
//        contentPanel.addComponent(new Button("Button", () -> MessageDialog.showMessageDialog(textGUI, "MessageBox", "This is a message box", MessageDialogButton.OK)).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));
//
//            /*
//            Close off with an empty row and a separator, then a button to close the window
//             */
//        contentPanel.addComponent(
//                new EmptySpace()
//                        .setLayoutData(
//                                GridLayout.createHorizontallyFilledLayoutData(2)));
//        contentPanel.addComponent(
//                new Separator(Direction.HORIZONTAL)
//                        .setLayoutData(
//                                GridLayout.createHorizontallyFilledLayoutData(2)));
//        contentPanel.addComponent(
//                new Button("Close", window::close).setLayoutData(
//                        GridLayout.createHorizontallyEndAlignedLayoutData(2)));

            /*
            We now have the content panel fully populated with components. A common mistake is to forget to attach it to
            the window, so let's make sure to do that.
             */
        window.setComponent(contentPanel);

            /*
            Now the window is created and fully populated. As discussed above regarding the threading model, we have the
            option to fire off the GUI here and then later on decide when we want to stop it. In order for this to work,
            you need a dedicated UI thread to run all the GUI operations, usually done by passing in a
            SeparateTextGUIThread object when you create the TextGUI. In this tutorial, we are using the conceptually
            simpler SameTextGUIThread, which essentially hijacks the caller thread and uses it as the GUI thread until
            some stop condition is met. The absolutely simplest way to do this is to simply ask lanterna to display the
            window and wait for it to be closed. This will initiate the event loop and make the GUI functional. In the
            "Close" button above, we tied a call to the close() method on the Window object when the button is
            triggered, this will then break the even loop and our call finally returns.
             */
        textGUI.addWindowAndWait(window);

        screen.stopScreen();
    }
}
