package com.example.presamsungproject.ConnectionObjects.Threads;

import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.MySingletons;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReadThread extends Thread {
    private final ConnectThread parentThread;
    private final Socket socket;
    private final DataInputStream in;

    public ReadThread(ConnectThread parentThread, Socket socket, DataInputStream in) {
        this.parentThread = parentThread;
        this.socket = socket;
        this.in = in;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            String received = "";
            try {
                received = in.readUTF();
                if (received == null || received.equals("")) {
                    Log.d("MyTraffic", "Received trash");
                    continue;
                }
            } catch (Exception e) {
                Log.d("MyTraffic", "Receiving error\n" + e.toString());
                e.printStackTrace();
                continue;
            }
            Log.d("MyTraffic", "Received: " + received);

            if (received.equalsIgnoreCase("end")) {
                parentThread.interrupt();
                break;
            }
            if (MySingletons.isLobby())
                MessageManager.serverProcessMessage(received);
            else
                MessageManager.clientProcessMessage(received);
        }
    }

    @Override
    public void interrupt() {
        try {
            in.close();
        } catch (IOException e) {
            Log.d("MyTag", "Oops...");
            e.printStackTrace();
        }
        super.interrupt();
    }
}
