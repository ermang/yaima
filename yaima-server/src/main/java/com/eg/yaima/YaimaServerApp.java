package com.eg.yaima;

import java.io.IOException;

public class YaimaServerApp {

    public static void main(String[] args) throws IOException {
        ServerConnection serverConnection = new ServerConnection(8080);
        serverConnection.start();
    }
}