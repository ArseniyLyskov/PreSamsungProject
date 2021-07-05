package com.example.presamsungproject.ConnectionObjects.Assistive;

import com.example.presamsungproject.ConnectionObjects.Client;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.Models.InfoSingleton;
import com.example.presamsungproject.Models.Resources;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionToServerTester {
    private static ConnectionTestThread connectionTestThread = null;

    public static void testConnection(String serverIP, String name, int timeout, boolean startClientIfSuccess) {
        if (connectionTestThread != null)
            connectionTestThread.close();
        connectionTestThread = new ConnectionTestThread(serverIP, name, timeout, startClientIfSuccess);
        connectionTestThread.start();
    }

    static class ConnectionTestThread extends Thread {
        private final String serverIP;
        private final boolean startClientIfSuccess;
        private final String name;
        private final int timeout;
        private Socket socket;

        public ConnectionTestThread(String serverIP, String name, int timeout, boolean startClientIfSuccess) {
            this.serverIP = serverIP;
            this.startClientIfSuccess = startClientIfSuccess;
            this.timeout = timeout;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverIP, Server.serverPort), timeout);
                socket.close();
                if (startClientIfSuccess) {
                    sleep(1000);
                    Client.createInstance(serverIP, name);
                }
            } catch (Exception e) {
                Resources.getInstance().getPListener().showProblem("Connection error.\n\n Possibly reasons:\n " +
                        "1) There is no lobby with such IP in the local network\n2) Lobby creator interrupted session");
                if (!InfoSingleton.getInstance().isLobby())
                    if (Client.getInstance() != null)
                        Client.getInstance().stop();
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
