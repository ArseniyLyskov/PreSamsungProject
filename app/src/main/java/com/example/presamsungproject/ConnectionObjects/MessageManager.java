package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.Game;
import com.example.presamsungproject.GameObjects.Tank;
import com.example.presamsungproject.Map;
import com.example.presamsungproject.MyInterfaces.ServerUpdatableUI;
import com.example.presamsungproject.MySingletons;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class MessageManager {
    private static final String CONNECT_MESSAGE = "CONNECTED";
    private static final String NAMES_LIST_MESSAGE = "NAMES_LIST";
    private static final String TEAM_MESSAGE = "TEAM";
    private static final String SENDING_MAP_MESSAGE = "SENDING_MAP";
    private static final String SENDING_TANK_MESSAGE = "SENDING_TANK";
    private static final String READY_MESSAGE = "READY";
    private static final String HIT_MESSAGE = "HIT";
    private static Game game;
    public static String EXTERNAL_ADDRESS = null;

    public static String connectMessage(String name) {
        return CONNECT_MESSAGE + " " + EXTERNAL_ADDRESS + " " + name;
    }

    public static String sendMapMessage(Map map) throws Exception {
        String serializedMap = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(map);
        serializedMap = Arrays.toString(baos.toByteArray());
        if (serializedMap == null)
            throw new Exception();
        return SENDING_MAP_MESSAGE + " " + serializedMap;
    }

    public static String sendTankMessage(Tank tank) throws Exception {
        String serializedTank = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(tank);
        serializedTank = Arrays.toString(baos.toByteArray());
        if (serializedTank == null)
            throw new Exception();
        return SENDING_TANK_MESSAGE + " " + EXTERNAL_ADDRESS + " " + serializedTank;
    }

    public static String hitMessage(String address) {
        return HIT_MESSAGE + " " + address;
    }

    public static void serverProcessMessage(String message) {
        String[] separated = message.split(" ");
        switch (separated[0]) {
            case CONNECT_MESSAGE: {
                ServerUpdatableUI serverUpdatableUI = MySingletons.getMyResources().getServerUpdatableUI();
                HashMap<String, String> players = serverUpdatableUI.getPlayers();
                players.put(separated[1], separated[2]);
                serverUpdatableUI.updateUI();
                String messageToAll = "";
                messageToAll = NAMES_LIST_MESSAGE + " ";
                String namesText = "";
                for (String s : players.values()) {
                    namesText += s + " ";
                }
                messageToAll += namesText;
                MySingletons.getServer().broadcastMessage(messageToAll);
                MySingletons.getServer().specificMessage(separated[1], TEAM_MESSAGE + " " + players.size());
                break;
            }
            case SENDING_TANK_MESSAGE: {
                while (game == null) {
                }
                Tank deserializedTank = deserializeTank(message, separated[1]);
                game.otherTanks.put(separated[1], deserializedTank);
                if ((game.otherTanks.size()) == MySingletons.getServer().getConnectionsQuantity()) {
                    if (!game.isEverybodyReady) {
                        game.isEverybodyReady = true;
                        serverBroadcastAllTanks();
                        MySingletons.getServer().broadcastMessage(READY_MESSAGE);
                    } else {
                        MySingletons.getServer().broadcastMessage(message);
                    }
                }
                break;
            }
            case HIT_MESSAGE: {
                if (separated[1].equals(EXTERNAL_ADDRESS)) {
                    game.getMyTank().minusHealth();
                    sendMyTank();
                } else
                    MySingletons.getServer().specificMessage(separated[1], hitMessage(separated[1]));
                break;
            }
        }
    }

    public static void clientProcessMessage(String message) {
        String[] separated = message.split(" ");
        switch (separated[0]) {
            case NAMES_LIST_MESSAGE: {
                String namesText = "";
                for (int i = 1; i < separated.length; i++) {
                    namesText += separated[i] + "\n";
                }
                MySingletons.getMyResources().getClientUpdatableUI().updateUI(namesText, separated.length - 1);
                break;
            }
            case SENDING_MAP_MESSAGE: {
                Map deserializedMap = deserializeMap(message);
                MySingletons.getMyResources().getClientUpdatableUI().startGame(deserializedMap);
                break;
            }
            case TEAM_MESSAGE: {
                MySingletons.getMyResources().getClientUpdatableUI().setTeam(Integer.parseInt(separated[1]));
                break;
            }
            case SENDING_TANK_MESSAGE: {
                if (!separated[1].equals(EXTERNAL_ADDRESS)) {
                    Tank deserializedTank = deserializeTank(message, separated[1]);
                    game.otherTanks.put(separated[1], deserializedTank);
                }
                break;
            }
            case READY_MESSAGE: {
                game.isEverybodyReady = true;
                break;
            }
            case HIT_MESSAGE: {
                if (separated[1].equals(EXTERNAL_ADDRESS)) {
                    game.getMyTank().minusHealth();
                    sendMyTank();
                }
                break;
            }
        }
    }

    public static void sendMyTank() {
        try {
            if (MySingletons.isLobby()) {
                MySingletons.getServer().broadcastMessage(MessageManager.sendTankMessage(game.getMyTank().getTankToSerialize()));
            } else {
                MySingletons.getClient().sendMessage(MessageManager.sendTankMessage(game.getMyTank().getTankToSerialize()));
            }
        } catch (Exception e) {
            Log.d("MyTag", "Sending myTank error");
            e.printStackTrace();
        }
    }

    private static void serverBroadcastAllTanks() {
        try {
            for (Tank tank : game.otherTanks.values()) {
                MySingletons.getServer().broadcastMessage(sendTankMessage(tank));
            }
            MySingletons.getServer().broadcastMessage(sendTankMessage(game.getMyTank().getTankToSerialize()));
        } catch (Exception e) {
            Log.d("MyTag", "Server sending tank error");
            e.printStackTrace();
        }
    }

    public static void tryToFindExternalAddress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket testConnection = new Socket();
                    testConnection.connect(new InetSocketAddress("www.google.com", 80));
                    //EXTERNAL_ADDRESS = testConnection.getLocalAddress().toString();
                    EXTERNAL_ADDRESS = testConnection.getLocalAddress().getHostAddress();
                    Log.d("MyTag", "ExternalAddress: " + EXTERNAL_ADDRESS);
                    testConnection.close();
                } catch (Exception e) {
                    Log.d("MyTag", "Getting external address error.");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void setGame(Game game) {
        MessageManager.game = game;
    }

    private static Map deserializeMap(String message) {
        String temp = message.replaceFirst(SENDING_MAP_MESSAGE + " ", "");
        String[] byteValues = temp.substring(1, temp.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];
        for (int i = 0, len = bytes.length; i < len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois;
        Map deserializedMap;
        try {
            ois = new ObjectInputStream(bais);
            deserializedMap = (Map) ois.readObject();
            return deserializedMap;
        } catch (Exception e) {
            Log.d("MyTag", "Deserializing map error");
            e.printStackTrace();
        }
        return null;
    }

    public static Tank deserializeTank(String message, String address) {
        String temp = message.replaceFirst(SENDING_TANK_MESSAGE + " " + address + " ", "");
        String[] byteValues = temp.substring(1, temp.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];
        for (int i = 0, len = bytes.length; i < len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois;
        Tank deserializedTank;
        try {
            ois = new ObjectInputStream(bais);
            deserializedTank = (Tank) ois.readObject();
            deserializedTank.scaleTo(game.getScale() / (float) deserializedTank.getScale());
            return deserializedTank;
        } catch (Exception e) {
            Log.d("MyTag", "Deserializing tank error");
            e.printStackTrace();
        }
        return null;
    }
}

