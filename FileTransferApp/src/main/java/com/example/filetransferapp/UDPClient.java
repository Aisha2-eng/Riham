package com.example.filetransferapp;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;

public class UDPClient extends Application {

    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    @FXML
    private Button sendButton;

    public UDPClient() {
        try {
            // TODO replace ip&port
            setServerPort(12345);
            socket = new DatagramSocket();
            setServerAddress("192.168.1.105");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setServerPort(int port){
        serverPort = port;
    }
    public void setServerAddress(String address) throws UnknownHostException {
        serverAddress = InetAddress.getByName(address);
    }
    private void sendFile() throws Exception {
        new UDPClient();
        File file = new File("test.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            DatagramPacket packet = new DatagramPacket(buffer, bytesRead, serverAddress, serverPort);
            socket.send(packet);
        }

        fileInputStream.close();

    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UDPClient.class.getResource("client.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Client side");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }

    public void onSendButtonClick(javafx.event.ActionEvent event) throws Exception {

        System.out.println("send");
        sendFile();
    }
}
