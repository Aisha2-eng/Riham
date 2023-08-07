package com.example.filetransferapp;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class UDPServer extends Application {
    private DatagramSocket socket;
    private int serverPort;
    private byte[] buffer;
    private DatagramPacket packet;

    void setServerPort(int port){
        this.serverPort = port;
    }

    private byte[] getBytes(ArrayList<Byte> arr){
        byte[] byteArray = new byte[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            byteArray[i] = arr.get(i); // Unboxing and copying to the byte array
        }
        return byteArray ;
    }

    private void saveToFile(byte[]data, String name) throws Exception{
        File file = new File(name);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

    public Runnable startReceiving() throws Exception {
        //TODO change port
        buffer  = new byte[1024];
        packet = new DatagramPacket(buffer, buffer.length);
        setServerPort(1234);
        socket = new DatagramSocket(serverPort);

        int receivedPackets=0;
        ArrayList<Byte>packets = new ArrayList<>();
        while (true) {
            socket.receive(packet);
            String input = new String(packet.getData(), 0, packet.getLength());
            if(input.equals("\n##START##\n")) {
                System.out.println("start receiving...");
            }
            else if(input.equals("\n##END##\n")){
                System.out.println("receiving packets ended");
                byte[] finalData = getBytes(packets);
                packets.clear();
                String name = "output.mp3";
                saveToFile(finalData, name);
                System.out.println("received packets = " + receivedPackets);
                receivedPackets=0;
            }
            else {
                for(int i =0 ; i < packet.getData().length ; i ++ ){
                    packets.add(packet.getData()[i]);
                }
                receivedPackets++;
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(UDPClient.class.getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("server side");
        stage.setScene(scene);
        stage.show();
        new Thread(() -> {
            try {
                UDPServer server = new UDPServer();
                server.startReceiving();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static void main(String[] args) throws Exception {
        launch();
    }

}
