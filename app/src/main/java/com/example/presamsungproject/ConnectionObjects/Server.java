package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.Threads.ServerThread;

import java.util.HashMap;

public class Server {
    private static Server instance = null;
    public static final int serverPort = 4444;
    private static final int serverBacklog = 1;
    private final ServerThread serverThread;
    private final HashMap<String, Connection> connections = new HashMap<>();

    public static void createInstance() {
        if (instance != null) {
            instance.stop();
        }
        instance = new Server();
    }

    public static Server getInstance() {
        return instance;
    }

    public Server() {
        serverThread = new ServerThread(serverPort, serverBacklog, connections);
        serverThread.start();
    }

    public void stop() {
        if (serverThread.isInterrupted())
            return;
        try {
            broadcastMessage("end");
            serverThread.interrupt();
            Log.d("MyTag", "Server closed properly");
        } catch (Exception e) {
            Log.d("MyTag", "Server error during closing");
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
        for (Connection connection : connections.values()) {
            connection.sendMessage(message);
        }
    }

    public void specificMessage(String address, String message) {
        for (Connection connection : connections.values()) {
            if (connection.getClientAddress().equals(address))
                connection.sendMessage(message);
        }
    }

    public void removeConnection(String address) {
        connections.remove(address);
    }

    public int getConnectionsQuantity() {
        return connections.size();
    }
}

