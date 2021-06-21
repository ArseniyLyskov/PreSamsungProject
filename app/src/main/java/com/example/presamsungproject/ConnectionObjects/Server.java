package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    //TODO: может тут стоит применить паттерн Singleton, а не статики?
    private static final int serverBacklog = 2;
    private static ServerSocket server = null;
    //TODO: тут стоит добавить модификатор final, поскольку коллекция одна, но данные разные
    private static HashMap<String, Connection> connections = new HashMap<>();

    public static final int serverPort = 4444;

    public static void startServer() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("MyTag", "Starting server...");
                    server = new ServerSocket();
                    server.bind(new InetSocketAddress(serverPort), serverBacklog);
                    server.setSoTimeout(0);
                    Log.d("MyTag", "Server created: " + server.toString());

                    while (!server.isClosed()) {
                        Socket client = server.accept();
                        if (connections.containsKey(client.getInetAddress().getHostAddress())) {
                            connections.get(client.getInetAddress().getHostAddress()).sendMessage("end");
                        }
                        Connection connection = new Connection(client);
                        connections.put(client.getInetAddress().getHostAddress(), connection);
                    }
                } catch (Exception e) {
                    Log.d("MyTag", "Server error during running");
                    e.printStackTrace();
                    if (!(server == null))
                        if (!server.isClosed())
                            try {
                                server.close();
                                Log.d("MyTag", "Server emergency closed");
                            } catch (Exception e2) {
                                Log.d("MyTag", "Server emergency closing didn't work!!!");
                                e2.printStackTrace();
                            }
                }
            }
        }).start();

    }

    public static void stopServer() {
        if (server != null)
            try {
                server.close();
                Log.d("MyTag", "Server closed");
            } catch (Exception e) {
                Log.d("MyTag", "Server error during closing");
                e.printStackTrace();
            }
    }

    public static void broadcastMessage(String message) {
        for (Connection connection : connections.values()) {
            connection.sendMessage(message);
        }
    }

    public static void specificMessage(String address, String message) {
        for (Connection connection : connections.values()) {
            if (connection.getClientAddress().equals(address))
                connection.sendMessage(message);
        }
    }

    public static void removeConnection(Connection connection) {
        connections.remove(connection);
    }

    public static boolean containsAddress(String address) {
        return connections.containsKey(address);
    }

    public static int getConnectionsQuantity() {
        return connections.size();
    }

}

