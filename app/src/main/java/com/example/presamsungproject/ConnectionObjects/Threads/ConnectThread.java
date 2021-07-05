package com.example.presamsungproject.ConnectionObjects.Threads;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.Client;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.ConnectionObjects.ServerConnection;
import com.example.presamsungproject.Models.InfoSingleton;
import com.example.presamsungproject.Models.Resources;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

public class ConnectThread extends Thread {
    private ServerConnection serverConnection = null;
    private Client client = null;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final LinkedList<String> messageQueue;

    public ConnectThread(Client client) {
        this.client = client;
        this.messageQueue = client.getMessageQueue();
    }

    public ConnectThread(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
        this.socket = serverConnection.getConnection();
        this.messageQueue = serverConnection.getMessageQueue();
    }

    @Override
    public void run() {
        try {
            if (socket == null) {
                socket = new Socket();
                socket.connect(new InetSocketAddress(client.getServerIP(), Server.serverPort), 1000);
            }
            Log.d("MyTag", "Connection between: " + socket.getLocalSocketAddress()
                    + " and " + socket.getRemoteSocketAddress());
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            ReadThread readThread = new ReadThread(this, socket, in);
            readThread.start();
            WriteThread writeThread = new WriteThread(this, socket, out, messageQueue);
            writeThread.start();
        } catch (Exception e) {
            Log.d("MyTag", "Socket creating error\n" + e.toString());
            e.printStackTrace();
        }
    }

    public void close(boolean tryToRestart) {
        try {
            in.close();
            out.close();
        } catch (Exception e) {
            Log.d("MyTag", "Oops...");
        }
        if (socket != null) {
            try {
                if (InfoSingleton.getInstance().isLobby()) {
                    Resources.getInstance().getSAMListener().serverRemovePlayer(serverConnection.getClientAddress());
                } else {
                    Resources.getInstance().getSAMListener().clientUpdateUI("", 0);
                }
                socket.close();
                Log.d("MyTag", "Socket closed properly");
            } catch (Exception e) {
                Log.d("MyTag", "Oops...");
                e.printStackTrace();
            }
        }
        if (serverConnection != null) {
            Server.getInstance().removeConnection(serverConnection.getClientAddress());
        }
        if (client != null && tryToRestart) {
            client.restart();
        }
    }
}
