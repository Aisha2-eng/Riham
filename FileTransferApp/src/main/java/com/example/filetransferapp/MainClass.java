package com.example.filetransferapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainClass extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainClass.class.getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
    private static boolean ports[] = new boolean[10000];
    public static boolean availablePort(int port){
        return !ports[port];
    }
    public static void editPort(int port){
        ports[port] = !ports[port];
    }
    public static void main(String[] args) {
        launch();
    }
}