package com.eg.yaima.cli.client;

import com.eg.yaima.common.Constant;
import com.eg.yaima.common.SendMessageCommand;
import com.eg.yaima.common.UserStatus;
import com.eg.yaima.client.Friend;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ClientConnection implements Runnable {

    private String ip;
    private int port;
    private Socket socket;
    private CLI cli;
    private String username;

    public ClientConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

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

                    cli.getGUI().getGUIThread().invokeLater(() -> {
                        Friend f = new Friend(friend, status.equals("ONL") ? UserStatus.ONLINE : UserStatus.OFFLINE);
                        cli.updateFriendListPanel(f);
                    });
                } else if (packetType.equals("SMS")) {
                    int fromIndex = -1;
                    int toIndex = -1;

                    for (int i = 0; i < tempArr.length; i++) {
                        if (tempArr[i] == 0) {
                            fromIndex = i;
                            break;
                        }
                    }

                    for (int i = fromIndex+1; i < tempArr.length; i++) {
                        if (tempArr[i] == 0) {
                            toIndex = i;
                            break;
                        }
                    }

                    String from = new String(tempArr, 3, fromIndex - 3, Constant.CHARSET);
                    String to = new String(tempArr, fromIndex+1, toIndex-fromIndex-1, Constant.CHARSET);
                    String msg = new String(tempArr, toIndex+1, tempArr.length - toIndex -1, Constant.CHARSET);

                    cli.getGUI().getGUIThread().invokeLater(() -> {
                        cli.updateChat(new SendMessageCommand(from, to, msg));
                    });

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

        this.username = username;

        return true;
    }

    public void setCLI(CLI cli) {
        this.cli = cli;
    }

    public void sendMessage(SendMessageCommand smc) {

        byte[] packetTypeArr = "SMS".getBytes(Constant.CHARSET);
        byte[] fromArr = smc.from.getBytes(Constant.CHARSET);
        byte[] toArr = smc.to.getBytes(Constant.CHARSET);
        byte[] messageArr = smc.message.getBytes(Constant.CHARSET);

        byte[] concatenatedArr = new byte[packetTypeArr.length + fromArr.length + 1 + toArr.length + 1 + messageArr.length];

        int index = 0;

        System.arraycopy(packetTypeArr, 0, concatenatedArr, 0, packetTypeArr.length);
        index = index + packetTypeArr.length;
        System.arraycopy(fromArr, 0, concatenatedArr, index, fromArr.length);
        index = index + fromArr.length;
        concatenatedArr[index] = 0x0;
        index = index + 1;
        System.arraycopy(toArr, 0, concatenatedArr, index, toArr.length);
        index = index + toArr.length;
        concatenatedArr[index] = 0x0;
        index = index + 1;
        System.arraycopy(messageArr, 0, concatenatedArr, index, messageArr.length);

        short x = (short) concatenatedArr.length;
        byte[] bytes = ByteBuffer.allocate(2).putShort(x).array();

        try {
            socket.getOutputStream().write(bytes);
            socket.getOutputStream().write(concatenatedArr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername() {
        return username;
    }

    public void stop() {
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
