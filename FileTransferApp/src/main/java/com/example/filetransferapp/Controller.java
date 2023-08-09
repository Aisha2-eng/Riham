package com.example.filetransferapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private Button clientButton, serverButton;

    @FXML
    ImageView clientImage, serverImage;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image image = new Image(new File("assets\\client.png").toURI().toString());
        clientImage.setImage(image);
        image = new Image(new File("assets\\server.png").toURI().toString());
        serverImage.setImage(image);
    }
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