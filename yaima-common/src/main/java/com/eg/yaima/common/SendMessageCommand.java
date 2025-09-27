package com.eg.yaima.common;

public class SendMessageCommand {

    public final String from;
    public final String to;
    public final String message;

    public SendMessageCommand(String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    @Override
    public String toString() {
        return "SendMessageCommand{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public byte[] serialize() {
        byte[] packetTypeArr = "SMS".getBytes(Constant.CHARSET);
        byte[] fromArr = from.getBytes(Constant.CHARSET);
        byte[] toArr = to.getBytes(Constant.CHARSET);
        byte[] messageArr = message.getBytes(Constant.CHARSET);

        byte[] concatenatedArr = new byte[packetTypeArr.length + fromArr.length + 1 + toArr.length + 1 + messageArr.length];

        int index = 0;

        System.arraycopy(packetTypeArr, 0, concatenatedArr, 0, packetTypeArr.length);
        index = index + packetTypeArr.length;
        System.arraycopy(fromArr, 0, concatenatedArr, index, fromArr.length);
        index = index + fromArr.length;
        concatenatedArr[index] = 0x0;
        index = index + 1;
        System.arraycopy(toArr, 0, concatenatedArr, index, toArr.length);
        index = index + toArr.length;
        concatenatedArr[index] = 0x0;
        index = index + 1;
        System.arraycopy(messageArr, 0, concatenatedArr, index, messageArr.length);

        return concatenatedArr;
    }
}
