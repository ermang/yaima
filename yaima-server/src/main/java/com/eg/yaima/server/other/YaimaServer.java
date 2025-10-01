package com.eg.yaima.server.other;

import com.eg.yaima.common.SendFriendRequestCommand;
import com.eg.yaima.common.SendMessageCommand;
import com.eg.yaima.common.UserStatus;
import com.eg.yaima.server.entity.AppUser;
import com.eg.yaima.server.entity.FriendRequest;
import com.eg.yaima.server.repo.AppFriendRepo;
import com.eg.yaima.server.repo.AppUserRepo;
import com.eg.yaima.server.repo.FriendRequestRepo;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class YaimaServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(YaimaServer.class);

    private final int port;
    private final Map<String, ClientHandler> onlineUsers;
    private final ConnectionAcceptor connectionAcceptor;

    private final AppUserRepo appUserRepo;
    private final AppFriendRepo appFriendRepo;
    private final FriendRequestRepo friendRequestRepo;

    public YaimaServer(@Value("${yaima.server.port}") int port,
                       AppUserRepo appUserRepo,
                       AppFriendRepo appFriendRepo,
                       FriendRequestRepo friendRequestRepo) {
        this.port = port;
        onlineUsers = new HashMap<>();
        this.connectionAcceptor = new ConnectionAcceptor(port, this);

        this.appUserRepo = appUserRepo;
        this.appFriendRepo = appFriendRepo;
        this.friendRequestRepo = friendRequestRepo;
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

    public void notifyFriendsOfStatusChange(String username, UserStatus userStatus) {
        List<String> x = getFriendsOfUser(username);

        for(String y : x) {
            ClientHandler ch = onlineUsers.get(y);

            if (ch != null) {
                ch.sendFriendSTT(username, userStatus);
            }
        }

    }

    public void removeFromOnlineUsers(String username) {
        onlineUsers.remove(username);
    }

    public List<String> getFriendsOfUser(String username) {
        List<String> x = appFriendRepo.findFriendsUsernameList(username);

        return x;
    }

    public UserStatus getUserStatus(String username) {
        ClientHandler ch = onlineUsers.get(username);

        return ch == null ? UserStatus.OFFLINE : UserStatus.ONLINE;
    }

    public void redirectFriendRequest(SendFriendRequestCommand sendFriendRequestCommand) {
        AppUser from = appUserRepo.findByUsername(sendFriendRequestCommand.from);
        AppUser to = appUserRepo.findByUsername(sendFriendRequestCommand.to);

        FriendRequest fr = new FriendRequest();
        fr.setFrom(from);
        fr.setTo(to);

        friendRequestRepo.save(fr);

        ClientHandler ch = onlineUsers.get(sendFriendRequestCommand.to);

        if (ch == null)
            LOGGER.debug("User:{} is not online not sending FriendRequestCommand", sendFriendRequestCommand.to);
        else
            ch.sendFriendRequestCommand(sendFriendRequestCommand);


    }

    public List<FriendRequest> getFriendRequestsOfUser(String username) {
        List<FriendRequest> friendRequestList = friendRequestRepo.findAllByAsd(username);

        return friendRequestList;
    }
}
