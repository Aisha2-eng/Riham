package com.example.filetransferapp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
public class UDPServer extends Application {
    private DatagramSocket socket;
    private int port;
    private byte[] buffer = new byte[1024];


    public void startReceiving() throws Exception {
        //TODO change port
        setPort(12345);
        socket = new DatagramSocket(port);

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            System.out.println("new packet received");
            saveToFile(packet.getData(), packet.getLength());
        }
    }
    void setPort(int port){
        this.port = port;
    }
    private void saveToFile(byte[] data, int length) throws Exception{
        File file = new File("received_file.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        fileOutputStream.write(data, 0, length);
        fileOutputStream.close();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(UDPClient.class.getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("server side");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) throws Exception {
        launch();
        UDPServer server = new UDPServer();
        server.startReceiving();
    }

}
