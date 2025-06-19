package com.eg.yaima.server.other;

import com.eg.yaima.common.Constant;
import com.eg.yaima.common.SendMessageCommand;
import com.eg.yaima.common.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.List;

public class ClientHandler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private final Socket socket;
    private final YaimaServer yaimaServer;
    private String username;

    public ClientHandler(Socket socket, YaimaServer yaimaServer) {
        this.socket = socket;
        this.yaimaServer = yaimaServer;
    }

    @Override
    public void run() {
        String remoteIp = socket.getInetAddress().getHostAddress();
        int remotePort = socket.getPort();

        byte[] tempArr = null;

        try {
            tempArr = socket.getInputStream().readNBytes(Constant.MAX_USERNAME_LEN);


            String username = new String(tempArr, Constant.CHARSET).trim();
            System.out.println(username);

            yaimaServer.addOnlineUser(username, this);
            this.username = username;

            //TODO: do the login
            //assume login is successful

            List<String> friendsOfUser = yaimaServer.getFriendsOfUser(username);
            //TODO: check their statuses (online/offline) and send this data to logged in user
            for (String s : friendsOfUser) {
                UserStatus userStatus = yaimaServer.getUserStatus(s);
                sendFriendSTT(s, userStatus);
            }

            yaimaServer.notifyFriendsOfStatusChange(username, UserStatus.ONLINE);

        } catch (IOException e) {
            LOGGER.error("ERR:", e);
            throw new RuntimeException(e);
            //TODO: bunu da handle et
        }

        //start listening from client
        while(true) {
            try {
                byte[] msgLenArr = socket.getInputStream().readNBytes(2);


                int msgLen = ByteBuffer.wrap(msgLenArr).getShort();

                tempArr = socket.getInputStream().readNBytes(msgLen);

                String packetType = new String(tempArr, 0, 3, Constant.CHARSET);

                if (packetType.equals("SMS")) {
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

                    yaimaServer.redirectChat(new SendMessageCommand(from, to, msg));
                }

            } catch (IOException | BufferUnderflowException e) { //TODO: may need to handle BufferUnderflowException somewhat better
                //throw new RuntimeException(e);
                //TODO: get online friends of this user
                //send them STT message with OFFLINE status
               LOGGER.error("ERR:", e);
               yaimaServer.notifyFriendsOfStatusChange(username, UserStatus.OFFLINE);
               yaimaServer.removeFromOnlineUsers(username);
               break;
            }
        }

    }



    public void sendFriendSTT(String username, UserStatus userStatus)  {

        String temp = "STT" + userStatus.getCode();

        temp = temp + username;

        short x = (short) temp.length();
        byte[] bytes = ByteBuffer.allocate(2).putShort(x).array();

        try {
            socket.getOutputStream().write(bytes);
            socket.getOutputStream().write(temp.getBytes(Constant.CHARSET));
        } catch (IOException e) {
            LOGGER.error("ERR:", e);
            throw new RuntimeException(e);
        }

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
            LOGGER.error("ERR:", e);
            throw new RuntimeException(e);
        }
    }

}
