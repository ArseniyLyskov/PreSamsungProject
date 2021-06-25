package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.Threads.ServerThread;

import java.util.HashMap;

public class Server {
    public static final int serverPort = 4444;
    private final int serverBacklog = 2;
    private ServerThread serverThread = null;
    private final HashMap<String, Connection> connections = new HashMap<>();

    public void start() {
        serverThread = new ServerThread(serverPort, serverBacklog, connections);
        serverThread.start();
    }

    public void stop() {
        if (serverThread.isAlive())
            try {
                for (Connection connection : connections.values()) {
                    connection.stop();
                }
                serverThread.interrupt();
                Log.d("MyTag", "Server closed");
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

    public void removeConnection(Connection connection) {
        connections.remove(connection);
    }

    public boolean containsAddress(String address) {
        return connections.containsKey(address);
    }

    public int getConnectionsQuantity() {
        return connections.size();
    }

    public boolean isRunning() {
        if(serverThread == null)
            return false;
        return serverThread.isInterrupted();
    }

}

