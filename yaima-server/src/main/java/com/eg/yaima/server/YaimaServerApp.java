package com.eg.yaima.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YaimaServerApp {

    public static void main(String[] args) {

//        YaimaServer yaimaServer = new YaimaServer(8080);
//
//        Thread t = new Thread(yaimaServer);
//        t.start();
//
//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        SpringApplication.run(YaimaServerApp.class, args);
    }
}