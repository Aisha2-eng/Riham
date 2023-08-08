package com.example.filetransferapp;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UDPClient extends Application implements Initializable {
    final FileChooser fileChooser = new FileChooser();
    private DatagramSocket socket, ackSocket;
    private InetAddress serverAddress;
    private int serverPort, clientIntPort=1212;
    @FXML
    private TextField serverIP, serverPortTF, clientIP;

    private String fileName;

    //-----------------------------------------------------------------------------------------------------------------------
    public void setServerPort(int port){
        serverPort = port;
    }

    public void setServerAddress(String address) throws UnknownHostException {
        System.out.println("sever address = "+address);
        serverAddress = InetAddress.getByName(address);
    }

    public void createSocket(int port, String address) throws Exception {
        System.out.println("Creating socket... ");
        setServerPort(port);
        setServerAddress(address);
        socket = new DatagramSocket();
        ackSocket = new DatagramSocket();
        System.out.println("Socket was created successfully :)\n");
    }

    private void sendFile() throws Exception {
        System.out.println("sending...");
        File file = new File(fileName);
        sendACK(file.getName());
        int sequenceNumber = 0;
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead = fileInputStream.read(buffer);
        while (true) {
            //send sequence number
            sendACK("ack "+Integer.toString(sequenceNumber));
            Thread.sleep(100);

            if(bytesRead == -1)
                break;
            DatagramPacket packet = new DatagramPacket(buffer, bytesRead, serverAddress, serverPort);
            socket.send(packet);


            //receive ACK
            byte[] receiveData = new byte[1024];
            DatagramPacket ackPacket = new DatagramPacket(receiveData, receiveData.length);
            ackSocket.receive(ackPacket);

            String ackMessage = new String(ackPacket.getData(), 0, ackPacket.getLength());
            if (ackMessage.equals("ACK " + sequenceNumber)) {
                System.out.println("Packet " + sequenceNumber + " sent and acknowledged.");
                sequenceNumber = (sequenceNumber + 1) % 2; // Toggle sequence number
                bytesRead = fileInputStream.read(buffer);
            } else {
                System.out.println("Packet " + sequenceNumber + " not acknowledged. Retransmitting.");
            }
            // Introduce a delay for simulation purposes
            Thread.sleep(100);
        }
        fileInputStream.close();
        System.out.println("sent successfully");
    }

    private void sendACK(String msg) throws IOException {
        DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),serverAddress, serverPort);
        ackSocket.send(packet);
    }

    @FXML
    public void onSendButtonClick(javafx.event.ActionEvent event) throws Exception {
        createSocket(Integer.parseInt(serverPortTF.getText()), serverIP.getText());
        sendACK("\n##START##\n");
        sendFile();
        sendACK("\n##END##\n");
        Thread.sleep(1000);
    }

    @FXML
    void chooseFileClick(ActionEvent event) throws IOException {
        File file = fileChooser.showOpenDialog(new Stage());
//        String fileFullName = new String(file.getName());
//        String fileType = new String(fileFullName.substring(fileFullName.lastIndexOf(".")+1,fileFullName.length()));
//        String fileName = new String(fileFullName.substring(0,fileFullName.lastIndexOf(".")));
//        ArrayList<DatagramPacket> packets = splitToPackets(Files.readAllBytes(file.toPath()));
//        UDPFileSender.sendData(Files.readAllBytes(file.toPath()), InetAddress.getLocalHost().getHostAddress(),1234);
        fileName = file.getAbsolutePath();
    }

//    int getFreePort() throws IOException {
//        for(int port = 1 ; port <= 9999; port++){
//            try {
//                if(!MainClass.availablePort(port))
//                    continue;
//                DatagramSocket tmp = new DatagramSocket(port);
//                tmp.close();
//                MainClass.editPort(port);
//                MainClass.editPort(clientIntPort);
//                return port;
//            } catch (IOException ex) {
//                continue;
//            }
//        }
//        throw new IOException("no free port found");
//    }
//
//    private void updatePort() throws IOException {
//        clientIntPort = getFreePort();
//        clientPort.setText(Integer.toString(clientIntPort));
//    }
//
//    @FXML
//    void updatePort(ActionEvent e) throws IOException {
//        updatePort();
//    }

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
        clientIP.setText(hostname);
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

}
