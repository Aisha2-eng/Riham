package com.example.filetransferapp;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UDPServer extends Application implements Initializable {
    private DatagramSocket socket;
    private int serverIntPort = 1234 ;
    private byte[] buffer;
    private DatagramPacket packet;
    @FXML
    private TextField clientIP, clientPort, serverIP, serverPort;
    //---------------------------------------------------------------------------------
    void setServerPort(int port){
        this.serverIntPort = port;
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

//    int getFreePort() throws IOException {
//        for(int port = 1 ; port <= 9999; port++){
//            try {
//                if(!MainClass.availablePort(port))
//                    continue;
//                DatagramSocket tmp = new DatagramSocket(port);
//                tmp.close();
//                MainClass.editPort(port);
//                MainClass.editPort(serverIntPort);
//                return port;
//            } catch (IOException ex) {
//                continue;
//            }
//        }
//        throw new IOException("no free port found");
//    }
//
//    private void updatePort() throws IOException {
//        serverIntPort = getFreePort();
//        serverPort.setText(Integer.toString(serverIntPort));
//    }
//
//    @FXML
//    void updatePort(ActionEvent e) throws IOException {
//        updatePort();
//    }

    public Runnable startReceiving() throws Exception {
        //TODO change
        buffer  = new byte[1024];
        packet = new DatagramPacket(buffer, buffer.length);
        socket = new DatagramSocket(serverIntPort);
        String fileName = "output.txt";
        int receivedPackets=0;
        boolean nameTurn=false;
        ArrayList<Byte>packets = new ArrayList<>();
        while (true) {
            socket.receive(packet);
            String input = new String(packet.getData(), 0, packet.getLength());
            if(nameTurn){
                fileName = input;
                nameTurn = false;
                continue;
            }
            if(input.equals("\n##START##\n")) {
                System.out.println("start receiving...");
                nameTurn = true;
            }
            else if(input.equals("\n##END##\n")) {
                System.out.println("receiving packets ended");
                byte[] finalData = getBytes(packets);
                packets.clear();
                saveToFile(finalData, fileName);
                System.out.println("received packets = " + receivedPackets);
                receivedPackets = 0;
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
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        try {
//            updatePort();
//        } catch (IOException e) {
//            System.out.println("Couldn't update the port");
//        }
        String hostname = "null";
        try {
            hostname = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Couldn't get client IP");
        }
        serverIP.setText(hostname);
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
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }).start();
    }
    public static void main(String[] args) throws Exception {
        launch();
    }

}