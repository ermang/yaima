package com.eg.yaima.server.repo;

import com.eg.yaima.server.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendRequestRepo extends JpaRepository<FriendRequest, Long> {

    @Query(value = "SELECT fr" +
            "    FROM FriendRequest fr" +
            "    WHERE fr.to.username = :username")
    List<FriendRequest> findAllByAsd(String username);
}
