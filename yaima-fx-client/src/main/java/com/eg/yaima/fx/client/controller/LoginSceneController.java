package com.eg.yaima.fx.client.controller;

import com.eg.yaima.common.ClientConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginSceneController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    private ClientConnection clientConnection;

    // Optional: initialize method called after FXML loaded
    @FXML
    public void initialize() {
        System.out.println("FXML Loaded");
        //label.setText("Hello from Controller!");
    }

    public void setClientConnection(ClientConnection cc) {
        this.clientConnection = cc;
    }

    @FXML
    private void onLoginClick(ActionEvent actionEvent) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Info");
        alert.setHeaderText(null);
        alert.setContentText("Username: " + username + "\nPassword: " + password);
        alert.showAndWait();

        clientConnection.login(username);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainScene.fxml"));
        Parent root = loader.load();

        MainSceneController mainSceneController = loader.getController();
        mainSceneController.setClientConnection(clientConnection);
        clientConnection.setUIHandler(mainSceneController);

        Scene newScene = new Scene(root);

        // Get the current stage (window)
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        stage.setScene(newScene);
        stage.show();
    }

    @FXML
    private void onSignUpClick(ActionEvent actionEvent) {
    }



}
