package com.eg.yaima.client;

import java.util.Scanner;

public class ClientCommandLineProcessor {

    private final Scanner scanner ;

    public ClientCommandLineProcessor() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("YamaClientApp started");

        System.out.println("""
                *****
                1- Show Friend List
                2- Something Something
                3- Exit
                *****
                """);
    }
}
