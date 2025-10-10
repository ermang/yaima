package com.eg.yaima.common;

public interface ClientConnection extends Runnable {
    @Override
    void run();

    boolean login(LoginRequestCommand username);

    void setUIHandler(UIHandler uiHandler);

    void sendMessage(SendMessageCommand smc);

    String getUsername();

    void stop();

    void sendFriendRequest(SendFriendRequestCommand sfc);


    void senfFriendAnswer(SendFriendAnswerCommand sfa);

    void setLoginSceneController(UIHandler uiHandler);
}
