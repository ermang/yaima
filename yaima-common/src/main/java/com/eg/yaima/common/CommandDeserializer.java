package com.eg.yaima.common;

public class CommandDeserializer {

    public SendMessageCommand deserialize(byte[] tempArr) {
        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 0; i < tempArr.length; i++) {
            if (tempArr[i] == 0) {
                fromIndex = i;
                break;
            }
        }

        for (int i = fromIndex+1; i < tempArr.length; i++) {
            if (tempArr[i] == 0) {
                toIndex = i;
                break;
            }
        }

        String from = new String(tempArr, 3, fromIndex - 3, Constant.CHARSET);
        String to = new String(tempArr, fromIndex+1, toIndex-fromIndex-1, Constant.CHARSET);
        String msg = new String(tempArr, toIndex+1, tempArr.length - toIndex -1, Constant.CHARSET);

        SendMessageCommand smc = new SendMessageCommand(from, to, msg);

        return smc;
    }

    public SendFriendRequestCommand deserializeSendFriendRequestCommand(byte[] tempArr) {
        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 0; i < tempArr.length; i++) {
            if (tempArr[i] == 0) {
                fromIndex = i;
                break;
            }
        }

        for (int i = fromIndex+1; i < tempArr.length; i++) {
            if (tempArr[i] == 0) {
                toIndex = i;
                break;
            }
        }

        String from = new String(tempArr, 3, fromIndex - 3, Constant.CHARSET);
        String to = new String(tempArr, fromIndex+1, tempArr.length-fromIndex-1, Constant.CHARSET);

        SendFriendRequestCommand sfc = new SendFriendRequestCommand(from, to);

        return sfc;
    }

    public SendFriendAnswerCommand deserializeSendFriendAnswerCommand(byte[] tempArr) {
        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 0; i < tempArr.length; i++) {
            if (tempArr[i] == 0) {
                fromIndex = i;
                break;
            }
        }

        for (int i = fromIndex+1; i < tempArr.length; i++) {
            if (tempArr[i] == 0) {
                toIndex = i;
                break;
            }
        }

        String from = new String(tempArr, 3, fromIndex - 3, Constant.CHARSET);
        String to = new String(tempArr, fromIndex+1, tempArr.length-fromIndex-2, Constant.CHARSET);
        boolean accepted = new String(tempArr, tempArr.length -1, 1, Constant.CHARSET).equals("Y") ? true : false;

        SendFriendAnswerCommand sfa = new SendFriendAnswerCommand(from, to, accepted);

        return sfa;
    }
}
