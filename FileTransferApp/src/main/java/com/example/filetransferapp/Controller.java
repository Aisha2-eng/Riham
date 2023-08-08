package com.example.filetransferapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    @FXML
    private Button clientButton, serverButton;
    @FXML
    protected void onClientClick() throws IOException {
        Stage stage = (Stage)clientButton.getScene().getWindow();
        stage.close();
        stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
        stage.setTitle("client side");
//        Image img = new Image(getClass().getResourceAsStream("/com/example/dental_lab/images/LOGO.png"));
//        stage.getIcons().add(img);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    @FXML
    protected void onServerClick() throws IOException {
        Stage stage = (Stage)serverButton.getScene().getWindow();
        stage.close();
        stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("server.fxml"));
        stage.setTitle("server side");
//        Image img = new Image(getClass().getResourceAsStream("/com/example/dental_lab/images/LOGO.png"));
//        stage.getIcons().add(img);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        new Thread(() -> {
            try {
                UDPServer server = new UDPServer();
                server.startReceiving();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }).start();
    }
}