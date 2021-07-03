package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.Threads.ConnectThread;

import java.net.Socket;
import java.util.LinkedList;

public class Client {
    private static Client instance = null;
    private String serverIP = "";
    private Socket socket = null;
    private ConnectThread connectThread = null;
    private final LinkedList<String> messageQueue = new LinkedList<>();

    public static void createInstance(String serverIP) {
        if (instance != null) {
            instance.stop();
        }
        instance = new Client(serverIP);
    }

    public static Client getInstance() {
        return instance;
    }

    private Client(String serverIP) {
        this.serverIP = serverIP;
        connectThread = new ConnectThread(serverIP, socket, messageQueue);
        connectThread.start();
    }

    public void stop() {
        if (connectThread.isInterrupted())
            return;
        sendMessage("end");
    }

    public void sendMessage(String toSend) {
        messageQueue.addLast(toSend);
        if (messageQueue.size() > 5)
            Log.d("MyTraffic", "Client too many messages");
    }
}