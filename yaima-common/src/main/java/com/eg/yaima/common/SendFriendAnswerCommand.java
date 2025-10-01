package com.eg.yaima.common;

public class SendFriendAnswerCommand {

    public final String from;
    public final String to;
    public final boolean accepted;

    public SendFriendAnswerCommand(String from, String to, boolean accepted) {
        this.from = from;
        this.to = to;
        this.accepted = accepted;
    }

    public byte[] serialize() {
        byte[] packetTypeArr = "SFA".getBytes(Constant.CHARSET);
        byte[] fromArr = from.getBytes(Constant.CHARSET);
        byte[] toArr = to.getBytes(Constant.CHARSET);

        byte[] concatenatedArr = new byte[packetTypeArr.length + fromArr.length + 1 + toArr.length + 1];

        int index = 0;

        System.arraycopy(packetTypeArr, 0, concatenatedArr, 0, packetTypeArr.length);
        index = index + packetTypeArr.length;
        System.arraycopy(fromArr, 0, concatenatedArr, index, fromArr.length);
        index = index + fromArr.length;
        concatenatedArr[index] = 0x0;
        index = index + 1;
        System.arraycopy(toArr, 0, concatenatedArr, index, toArr.length);

        concatenatedArr[concatenatedArr.length-1] = accepted ? "Y".getBytes(Constant.CHARSET)[0] : "N".getBytes(Constant.CHARSET)[0] ;

        return concatenatedArr;
    }
}
