package com.example.filetransferapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

public class UDPClient implements Initializable {
    final FileChooser fileChooser = new FileChooser();
    private DatagramSocket socket, ackSocket;
    private InetAddress serverAddress;
    private int serverPort, clientIntPort=1212;
    @FXML
    private TextField serverIP, serverPortTF, clientIP;
    @FXML
    private Label packetsStatus;
    @FXML
    private VBox packetsLog;
    private String fileName;
    private int sentPackets=0, failedPackets=0;

    //-----------------------------------------------------------------------------------------------------------------------
    public void setServerPort(int port){
        serverPort = port;
    }

    public void setServerAddress(String address) throws UnknownHostException {
        System.out.println("sever address = "+address);
        serverAddress = InetAddress.getByName(address);
    }

    public void createSocket(int port, String address) throws Exception {
        addToLogAndPrint("Creating socket... ");
        setServerPort(port);
        setServerAddress(address);
        socket = new DatagramSocket();
        ackSocket = new DatagramSocket();
        addToLogAndPrint("Socket was created successfully :)\n");
    }

    private void addToLogAndPrint(String msg) throws InterruptedException {
        Node label = new Label(msg);
        label.setStyle("-fx-text-fill: blue;");
        packetsLog.getChildren().add(label);
        System.out.println(msg);
    }

    private void sendFile() throws Exception {
        addToLogAndPrint("sending ...");
        File file = new File(fileName);
        sendACK(file.getName());
        int sequenceNumber = 0;
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead = fileInputStream.read(buffer);
        while (true) {
            if(bytesRead == -1)
                break;
            //send sequence number
            sendACK("ack "+Integer.toString(sequenceNumber));

            //receive ACK
            byte[] receiveData = new byte[1024];
            DatagramPacket ackPacket = new DatagramPacket(receiveData, receiveData.length);
            ackSocket.receive(ackPacket);
//            Thread.sleep(100);

            DatagramPacket packet = new DatagramPacket(buffer, bytesRead, serverAddress, serverPort);
            socket.send(packet);
            String ackMessage = new String(ackPacket.getData(), 0, ackPacket.getLength());
            if (ackMessage.equals("ACK " + sequenceNumber)) {
                sentPackets++;
                addToLogAndPrint("Packet " + sequenceNumber + " sent and acknowledged.");
                sequenceNumber = (sequenceNumber + 1) % 2; // Toggle sequence number
                bytesRead = fileInputStream.read(buffer);
            } else {
                addToLogAndPrint("Packet " + sequenceNumber + " not acknowledged. Retransmitting.");
                failedPackets++;
            }
            // Introduce a delay for simulation purposes
            Thread.sleep(100);
        }
        fileInputStream.close();
        addToLogAndPrint("sent successfully");
    }

    private void sendACK(String msg) throws IOException {
        DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),serverAddress, serverPort);
        ackSocket.send(packet);
    }
    private void endSending() throws Exception {
        sendACK("\n##END##\n");
        String msg = "Number of successful packets: "+sentPackets+", number of failed packet: "+failedPackets;
        packetsStatus.setText(msg);
        packetsStatus.setVisible(true);
        sentPackets=0;
        failedPackets=0;
        Thread.sleep(1000);
    }
    @FXML
    public void onSendButtonClick(javafx.event.ActionEvent event) throws Exception {
        packetsStatus.setVisible(false);
        addToLogAndPrint(" ");
        createSocket(Integer.parseInt(serverPortTF.getText()), serverIP.getText());
        sendACK("\n##START##\n");
        sendFile();
        endSending();
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String hostname = "null";
        try {
            hostname = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            try {
                addToLogAndPrint("Couldn't get client IP");
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        clientIP.setText(hostname);
    }


    public static void main(String[] args) {
        System.exit(0);
    }

}