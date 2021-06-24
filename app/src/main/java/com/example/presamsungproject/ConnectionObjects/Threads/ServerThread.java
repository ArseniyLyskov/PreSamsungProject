package com.example.presamsungproject.ConnectionObjects.Threads;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.Connection;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {
    private final int serverPort;
    private final int serverBacklog;
    private final HashMap<String, Connection> connections;

    public ServerThread(int serverPort, int serverBacklog, HashMap<String, Connection> connections) {
        this.serverPort = serverPort;
        this.serverBacklog = serverBacklog;
        this.connections = connections;
    }

    @Override
    public void run() {
        ServerSocket server = null;
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
}
