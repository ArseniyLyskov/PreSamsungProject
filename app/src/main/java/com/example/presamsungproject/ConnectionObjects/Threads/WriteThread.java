package com.example.presamsungproject.ConnectionObjects.Threads;

import android.util.Log;

import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
            if (toSend == null || toSend.equals(""))
                continue;
            byte[] bytesToSend = toSend.getBytes(StandardCharsets.UTF_8);

            try {
                out.writeInt(bytesToSend.length);
                out.write(bytesToSend);
                out.flush();
            } catch (Exception e) {
                Log.d("MyTraffic", "Sending error\n" + e.toString());
                messageQueue.addFirst(toSend);
                e.printStackTrace();
                if (!socket.isClosed())
                    parentThread.close(true);
                break;
            }
        }
    }
}
