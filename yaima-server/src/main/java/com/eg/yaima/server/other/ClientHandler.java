package com.eg.yaima.server.other;

import com.eg.yaima.common.*;
import com.eg.yaima.server.entity.FriendRequest;
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
    private final CommandDeserializer commandDeserializer;
    private String username;

    public ClientHandler(Socket socket, YaimaServer yaimaServer, CommandDeserializer commandDeserializer) {
        this.socket = socket;
        this.yaimaServer = yaimaServer;
        this.commandDeserializer = commandDeserializer;
    }

    @Override
    public void run() {
        String remoteIp = socket.getInetAddress().getHostAddress();
        int remotePort = socket.getPort();

        LOGGER.debug("Client with ip:{} port:{} connected", remoteIp, remotePort);

        byte[] tempArr = null;
        boolean loggedIn = false;

        try {

            while(!loggedIn) {
                byte[] msgLenArr = socket.getInputStream().readNBytes(2);
                int msgLen = ByteBuffer.wrap(msgLenArr).getShort();
                tempArr = socket.getInputStream().readNBytes(msgLen);

                String packetType = new String(tempArr, 0, 3, Constant.CHARSET);

                if (packetType.equals("LRC")) {
                    LoginRequestCommand lrc = commandDeserializer.deserializeLoginRequestCommand(tempArr);
                    loggedIn = yaimaServer.tryLogin(lrc, this);

                    if (loggedIn) {
                        this.username = lrc.username;
                        LOGGER.debug("Client with ip:{} port:{} username:{} logged in successfully", remoteIp, remotePort, username);

                        SendLoginResponse slr = new SendLoginResponse("log in success", true);
                        byte[] temp = slr.serialize();

                        short x = (short) temp.length;
                        byte[] bytes = ByteBuffer.allocate(2).putShort(x).array();

                        try {
                            socket.getOutputStream().write(bytes);
                            socket.getOutputStream().write(temp);
                        } catch (IOException e) {
                            LOGGER.error("ERR:", e);
                            throw new RuntimeException(e);
                        }

                        List<String> friendsOfUser = yaimaServer.getFriendsOfUser(username);
                        LOGGER.debug("Client with ip:{} port:{} username:{} friends:{}", remoteIp, remotePort, username, friendsOfUser);
                        //TODO: check their statuses (online/offline) and send this data to logged in user
                        for (String s : friendsOfUser) {
                            UserStatus userStatus = yaimaServer.getUserStatus(s);
                            sendFriendSTT(s, userStatus);
                        }

                        //send waiting requests begin
                        List<FriendRequest> friendRequestList = yaimaServer.getFriendRequestsOfUser(username);
                        for (FriendRequest fr : friendRequestList)
                            sendFriendRequestCommand(new SendFriendRequestCommand(fr.getFrom().getUsername(), fr.getTo().getUsername()));
                        //send waiting requests end

                        yaimaServer.notifyFriendsOfStatusChange(username, UserStatus.ONLINE);
                    }
                } else {
                    LOGGER.error("olmaz oyle sey");
                    //throw new RuntimeException("olmaz oyle sey");
                }
            }

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
                SendMessageCommand smc = commandDeserializer.deserialize(tempArr);
                    yaimaServer.redirectChat(smc);
                } else if (packetType.equals("SFR")) {
                    SendFriendRequestCommand sfc = commandDeserializer.deserializeSendFriendRequestCommand(tempArr);
                    yaimaServer.redirectFriendRequest(sfc);
                } else if (packetType.equals("SFA")) {
                    SendFriendAnswerCommand sfa = commandDeserializer.deserializeSendFriendAnswerCommand(tempArr);
                    yaimaServer.processSendFriendAnswerCommand(sfa);
                    //sendFriendSTT(s, userStatus);
                }

            } catch (IOException | BufferUnderflowException e) { //TODO: may need to handle BufferUnderflowException somewhat better
                //throw new RuntimeException(e);
                //TODO: get online friends of this user
                LOGGER.debug("Client with ip:{} port:{} username:{} has a problem", remoteIp, remotePort, username);

               LOGGER.error("ERR:", e);
               yaimaServer.notifyFriendsOfStatusChange(username, UserStatus.OFFLINE);
               yaimaServer.removeFromOnlineUsers(username);
               break;
            } catch (Exception e) {
                //TODO: buraya gelmemeli handle et
                LOGGER.error("ERR", e);
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

        byte[] concatenatedArr = smc.serialize();

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

    public void sendFriendRequestCommand(SendFriendRequestCommand sendFriendRequestCommand) {

        byte[] concatenatedArr = sendFriendRequestCommand.serialize();

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

    public void sendServerResponseCommand(SendServerResponseCommand ssr) {

        byte[] concatenatedArr = ssr.serialize();

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
