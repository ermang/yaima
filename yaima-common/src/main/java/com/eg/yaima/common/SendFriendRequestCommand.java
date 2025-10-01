package com.eg.yaima.common;

public class SendFriendRequestCommand {

    public final String from;
    public final String to;

    public SendFriendRequestCommand(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public byte[] serialize() {
        byte[] packetTypeArr = "SFR".getBytes(Constant.CHARSET);
        byte[] fromArr = from.getBytes(Constant.CHARSET);
        byte[] toArr = to.getBytes(Constant.CHARSET);

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
