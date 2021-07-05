package com.example.presamsungproject.ConnectionObjects.Threads;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.ServerConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;

public class ServerThread extends Thread {
    private final int serverPort;
    private final int serverBacklog;
    private final HashMap<String, ServerConnection> connections;
    private ServerSocket server = null;

    public ServerThread(int serverPort, int serverBacklog, HashMap<String, ServerConnection> connections) {
        this.serverPort = serverPort;
        this.serverBacklog = serverBacklog;
        this.connections = connections;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress(serverPort), serverBacklog);
            server.setSoTimeout(0);
            Log.d("MyTag", "Server created: " + server.toString());

            while (!server.isClosed()) {
                Socket client = server.accept();
                ServerConnection serverConnection = new ServerConnection(client);
                connections.put(client.getInetAddress().getHostAddress(), serverConnection);
            }
        } catch (Exception e) {
            if (Objects.requireNonNull(e.getMessage()).equals("Socket closed"))
                return;
            Log.d("MyTag", "Server error during running " + e.getMessage());
            e.printStackTrace();
            if (!(server == null)) {
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

    public void close() {
        try {
            for (ServerConnection serverConnection : connections.values()) {
                serverConnection.close();
            }
            server.close();
        } catch (IOException e) {
            Log.d("MyTag", "Server closing error");
            e.printStackTrace();
        }
        Log.d("MyTag", "Server closed properly");
    }
}
