package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

public class Client {
    private static String serverIP = "";
    private static Socket socket = null;
    private static DataOutputStream out = null;
    private static DataInputStream in = null;
    private static LinkedList<String> messageQueue = new LinkedList<>();

    public static void sendMessage(String toSend) {
        if (toSend == null || toSend.equals("")) {
            Log.d("MyTraffic", "Sending trash");
            return;
        }
        messageQueue.addLast(toSend);
        //if(messageQueue.size() > 5)
            //Log.d("MyTraffic", "Client too many messages");
    }

    public static void startClient(String serverIP) {
        Client.serverIP = serverIP;

        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(serverIP, Server.serverPort), 500);
            socket.setSoTimeout(0);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            Log.d("MyTag", "Client creating error\n" + e.toString());
            e.printStackTrace();
        }
        Log.d("MyTag", "Client created: " + socket.toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!socket.isClosed()) {
                    String received = "";
                    try {
                        received = in.readUTF();
                        if(received == null || received.equals("")) {
                            Log.d("MyTraffic", "Received trash");
                            continue;
                        }
                    } catch (Exception e) {
                        Log.d("MyTraffic", "Client receiving message error\n" + e.toString());
                        e.printStackTrace();
                        continue;
                    }
                    Log.d("MyTraffic", "Client received: " + received);

                    if (received.equalsIgnoreCase("end")) {
                        closeEverything();
                        break;
                    }
                    MessageManager.clientProcessMessage(received);

                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!socket.isClosed()) {
                    if (messageQueue.peekFirst() == null)
                        continue;

                    String toSend = messageQueue.pollFirst();

                    try {
                        out.writeUTF(toSend);
                        out.flush();
                    } catch (Exception e) {
                        Log.d("MyTraffic", "Client sending exception\n" + e.toString());
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

    public static void closeEverything() {
        try {
            out.close();
            in.close();
            socket.close();
            Log.d("MyTag", "Client closed properly");
        } catch (Exception e) {
            Log.d("MyTag", "Oops...");
            e.printStackTrace();
        }
    }

    public static boolean isConnected() {
        if (socket == null)
            return false;
        return socket.isConnected();
    }

    public static void stopClient() {
        closeEverything();
    }

}