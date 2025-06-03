package com.eg.yaima;

import java.io.IOException;

public class YaimaServerApp {

    public static void main(String[] args) throws IOException {

        YaimaServer yaimaServer = new YaimaServer(8080);

        Thread t = new Thread(yaimaServer);
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}