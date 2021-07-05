package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.Threads.ConnectThread;

import java.net.Socket;
import java.util.LinkedList;

public class ServerConnection {
    private final Socket connection;
    private final String clientAddress;
    private final ConnectThread connectThread;
    private final LinkedList<String> messageQueue = new LinkedList<>();

    public ServerConnection(Socket connection) {
        this.connection = connection;

        clientAddress = connection.getInetAddress().getHostAddress();
        connectThread = new ConnectThread(this);
        connectThread.start();
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void sendMessage(String toSend) {
        if (messageQueue.size() > 5) {
            Log.d("MyTraffic", "Connection too many messages");
            if (toSend.split(" ")[0].equals(MessageManager.SENDING_TANK_MESSAGE))
                return;
        }
        messageQueue.addLast(toSend);
    }

    public void close() {
        connectThread.close(false);
    }

    public Socket getConnection() {
        return connection;
    }

    public LinkedList<String> getMessageQueue() {
        return messageQueue;
    }
}



