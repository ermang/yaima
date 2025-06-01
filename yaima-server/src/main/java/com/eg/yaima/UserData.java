package com.eg.yaima;

public class UserData {
    public final int id;
    public final String username;
    public final String ip;
    public final int port;

    public UserData(int id, String username, String ip, int port) {
        this.id = id;
        this.username = username;
        this.ip = ip;
        this.port = port;
    }
}
