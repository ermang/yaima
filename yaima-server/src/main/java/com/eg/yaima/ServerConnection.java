package com.eg.yaima;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerConnection {
    private ServerSocket serverSocket;

    private int port;

    public ServerConnection(int port) {
        this.port = port;
    }

    public void doIt() throws IOException {

        serverSocket = new ServerSocket(port);

        while (true) {
            Socket socket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socket);
            new Thread(clientHandler).start();
        }






    }
}
