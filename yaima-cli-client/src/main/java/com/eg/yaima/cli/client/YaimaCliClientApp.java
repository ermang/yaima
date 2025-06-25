package com.eg.yaima.cli.client;

import java.io.IOException;

public class YaimaCliClientApp {

    public static void main(String[] args) throws IOException {

        CLIClientConnection CLIClientConnection = new CLIClientConnection("127.0.0.1", 8080);


        Thread thread = new Thread(CLIClientConnection);


        CLI cli = new CLI(CLIClientConnection);
        CLIClientConnection.setUIHandler(cli);

        thread.start();
        cli.run();

        cli.stopClientConnection();

        System.out.println("cli exited");

    }
}