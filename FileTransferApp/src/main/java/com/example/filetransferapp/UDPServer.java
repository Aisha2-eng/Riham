package com.example.filetransferapp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UDPServer implements Initializable {
    private DatagramSocket socket, ackSocket;
    private int serverIntPort = 1234 , receivedPackets=0;
    private byte[] buffer;
    private DatagramPacket packet;
    @FXML
    private TextField  serverIP, serverPort;
    @FXML
    private VBox packetsLog;
    @FXML
    private Label packetsStatus;
    //---------------------------------------------------------------------------------

    private void endReceiving(String filePath){
        String msg = "Number of received packets: "+receivedPackets;
//        packetsStatus.setText(msg);
//        packetsStatus.setVisible(true);
        JOptionPane.showMessageDialog(null, msg);
        String message = "Do you want to see the file?";
        int result = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            System.out.println("User clicked 'Yes'");
            File fileToOpen = new File(filePath);

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();

                if (fileToOpen.exists()) {
                    try {
                        desktop.open(fileToOpen);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("File does not exist.");
                }
            } else {
                System.out.println("Desktop is not supported.");
            }
        } else if (result == JOptionPane.NO_OPTION) {
            System.out.println("User clicked 'No'");
        }
    }
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
        fileOutputStream.write(new byte[0]);
        System.out.println(new String(data));
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

    private void addToLogAndPrint(String msg) {
//        Node label = new Label(msg);
//        label.setStyle("-fx-text-fill: blue;");
//        packetsLog.getChildren().add(label);
        System.out.println(msg);
    }

    public Runnable startReceiving() throws Exception {
        buffer  = new byte[1024];
        packet = new DatagramPacket(buffer, buffer.length);
        socket = new DatagramSocket(serverIntPort);
        ackSocket = new DatagramSocket();
        String fileName = "output.txt";
        boolean nameTurn=false;
        ArrayList<Byte>packets = new ArrayList<>();
        int expectedSequenceNumber = 0;
        while (true) {
            socket.receive(packet);
            String input = new String(packet.getData(), 0, packet.getLength());
            if(nameTurn){
                fileName = input;
                nameTurn = false;
                continue;
            }
            if(input.equals("\n##START##\n")) {
                receivedPackets=0;
//                packetsStatus.setVisible(false);
                addToLogAndPrint("start receiving...");
                expectedSequenceNumber = 0;
                nameTurn = true;
            }
            else if(input.equals("\n##END##\n")) {
                addToLogAndPrint("receiving packets ended");
                byte[] finalData = getBytes(packets);
                packets.clear();
                String filePath = "receivedFiles\\"+fileName;
                saveToFile(finalData, filePath);
                endReceiving(filePath);
                System.out.println("received packets = " + receivedPackets);
                receivedPackets = 0;
            }
            else {
                int receivedSequenceNumber = Integer.parseInt(input.split(" ")[1]);

                if (receivedSequenceNumber == expectedSequenceNumber) {
                    addToLogAndPrint("Received: " + input);
                    // Send acknowledgment
                    String ackMessage = "ACK " + expectedSequenceNumber;
                    byte[] ackData = ackMessage.getBytes();
                    DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length, packet.getAddress(), packet.getPort());
                    ackSocket.send(ackPacket);

                    //receive packet
                    buffer  = new byte[1024];
                    packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    input = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(input);
                    for(int i =0 ; i < packet.getData().length ; i ++ ){
                        packets.add(packet.getData()[i]);
                    }
                    receivedPackets++;
                    expectedSequenceNumber = (expectedSequenceNumber + 1) % 2; // Toggle sequence number

                } else {
                    addToLogAndPrint("Received out-of-sequence packet. Discarding.");
                }

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

    public static void main(String[] args){
//        launch();
        System.exit(0);
    }

}