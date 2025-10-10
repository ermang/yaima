package com.eg.yaima.common;

public class LoginRequestCommand {

    public final String username;
    public final String password;

    public LoginRequestCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public byte[] serialize() {
        byte[] packetTypeArr = "LRC".getBytes(Constant.CHARSET);
        byte[] usernameArr = username.getBytes(Constant.CHARSET);
        byte[] passwordArr = password.getBytes(Constant.CHARSET);

        byte[] concatenatedArr = new byte[packetTypeArr.length + usernameArr.length + 1 + passwordArr.length];

        int index = 0;

        System.arraycopy(packetTypeArr, 0, concatenatedArr, 0, packetTypeArr.length);
        index = index + packetTypeArr.length;
        System.arraycopy(usernameArr, 0, concatenatedArr, index, usernameArr.length);
        index = index + usernameArr.length;
        concatenatedArr[index] = 0x0;
        index = index + 1;
        System.arraycopy(passwordArr, 0, concatenatedArr, index, passwordArr.length);
        index = index + passwordArr.length;

        return concatenatedArr;
    }
}
