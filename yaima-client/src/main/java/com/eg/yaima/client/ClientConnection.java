package com.eg.yaima.client;

import com.eg.yaima.Constant;

import java.io.IOException;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class ClientConnection implements Runnable{


    private String ip;
    private int port;
    private Socket socket;

    public ClientConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

//    public void start() throws IOException {
//
//
//        socket = new Socket(ip, port);
//
//        System.out.println("username:");
//        Scanner scanner = new Scanner(System.in);
//        String username = scanner.nextLine();
//
//        String usernameSpacePadded = username.length() < Constant.MAX_USERNAME_LEN ? username + " ".repeat(Constant.MAX_USERNAME_LEN - username.length()) : username;
//
//        byte[] tempArr = usernameSpacePadded.getBytes(Constant.CHARSET);
//        socket.getOutputStream().write(tempArr);
//
//        byte[] msgLenArr = socket.getInputStream().readNBytes(2);
//
//        int result = ByteBuffer.wrap(msgLenArr).getShort();
//
//        tempArr = socket.getInputStream().readNBytes(result);
//
//        String packetType = new String(tempArr, 0, 3, Constant.CHARSET);
//
//        if (packetType.equals("STT")) {
//            String status = new String(tempArr, 3, 3, Constant.CHARSET);
//            String friend = new String(tempArr, 6, tempArr.length-6, Constant.CHARSET);
//            System.out.println(friend);
//
//        }
//
//
////
////        String username = new String(tempArr, Constant.CHARSET);
////        System.out.println(username);
//    }

    @Override
    public void run() {

        try {
            socket = new Socket(ip, port);

            byte[] tempArr = null;
            byte[] msgLenArr = null;

            while(true) {

                msgLenArr = socket.getInputStream().readNBytes(2);

                int result = ByteBuffer.wrap(msgLenArr).getShort();

                tempArr = socket.getInputStream().readNBytes(result);

                String packetType = new String(tempArr, 0, 3, Constant.CHARSET);

                if (packetType.equals("STT")) {
                    String status = new String(tempArr, 3, 3, Constant.CHARSET);
                    String friend = new String(tempArr, 6, tempArr.length - 6, Constant.CHARSET);
                    System.out.println(friend);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean login(String username) {

        String usernameSpacePadded = username.length() < Constant.MAX_USERNAME_LEN ? username + " ".repeat(Constant.MAX_USERNAME_LEN - username.length()) : username;

        byte[] tempArr = usernameSpacePadded.getBytes(Constant.CHARSET);
        try {
            socket.getOutputStream().write(tempArr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
