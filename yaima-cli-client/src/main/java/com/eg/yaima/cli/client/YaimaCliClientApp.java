package com.eg.yaima.cli.client;

import java.io.IOException;

public class YaimaCliClientApp {

    public static void main(String[] args) throws IOException, InterruptedException {

        ClientConnection clientConnection = new ClientConnection("127.0.0.1", 8080);


        Thread thread = new Thread(clientConnection);


        CLI cli = new CLI(clientConnection);
        clientConnection.setCLI(cli);

        thread.start();
        cli.run();

        cli.stopClientConnection();

        System.out.println("cli exited");

    }
}