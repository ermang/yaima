package com.eg.yaima.cli.client;

import com.eg.yaima.client.ClientConnection;

import java.io.IOException;

public class YaimaCliClientApp {

    public static void main(String[] args) throws IOException, InterruptedException {

        ClientConnection clientConnection = new ClientConnection("127.0.0.1", 8080);


        Thread thread = new Thread(clientConnection);
        thread.start();

        CLI cli = new CLI(clientConnection);
        cli.doIt();

        System.out.println("cli exited");

    }
}