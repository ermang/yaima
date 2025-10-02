package com.eg.yaima.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class FriendRequest extends BaseEntity{

    @ManyToOne(optional = false)
    private AppUser from;

    @ManyToOne(optional = false)
    private AppUser to;

    public AppUser getFrom() {
        return from;
    }

    public void setFrom(AppUser from) {
        this.from = from;
    }

    public AppUser getTo() {
        return to;
    }

    public void setTo(AppUser to) {
        this.to = to;
    }
}
