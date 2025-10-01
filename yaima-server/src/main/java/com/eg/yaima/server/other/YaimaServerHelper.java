package com.eg.yaima.server.other;

import com.eg.yaima.common.SendFriendAnswerCommand;
import com.eg.yaima.server.entity.AppFriend;
import com.eg.yaima.server.entity.AppUser;
import com.eg.yaima.server.repo.AppFriendRepo;
import com.eg.yaima.server.repo.AppUserRepo;
import com.eg.yaima.server.repo.FriendRequestRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Transactional
@Component
public class YaimaServerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(YaimaServerHelper.class);

    private final AppUserRepo appUserRepo;
    private final AppFriendRepo appFriendRepo;
    private final FriendRequestRepo friendRequestRepo;

    public YaimaServerHelper(Map<String, ClientHandler> onlineUsers, AppUserRepo appUserRepo,
                             AppFriendRepo appFriendRepo, FriendRequestRepo friendRequestRepo) {

        this.appUserRepo = appUserRepo;
        this.appFriendRepo = appFriendRepo;
        this.friendRequestRepo = friendRequestRepo;
    }

    public void processSendFriendAnswerCommand(SendFriendAnswerCommand sfa) {
        if (sfa.accepted) {
            friendRequestRepo.deleteByFromUsernameAndToUsername(sfa.from, sfa.to);

            AppUser from = appUserRepo.findByUsername(sfa.from);
            AppUser to = appUserRepo.findByUsername(sfa.to);

            AppFriend af1 = new AppFriend();
            af1.setAppUser(from);
            af1.setAppFriend(to);

            AppFriend af2 = new AppFriend();
            af2.setAppUser(to);
            af2.setAppFriend(from);

            appFriendRepo.save(af1);
            appFriendRepo.save(af2);
        } else
            LOGGER.debug("what to do when friend request is rejected ???");
    }
}
