package com.eg.yaima;

import java.util.HashMap;
import java.util.Map;

public class YaimaServer implements Runnable{

    private final int port;
    private final Map<String, ClientHandler> onlineUsers;
    private final ConnectionAcceptor connectionAcceptor;

    public YaimaServer(int port) {
        this.port = port;
        onlineUsers = new HashMap<>();
        this.connectionAcceptor = new ConnectionAcceptor(port, this);
    }

    @Override
    public void run() {

        Thread t = new Thread(connectionAcceptor);
        t.start();
    }

    public void addOnlineUser(String username, ClientHandler clientHandler) {
        onlineUsers.put(username, clientHandler);
    }

    public void redirectChat(SendMessageCommand sendMessageCommand) {
        ClientHandler ch = onlineUsers.get(sendMessageCommand.to);

        if (ch == null)
            throw new RuntimeException("yok oyle bisi");

        ch.sendMessage(sendMessageCommand);
    }

    public void notifyFriendsOfStatusChange(String username) {
        if (username.equals("bob")) {   //TODO: bob is friend of alice, tell alice that bob is offline now
            ClientHandler ch = onlineUsers.get("alice");

            if (ch == null)
                throw new RuntimeException("olmaz oyle sey");
            else {
                ch.sendSTT(username);
            }
        }
    }

    public void removeFromOnlineUsers(String username) {
        onlineUsers.remove(username);
    }
}
