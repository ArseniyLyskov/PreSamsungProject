package com.example.presamsungproject.ConnectionObjects.Assistive;

import com.example.presamsungproject.ConnectionObjects.Client;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.Activities.Start.StartActivityFragmentListener;

import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionToServerTester {
    private static ConnectionTestThread connectionTestThread = null;

    public static void testConnection(String serverIP, boolean startClientIfSuccess,
                                      String name, StartActivityFragmentListener SAFListener) {
        if (connectionTestThread != null)
            if (!connectionTestThread.isInterrupted())
                connectionTestThread.interrupt();
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
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String message = "end";
                byte[] bytesToSend = message.getBytes(StandardCharsets.UTF_8);
                out.writeInt(bytesToSend.length);
                out.write(bytesToSend);
                out.flush();
                socket.close();
                if (startClientIfSuccess) {
                    sleep(1000);
                    Client.createInstance(serverIP);
                    Client.getInstance().sendMessage(MessageManager.connectMessage(name));
                }
            } catch (Exception e) {
                SAFListener.showProblem("Error during connection. Possibly a typo in the IP " +
                        "or there is no lobby in the local network.");
                e.printStackTrace();
            }
        }
    }
}
