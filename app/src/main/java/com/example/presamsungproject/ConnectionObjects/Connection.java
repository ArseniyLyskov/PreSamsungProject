package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.Threads.ConnectThread;
import com.example.presamsungproject.MySingletons;

import java.net.Socket;
import java.util.LinkedList;

public class Connection {
    private Socket connection;
    private String clientAddress = null;
    private ConnectThread connectThread = null;
    private final LinkedList<String> messageQueue = new LinkedList<>();

    public Connection(Socket connection) {
        this.connection = connection;

        clientAddress = connection.getInetAddress().getHostAddress();
        connectThread = new ConnectThread(this, connection, messageQueue);
        connectThread.start();
    }

    private void closeEverything() {
        if (!connectThread.isAlive())
            return;
        connectThread.interrupt();
        if (!MySingletons.getServer().containsAddress(clientAddress)) {
            //TODO: disconnection
        }
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void sendMessage(String toSend) {
        if (toSend == null || toSend.equals("")) {
            Log.d("MyTraffic", "Sending trash");
            return;
        }
        messageQueue.addLast(toSend);
        if (messageQueue.size() > 5)
            Log.d("MyTraffic", "Connection too many messages");
    }

    public void stop() {
        closeEverything();
    }

}



