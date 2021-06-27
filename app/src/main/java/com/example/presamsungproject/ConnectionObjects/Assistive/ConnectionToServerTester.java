package com.example.presamsungproject.ConnectionObjects.Assistive;

import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.Models.MySingletons;
import com.example.presamsungproject.MyInterfaces.StartActivityFragmentListener;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionToServerTester {
    public static void testConnection(String serverIP, boolean startClientIfSuccess,
                                      String name, StartActivityFragmentListener SAFListener) {
        ConnectionTestThread connectionTestThread;
        connectionTestThread = new ConnectionTestThread(serverIP, startClientIfSuccess, name, SAFListener);
        connectionTestThread.start();
    }

    static class ConnectionTestThread extends Thread {
        private final String serverIP;
        private final boolean startClientIfSuccess;
        private final String name;
        private final StartActivityFragmentListener SAFListener;

        public ConnectionTestThread(String serverIP, boolean startClientIfSuccess,
                                    String name, StartActivityFragmentListener SAFListener) {
            this.serverIP = serverIP;
            this.startClientIfSuccess = startClientIfSuccess;
            this.name = name;
            this.SAFListener = SAFListener;
        }

        @Override
        public void run() {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(serverIP, Server.serverPort), 1000);
                socket.close();
                if (startClientIfSuccess) {
                    MySingletons.createClient(serverIP);
                    MySingletons.getClient().sendMessage(MessageManager.connectMessage(name));
                }
            } catch (Exception e) {
                SAFListener.showProblem("Error during connection. Possibly a typo in the IP " +
                        "or there is no lobby in the local network.");
                e.printStackTrace();
            }
        }
    }
}
