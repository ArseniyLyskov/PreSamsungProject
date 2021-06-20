package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.Activities.LobbyServerActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class Connection {
    private Socket connection;
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private String clientAddress = null;
    private LinkedList<String> messageQueue = new LinkedList<>();

    public Connection(Socket connection) {
        this.connection = connection;

        clientAddress = connection.getInetAddress().getHostAddress();
        try {
            out = new DataOutputStream(connection.getOutputStream());
            in = new DataInputStream(connection.getInputStream());
        } catch (Exception e) {
            Log.d("MyTag","Connection creating error\n" + e.toString());
            e.printStackTrace();
        }
        Log.d("MyTag", "Connection between: " + connection.getLocalSocketAddress()
                + " and " + connection.getRemoteSocketAddress());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!connection.isClosed()) {
                    String received = "";
                    try {
                        received = in.readUTF();
                        if(received == null || received.equals("")) {
                            Log.d("MyTraffic", "Received trash");
                            continue;
                        }
                    } catch (Exception e) {
                        Log.d("MyTraffic", "Connection receiving message error\n" + e.toString());
                        e.printStackTrace();
                        continue;
                    }
                    Log.d("MyTraffic", "Connection received: " + received);

                    if (received.equalsIgnoreCase("end")) {
                        closeEverything();
                        break;
                    }
                    MessageManager.serverProcessMessage(received);
                }
                Server.removeConnection(Connection.this);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!connection.isClosed()) {
                    if (messageQueue.peekFirst() == null)
                        continue;

                    String toSend = messageQueue.pollFirst();

                    try {
                        out.writeUTF(toSend);
                        out.flush();
                    } catch (Exception e) {
                        Log.d("MyTraffic", "Connection sending exception\n" + e.toString());
                        e.printStackTrace();
                    }

                    if (toSend.equalsIgnoreCase("end")) {
                        closeEverything();
                        break;
                    }
                }
            }
        }).start();
    }

    private void closeEverything() {
        try {
            out.close();
            in.close();
            connection.close();
            Server.removeConnection(Connection.this);
            Log.d("MyTag", "Connection closed properly");
        } catch (Exception e) {
            Log.d("MyTag", "Oops...");
            e.printStackTrace();
        }
        if (!Server.containsAddress(clientAddress)) {
            LobbyServerActivity.players.remove(clientAddress);
            LobbyServerActivity.updateUI();
        }
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void sendMessage(String toSend) {
        if (toSend == null || toSend.equals("")) {
            Log.d("MyTraffic", "Sending trash");
            return;
        }
        messageQueue.addLast(toSend);
        //if(messageQueue.size() > 5)
           // Log.d("MyTraffic", "Connection too many messages");
    }

}



