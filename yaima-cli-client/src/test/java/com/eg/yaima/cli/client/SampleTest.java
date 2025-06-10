package com.eg.yaima.cli.client;

import com.eg.yaima.common.Constant;
import com.eg.yaima.common.SendMessageCommand;
import org.junit.jupiter.api.Test;

public class SampleTest {

    @Test
    public void testy () {

        SendMessageCommand smc = new SendMessageCommand("ABC", "DEF", "GHI");

        byte[] packetTypeArr = "SMS".getBytes(Constant.CHARSET);
        byte[] fromArr = smc.from.getBytes(Constant.CHARSET);
        byte[] toArr = smc.to.getBytes(Constant.CHARSET);
        byte[] messageArr = smc.message.getBytes(Constant.CHARSET);

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

        int c = 5;

    }
}
