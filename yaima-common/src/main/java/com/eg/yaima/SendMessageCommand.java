package com.eg.yaima;

public class SendMessageCommand {

    public final String from;
    public final String to;
    public final String message;

    public SendMessageCommand(String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }
}
