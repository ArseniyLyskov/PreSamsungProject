package com.example.presamsungproject.ConnectionObjects;

import com.example.presamsungproject.ConnectionObjects.Threads.ServerThread;

import java.util.HashMap;

public class Server {
    private static Server instance = null;
    public static final int serverPort = 4444;
    private static final int serverBacklog = 1;
    private final ServerThread serverThread;
    private final HashMap<String, ServerConnection> connections = new HashMap<>();

    public static void createInstance() {
        if (instance != null) {
            instance.stop();
        }
        instance = new Server();
    }

    public static Server getInstance() {
        return instance;
    }

    public void recreate() {
        instance = new Server();
    }

    private Server() {
        serverThread = new ServerThread(serverPort, serverBacklog, connections);
        serverThread.start();
    }

    public void pause() {
        serverThread.close();
        connections.clear();
    }

    public void stop() {
        serverThread.close();
        connections.clear();
        instance = null;
    }

    public void broadcastMessage(String message) {
        for (ServerConnection serverConnection : connections.values()) {
            serverConnection.sendMessage(message);
        }
    }

    public void specificMessage(String address, String message) {
        for (ServerConnection serverConnection : connections.values()) {
            if (serverConnection.getClientAddress().equals(address))
                serverConnection.sendMessage(message);
        }
    }

    public void removeConnection(String address) {
        connections.remove(address);
    }

    public int getConnectionsQuantity() {
        return connections.size();
    }
}

