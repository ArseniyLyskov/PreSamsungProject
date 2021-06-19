package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    private static String startMessage = "";
    private static String serverIP = "";
    private static Socket socket = null;
    private static DataOutputStream out = null;
    private static DataInputStream in = null;
    private static boolean ready = true;

    public static void sendMessage(String toSend) {
        if (toSend.equals("")) {
            return;
        }
        if (out != null) {
            final String finalToSend = new String(toSend);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!ready) {
                        }
                        ready = false;
                        out.writeUTF(finalToSend);
                        out.flush();
                        ready = true;
                        Log.d("MyTag", "Client sent: " + finalToSend);
                    } catch (Exception e) {
                        Log.d("MyTag", "Server message sending error");
                        e.printStackTrace();
                    }
                }
            }).start();
        } else
            Client.startMessage = toSend;
    }

    public static void startClient(String serverIP) {
        Client.serverIP = serverIP;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (serverIP.equals(""))
                        return;
                    Log.d("MyTag", "Starting client...");
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(serverIP, Server.serverPort), 5000);
                    socket.setSoTimeout(0);
                    Log.d("MyTag", "Client created: " + socket.toString());

                    out = new DataOutputStream(socket.getOutputStream());
                    in = new DataInputStream(socket.getInputStream());
                    //while (!socket.isOutputShutdown()) {
                    while (!socket.isClosed()) {
                        if (!startMessage.equals("")) {
                            sendMessage(startMessage);
                            startMessage = "";
                        }

                        String received = in.readUTF();
                        Log.d("MyTag", "Client received: " + received);

                        if (received.equalsIgnoreCase("end")) {
                            break;
                        }
                        MessageManager.clientProcessMessage(received);
                    }
                    Log.d("MyTag", "Client connection closed");
                } catch (Exception e) {
                    Log.d("MyTag", "Client error during running");
                    e.printStackTrace();
                    if (!(socket == null))
                        if (!socket.isClosed())
                            try {
                                socket.close();
                                Log.d("MyTag", "Client emergency closed");
                            } catch (Exception e2) {
                                Log.d("MyTag", "Client emergency closing didn't work!!!");
                                e2.printStackTrace();
                            }
                }
            }
        }).start();

    }

    public static boolean isConnected() {
        if (socket == null)
            return false;
        return socket.isConnected();
    }

    public static void stopClient() {
        if (socket != null)
            try {
                socket.close();
                Log.d("MyTag", "Client closed");
            } catch (Exception e) {
                Log.d("MyTag", "Client error during closing");
                e.printStackTrace();
            }
    }

}