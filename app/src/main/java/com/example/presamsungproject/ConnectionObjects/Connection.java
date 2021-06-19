package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.Activities.LobbyServerActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Connection extends Thread {
    private Socket connection;
    private String startMessage = "";
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private String clientAddress = null;
    private boolean ready = true;

    public Connection(Socket connection) {
        this.connection = connection;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void sendMessage(String toSend) {
        if (toSend.equals("")) {
            return;
        }
        if (out != null) {
            final String finalToSend = new String(toSend);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ready = false;
                        out.writeUTF(finalToSend);
                        out.flush();
                        ready = true;
                        Log.d("MyTag", "Server sent: " + finalToSend);
                    } catch (Exception e) {
                        Log.d("MyTag", "Server message sending error");
                        e.printStackTrace();
                    }
                }
            }).start();
        } else
            this.startMessage = toSend;
    }


    @Override
    public void run() {
        try {
            Log.d("MyTag", "Connection between: " + connection.getLocalSocketAddress()
                    + " and " + connection.getRemoteSocketAddress());
            clientAddress = connection.getInetAddress().getHostAddress();
            out = new DataOutputStream(connection.getOutputStream());
            in = new DataInputStream(connection.getInputStream());

            while (!connection.isClosed()) {
                if (!startMessage.equals("")) {
                    sendMessage(startMessage);
                    startMessage = "";
                }

                String received = in.readUTF();
                Log.d("MyTag", "Server received: " + received);

                if (received.equalsIgnoreCase("end")) {
                    break;
                }
                MessageManager.serverProcessMessage(received);
            }
            in.close();
            out.close();
            connection.close();
            Server.removeConnection(this);
            Log.d("MyTag", "Connection closed");

        } catch (Exception e) {
            Log.d("MyTag", "Connection error");
            e.printStackTrace();
            if (!(connection == null))
                if (!connection.isClosed())
                    try {
                        connection.close();
                        Server.removeConnection(this);
                        Log.d("MyTag", "Connection emergency closed");
                    } catch (Exception e2) {
                        Log.d("MyTag", "Connection emergency closing didn't work!!!");
                        e2.printStackTrace();
                    }
        }

        if (clientAddress != null) {
            LobbyServerActivity.players.remove(clientAddress);
            LobbyServerActivity.updateUI();
        }
    }
}



