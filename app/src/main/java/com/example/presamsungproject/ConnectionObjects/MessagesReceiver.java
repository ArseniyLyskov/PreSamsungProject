package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import android.view.View;
import com.example.presamsungproject.Activities.LobbyActivity;
import com.example.presamsungproject.Game;

import java.net.*;

import static com.example.presamsungproject.Activities.LobbyActivity.txtWait;

public class MessagesReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[1024];
    private String address;
    private int port;
    private LobbyActivity lobbyActivity;
    private Game game;

    public void setGame(Game game) {
        this.game = game;
    }

    public void run() {

        try {
            socket = new MulticastSocket(port);
            InetAddress group = InetAddress.getByName(address);
            socket.joinGroup(group);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                Log.d("MyTagReceived", "Received message: " + received);
                if ("end".equals(received)) {
                    break;
                } else {
                    if(lobbyActivity.isLobby)
                        lobbyProcessing(received);
                    else
                        not_lobbyProcessing(received);
                }
            }
            socket.leaveGroup(group);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MyTag", "MessagesReceiver Thread Error");
        }
    }

    public void lobbyProcessing(String received) {
        String[] result = received.split(" ");

        if(result[0].equals(StringConverter.SEND_READY_TO_BATTLE)) {
            lobbyActivity.names.add(result[1]);
            lobbyActivity.ports.add(Integer.valueOf(result[2]));
            String string = StringConverter.createParticipantsList(lobbyActivity.names);

            updateTextViews(string.split(" ").length - 1, StringConverter.getParticipantsList(string));

            for (Integer port : lobbyActivity.ports) {
                BroadcastingMessage message = new BroadcastingMessage(string, "255.255.255.255", port);
                message.start();
            }
        }

        if(result[0].equals("TANK")) {
            Log.d("MyTag2", "" + "nach");
            if(result[1].equals("4444"))
                return;

            if(game.ports.contains(result[1])) {
                Log.d("MyTag2", "" + "est");
            } else {
                game.enemyTanks.add(StringConverter.getTank(received));
                Log.d("MyTag2", "" + "nov");
            }

            for (Integer port : lobbyActivity.ports) {
                BroadcastingMessage message = new BroadcastingMessage(received, "255.255.255.255", port);
                message.start();
            }
        }
    }

    public void not_lobbyProcessing(String received) {
        String[] result = received.split(" ");

        if(result[0].equals(StringConverter.SEND_PARTICIPANTS_LIST)) {
            String update = StringConverter.getParticipantsList(received);
            updateTextViews(result.length - 1, update);
        }

        if(result[0].equals("MAP")) {
            lobbyActivity.goToMainActivity(received);
        }

        if(result[0].equals("TANK")) {
            Log.d("MyTag2", "" + "nach");
            if(result[1].equals(port))
                return;

            if(game.ports.contains(result[1])) {
                Log.d("MyTag2", "" + "est");
            } else {
                game.enemyTanks.add(StringConverter.getTank(received));
                Log.d("MyTag2", "" + "nov");
            }
        }
    }

    public void updateTextViews (int size, String nicks) {
        lobbyActivity.handler.post(new Runnable() {
            @Override
            public void run() {
                lobbyActivity.tv_number.setText(txtWait + size);
                lobbyActivity.tv_nicks.setText(nicks);
            }
        });
    }

    public MessagesReceiver(String address, int port, LobbyActivity lobbyActivity) {
        this.address = address;
        this.port = port;
        this.lobbyActivity = lobbyActivity;
    }
}
