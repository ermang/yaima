package com.eg.yaima.server.repo;

import com.eg.yaima.server.entity.AppFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppFriendRepo extends JpaRepository<AppFriend, Long> {

    @Query(value = "SELECT af.appFriend.username" +
                    "    FROM AppFriend af" +
                    "    WHERE af.appUser.username = :username")
    List<String> findFriendsUsernameList(String username);
}
