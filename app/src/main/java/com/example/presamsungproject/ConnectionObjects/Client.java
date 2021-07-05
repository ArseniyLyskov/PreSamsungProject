package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.Assistive.ConnectionToServerTester;
import com.example.presamsungproject.ConnectionObjects.Threads.ConnectThread;

import java.util.LinkedList;

public class Client {
    private static Client instance = null;
    private final String serverIP;
    private final String name;
    private final ConnectThread connectThread;
    private final LinkedList<String> messageQueue = new LinkedList<>();

    public static void createInstance(String serverIP, String name) {
        if (instance != null) {
            instance.stop();
        }
        instance = new Client(serverIP, name);
    }

    public static Client getInstance() {
        return instance;
    }

    public void restart() {
        if (instance != null)
            ConnectionToServerTester.testConnection(serverIP, name, 5000, true);
    }

    private Client(String serverIP, String name) {
        this.serverIP = serverIP;
        this.name = name;
        connectThread = new ConnectThread(this);
        connectThread.start();
        sendMessage(MessageManager.connectMessage(name));
        if(MessageManager.getCurrentGame() != null) {
            try {
                sendMessage(MessageManager.sendTankMessage(MessageManager.getCurrentGame().getControlledTank().getSimpleVersion()));
            } catch (Exception e) {
                Log.d("MyTag", "Sending tank error");
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        connectThread.close(false);
    }

    public void stop() {
        instance = null;
        connectThread.close(false);
    }

    public void sendMessage(String toSend) {
        if (messageQueue.size() > 5) {
            Log.d("MyTraffic", "Client too many messages");
            if (toSend.split(" ")[0].equals(MessageManager.SENDING_TANK_MESSAGE))
                return;
        }
        messageQueue.addLast(toSend);
    }

    public String getServerIP() {
        return serverIP;
    }

    public String getName() {
        return name;
    }

    public LinkedList<String> getMessageQueue() {
        return messageQueue;
    }
}