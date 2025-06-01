package com.eg.yaima;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerConnection {
    private ServerSocket serverSocket;
    private Socket socket;
    private int port;

    public ServerConnection(int port) {
        this.port = port;
    }

    public void start() throws IOException {

        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();

         String remoteIp = socket.getInetAddress().getHostAddress();
         int remotePort = socket.getPort();


        byte[] tempArr = socket.getInputStream().readNBytes(Constant.MAX_USERNAME_LEN);

        String username = new String(tempArr, Constant.CHARSET).trim();
        System.out.println(username);

        //TODO: do the login
        //assume login is successful

        //TODO: get friends of logged in user
        //TODO: check their statuses (online/offline) and send this data to logged in user

        if (username.equals("alice")) {
            String temp = "STT" + "ONL";
            temp = temp + "bob";

            short x = (short) temp.length();
            byte[] bytes = ByteBuffer.allocate(2).putShort(x).array();

            socket.getOutputStream().write(bytes);

            socket.getOutputStream().write(temp.getBytes(Constant.CHARSET));

        }




    }
}
