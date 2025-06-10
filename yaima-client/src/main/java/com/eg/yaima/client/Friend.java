package com.eg.yaima.client;

import com.eg.yaima.common.UserStatus;

public class Friend {

    public final String username;
    public final UserStatus userStatus;

    public Friend(String username, UserStatus userStatus) {
        this.username = username;
        this.userStatus = userStatus;
    }
}
