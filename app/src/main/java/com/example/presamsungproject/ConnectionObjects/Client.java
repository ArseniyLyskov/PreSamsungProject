package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.Threads.ConnectThread;

import java.net.Socket;
import java.util.LinkedList;

public class Client {
    private String serverIP = "";
    private Socket socket = null;
    private ConnectThread connectThread = null;
    private final LinkedList<String> messageQueue = new LinkedList<>();

    public void sendMessage(String toSend) {
        if (toSend == null || toSend.equals("")) {
            Log.d("MyTraffic", "Sending trash");
            return;
        }
        messageQueue.addLast(toSend);
        if (messageQueue.size() > 5)
            Log.d("MyTraffic", "Client too many messages");
    }

    public void start(String serverIP) {
        this.serverIP = serverIP;

        connectThread = new ConnectThread(serverIP, socket, messageQueue);
        connectThread.start();
    }

    private void closeEverything() {
        if (!connectThread.isAlive())
            return;
        connectThread.interrupt();
    }

    public boolean isConnected() {
        if (socket == null)
            return false;
        return socket.isConnected();
    }

    public void stop() {
        closeEverything();
    }

}