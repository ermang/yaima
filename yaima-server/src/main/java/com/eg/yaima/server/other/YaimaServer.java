package com.eg.yaima.server.other;

import com.eg.yaima.common.SendMessageCommand;
import com.eg.yaima.server.repo.AppFriendRepo;
import com.eg.yaima.server.repo.AppUserRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class YaimaServer {

    private final int port;
    private final Map<String, ClientHandler> onlineUsers;
    private final ConnectionAcceptor connectionAcceptor;

    private final AppUserRepo appUserRepo;
    private final AppFriendRepo appFriendRepo;

    public YaimaServer(@Value("${yaima.server.port}") int port, AppUserRepo appUserRepo, AppFriendRepo appFriendRepo) {
        this.port = port;
        onlineUsers = new HashMap<>();
        this.connectionAcceptor = new ConnectionAcceptor(port, this);

        this.appUserRepo = appUserRepo;
        this.appFriendRepo = appFriendRepo;
    }

    @PostConstruct
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
