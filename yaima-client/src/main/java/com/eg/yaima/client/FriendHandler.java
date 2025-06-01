package com.eg.yaima.client;

import com.eg.yaima.UserStatus;

import java.util.HashMap;
import java.util.Map;

public class FriendHandler {

    private final Map<String, UserStatus> friendMap;

    public FriendHandler() {
        friendMap = new HashMap<>();
    }
}
