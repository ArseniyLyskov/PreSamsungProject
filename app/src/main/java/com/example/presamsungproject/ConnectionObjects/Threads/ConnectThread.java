package com.example.presamsungproject.ConnectionObjects.Threads;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.Connection;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.Models.MySingletons;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

public class ConnectThread extends Thread {
    private Connection connection = null;
    private String serverIP;
    private Socket socket;
    private ReadThread readThread = null;
    private WriteThread writeThread = null;
    private final LinkedList<String> messageQueue;

    public ConnectThread(String serverIP, Socket socket, LinkedList<String> messageQueue) {
        this.serverIP = serverIP;
        this.socket = socket;
        this.messageQueue = messageQueue;
    }

    public ConnectThread(Connection connection, Socket socket, LinkedList<String> messageQueue) {
        this.connection = connection;
        this.socket = socket;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        try {
            if (socket == null) {
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverIP, Server.serverPort), 1000);
            }
            Log.d("MyTag", "Connection between: " + socket.getLocalSocketAddress()
                    + " and " + socket.getRemoteSocketAddress());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            readThread = new ReadThread(this, socket, in);
            readThread.start();
            writeThread = new WriteThread(this, socket, out, messageQueue);
            writeThread.start();
        } catch (Exception e) {
            Log.d("MyTag", "Socket creating error\n" + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        if (readThread.isAlive())
            readThread.interrupt();
        if (writeThread.isAlive())
            writeThread.interrupt();
        if (socket != null) {
            try {
                if (MySingletons.isLobby()) {
                    MySingletons.getMyResources().getSAMListener().serverRemovePlayer(connection.getClientAddress());
                } else {
                    MySingletons.getMyResources().getSAMListener().clientUpdateUI("", 0);
                }
                socket.close();
                Log.d("MyTag", "Socket closed properly");
            } catch (Exception e) {
                Log.d("MyTag", "Oops...");
                e.printStackTrace();
            }
        }
        if (connection != null)
            MySingletons.getServer().removeConnection(connection);
        super.interrupt();
    }
}
