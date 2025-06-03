package com.eg.yaima;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionAcceptor implements Runnable {
    private ServerSocket serverSocket;
    private final YaimaServer yaimaServer;

    private int port;

    public ConnectionAcceptor(int port, YaimaServer yaimaServer) {
        this.port = port;
        this.yaimaServer = yaimaServer;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            Socket socket = null;

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ClientHandler clientHandler = new ClientHandler(socket, yaimaServer);

            new Thread(clientHandler).start();
        }
    }
}
