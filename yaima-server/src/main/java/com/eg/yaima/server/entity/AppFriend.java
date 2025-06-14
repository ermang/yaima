package com.eg.yaima.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class AppFriend extends BaseEntity{

    @ManyToOne(optional = false)
    private AppUser appUser;

    @ManyToOne(optional = false)
    private AppUser appFriend;

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public AppUser getAppFriend() {
        return appFriend;
    }

    public void setAppFriend(AppUser appFriend) {
        this.appFriend = appFriend;
    }
}
