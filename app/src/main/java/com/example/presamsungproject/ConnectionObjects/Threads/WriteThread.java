package com.example.presamsungproject.ConnectionObjects.Threads;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class WriteThread extends Thread {
    private final ConnectThread parentThread;
    private final Socket socket;
    private final DataOutputStream out;
    private final LinkedList<String> messageQueue;

    public WriteThread(ConnectThread parentThread, Socket socket, DataOutputStream out, LinkedList<String> messageQueue) {
        this.parentThread = parentThread;
        this.socket = socket;
        this.out = out;
        this.messageQueue = messageQueue;
    }

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
                Log.d("MyTraffic", "Sending error\n" + e.toString());
                e.printStackTrace();
            }

            if (toSend.equalsIgnoreCase("end")) {
                parentThread.interrupt();
                break;
            }
        }
    }

    @Override
    public void interrupt() {
        try {
            out.close();
        } catch (IOException e) {
            Log.d("MyTag", "Oops...");
            e.printStackTrace();
        }
        super.interrupt();
    }
}
