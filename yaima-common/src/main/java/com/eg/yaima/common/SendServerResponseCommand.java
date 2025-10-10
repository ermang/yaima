package com.eg.yaima.common;

public class SendServerResponseCommand {

    public final String message;

    public SendServerResponseCommand(String message) {
        this.message = message;
    }

    public byte[] serialize() {
        byte[] packetTypeArr = "SSR".getBytes(Constant.CHARSET);
        byte[] fromArr = message.getBytes(Constant.CHARSET);

        byte[] concatenatedArr = new byte[packetTypeArr.length + fromArr.length];

        int index = 0;

        System.arraycopy(packetTypeArr, 0, concatenatedArr, 0, packetTypeArr.length);
        index = index + packetTypeArr.length;
        System.arraycopy(fromArr, 0, concatenatedArr, index, fromArr.length);

        return concatenatedArr;
    }
}
