package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastingMessage extends Thread {
    private static DatagramSocket socket;
    private String broadcastMessage;
    private String address;
    private int port;

    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);

            byte[] buffer = broadcastMessage.getBytes();

            InetAddress inetAddress = InetAddress.getByName(address);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inetAddress, port);
            socket.send(packet);
            socket.close();
            Log.d("MyTag", "Message sent: " + broadcastMessage);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MyError", "BroadcastingMessage Thread Error");
        }
    }

    public BroadcastingMessage(String broadcastMessage, String address, int port) {
        this.broadcastMessage = broadcastMessage;
        this.address = address;
        this.port = port;
    }
}
