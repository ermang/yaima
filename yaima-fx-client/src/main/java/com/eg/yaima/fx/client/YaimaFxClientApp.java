package com.eg.yaima.fx.client;

import com.eg.yaima.fx.client.controller.LoginSceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class YaimaFxClientApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        try {

            FXClientConnection fxClientConnection = new FXClientConnection("127.0.0.1", 8080);
            Thread thread = new Thread(fxClientConnection);



            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginScene.fxml"));
            Parent root = loader.load();

            LoginSceneController loginSceneController = loader.getController();
            loginSceneController.setClientConnection(fxClientConnection);

            thread.start();


            Scene scene = new Scene(root, 300, 200);
            stage.setTitle("YAIMA");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
