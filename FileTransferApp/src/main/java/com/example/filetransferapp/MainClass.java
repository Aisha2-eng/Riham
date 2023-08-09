package com.example.filetransferapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainClass extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainClass.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        scene.getStylesheets().add(("assets\\myCSS.css"));
        stage.setTitle("Home");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }

}