package com.eg.yaima.fx.client;

import com.eg.yaima.client.Friend;
import com.eg.yaima.common.*;
import com.eg.yaima.fx.client.controller.LoginSceneController;
import com.eg.yaima.fx.client.controller.MainSceneController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class FXClientConnection implements ClientConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(FXClientConnection.class);

    private final CommandDeserializer commandDeserializer;
    private String ip;
    private int port;
    private Socket socket;
    private MainSceneController uiHandler;
    private LoginSceneController loginSceneController;
    private String username;

    public FXClientConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.commandDeserializer = new CommandDeserializer();
    }

    @Override
    public void run() {

        try {
            socket = new Socket(ip, port);

            byte[] tempArr = null;
            byte[] msgLenArr = null;

            while(true) {

                msgLenArr = socket.getInputStream().readNBytes(2);
                int result = ByteBuffer.wrap(msgLenArr).getShort();
                tempArr = socket.getInputStream().readNBytes(result);

                String packetType = new String(tempArr, 0, 3, Constant.CHARSET);

                if (packetType.equals("STT")) {
                    String status = new String(tempArr, 3, 3, Constant.CHARSET);
                    String friend = new String(tempArr, 6, tempArr.length - 6, Constant.CHARSET);
                    System.out.println(friend);

                    Platform.runLater(() -> {
                        Friend f = new Friend(friend, status.equals("ONL") ? UserStatus.ONLINE : UserStatus.OFFLINE);
                        uiHandler.updateFriendListPanel(f);
                    });
                } else if (packetType.equals("SMS")) {

                    SendMessageCommand smc = commandDeserializer.deserialize(tempArr);

                    Platform.runLater(() -> {
                        uiHandler.updateChat(smc);
                    });

                } else if (packetType.equals("SFR")) {
                    SendFriendRequestCommand sfc = commandDeserializer.deserializeSendFriendRequestCommand(tempArr);
                    int x = 5;

                   Platform.runLater(() -> uiHandler.updateWaitingFriendRequests(sfc));
                } else if (packetType.equals("SSR")) {
                    SendServerResponseCommand ssr = commandDeserializer.deserializeSendServerResponseCOmmand(tempArr);

                    Platform.runLater(() -> {

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("server says");
                        alert.setHeaderText(null);
                        alert.setContentText(ssr.message);
                        alert.showAndWait();
                    });
                } else if (packetType.equals("SLR")) {
                    SendLoginResponse slr = commandDeserializer.deserializeSendLoginResponse(tempArr);

                    if (slr.operationSuccess) {
                        Platform.runLater(() -> {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainScene.fxml"));
                            Parent root = null;
                            try {
                                root = loader.load();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            MainSceneController mainSceneController = loader.getController();
                            mainSceneController.setClientConnection(this);
                            this.setUIHandler(mainSceneController);

                            Scene newScene = new Scene(root);

                            // Get the current stage (window)
                            //Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                            Stage stage = (Stage) loginSceneController.getUsernameField().getScene().getWindow();

                            stage.setScene(newScene);
                            stage.show();
                        });
                    }

                    //uiHandler.processLoginSuccess();
                }
            }

        } catch(ConnectException e) {
            LOGGER.error("ERR:", e);
            Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Network Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Can not connect to server, closing app");
                        alert.showAndWait();
                        Platform.exit();
                    }
                    );

        }
        catch (IOException e) {
            LOGGER.error("ERR:", e);
            while (!Platform.isFxApplicationThread()) { // wait until javafx is running
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }

            Platform.runLater(() -> {
               // uiHandler.showErrorPopup();
                }
            );
        }

    }

    @Override
    public boolean login(LoginRequestCommand lrc) {

//        String usernameSpacePadded = username.length() < Constant.MAX_USERNAME_LEN ? username + " ".repeat(Constant.MAX_USERNAME_LEN - username.length()) : username;
//
//        byte[] tempArr = usernameSpacePadded.getBytes(Constant.CHARSET);

        byte[] tempArr = lrc.serialize();

        short x = (short) tempArr.length;
        byte[] bytes = ByteBuffer.allocate(2).putShort(x).array();


        try {
            socket.getOutputStream().write(bytes);
            socket.getOutputStream().write(tempArr);
        } catch (IOException e) {
            LOGGER.error("ERR: ", e);
            throw new RuntimeException(e);
        }

        this.username = lrc.username;

        return true;
    }

    @Override
    public void setUIHandler(UIHandler uiHandler) {
        if (uiHandler instanceof MainSceneController)
            this.uiHandler = (MainSceneController) uiHandler;
        else
            throw new UnsupportedOperationException("olmaz oyle sey");
    }

    @Override
    public void sendMessage(SendMessageCommand smc) {

        byte[] concatenatedArr = smc.serialize();
        short x = (short) concatenatedArr.length;
        byte[] bytes = ByteBuffer.allocate(2).putShort(x).array();

        try {
            socket.getOutputStream().write(bytes);
            socket.getOutputStream().write(concatenatedArr);
        } catch (IOException e) {
            LOGGER.error("ERR: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void stop() {
        try {
            if (this.socket != null)
                this.socket.close();
        } catch (IOException e) {
            LOGGER.error("ERR: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendFriendRequest(SendFriendRequestCommand sfc) {
        byte[] concatenatedArr = sfc.serialize();

        short x = (short) concatenatedArr.length;
        byte[] bytes = ByteBuffer.allocate(2).putShort(x).array();

        try {
            socket.getOutputStream().write(bytes);
            socket.getOutputStream().write(concatenatedArr);
        } catch (IOException e) {
            LOGGER.error("ERR: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void senfFriendAnswer(SendFriendAnswerCommand sfa) {
        byte[] concatenatedArr = sfa.serialize();

        short x = (short) concatenatedArr.length;
        byte[] bytes = ByteBuffer.allocate(2).putShort(x).array();

        try {
            socket.getOutputStream().write(bytes);
            socket.getOutputStream().write(concatenatedArr);
        } catch (IOException e) {
            LOGGER.error("ERR: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setLoginSceneController(UIHandler uiHandler) {
        if (uiHandler instanceof LoginSceneController)
            this.loginSceneController = (LoginSceneController) uiHandler;
        else
            throw new UnsupportedOperationException("olmaz oyle sey");
    }

    public void setLoginSceneController(LoginSceneController lsc) {
        if (lsc instanceof LoginSceneController)
            this.loginSceneController = (LoginSceneController) lsc;
        else
            throw new UnsupportedOperationException("olmaz oyle sey");
    }
}
