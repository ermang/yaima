package com.eg.yaima.common;

public class SendLoginResponse {

    public final String message;
    public final boolean operationSuccess;

    public SendLoginResponse(String message, boolean operationSuccess) {
        this.message = message;
        this.operationSuccess = operationSuccess;
    }

    public byte[] serialize() {
        byte[] packetTypeArr = "SLR".getBytes(Constant.CHARSET);
        byte operationSuccessByte =  operationSuccess ? "Y".getBytes(Constant.CHARSET)[0] : "N".getBytes(Constant.CHARSET)[0] ;
        byte[] messageArr = message.getBytes(Constant.CHARSET);

        byte[] concatenatedArr = new byte[packetTypeArr.length + 1 + messageArr.length];

        int index = 0;

        System.arraycopy(packetTypeArr, 0, concatenatedArr, 0, packetTypeArr.length); //copy packetType
        index = index + packetTypeArr.length;
        concatenatedArr[index] = operationSuccessByte;
        index = index + 1;
        System.arraycopy(messageArr, 0, concatenatedArr, index, messageArr.length);

        return concatenatedArr;
    }
}
