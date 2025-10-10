package com.eg.yaima.common;

public class SignUpRequestCommand {

    public final String username;
    public final String password;

    public SignUpRequestCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public byte[] serialize() {
        byte[] packetTypeArr = "SUR".getBytes(Constant.CHARSET);
        byte[] fromArr = username.getBytes(Constant.CHARSET);
        byte[] toArr = password.getBytes(Constant.CHARSET);

        byte[] concatenatedArr = new byte[packetTypeArr.length + fromArr.length + 1 + toArr.length];

        int index = 0;

        System.arraycopy(packetTypeArr, 0, concatenatedArr, 0, packetTypeArr.length);
        index = index + packetTypeArr.length;
        System.arraycopy(fromArr, 0, concatenatedArr, index, fromArr.length);
        index = index + fromArr.length;
        concatenatedArr[index] = 0x0;
        index = index + 1;
        System.arraycopy(toArr, 0, concatenatedArr, index, toArr.length);
        index = index + toArr.length;

        return concatenatedArr;
    }
}
