package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.GameObjects.Tank;
import com.example.presamsungproject.Models.Game;
import com.example.presamsungproject.Models.GameOptions;
import com.example.presamsungproject.Models.MySingletons;
import com.example.presamsungproject.MyInterfaces.StartActivityMessageListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class MessageManager {
    private static final String CONNECT_MESSAGE = "CONNECTED";
    private static final String NAMES_LIST_MESSAGE = "NAMES_LIST";
    private static final String SENDING_GAME_OPTIONS_MESSAGE = "SENDING_GAME_OPTIONS";
    private static final String SENDING_TANK_MESSAGE = "SENDING_TANK";
    private static final String READY_MESSAGE = "READY";
    private static final String HIT_MESSAGE = "HIT";
    private static final String SFX_MESSAGE = "SFX";
    private static Game game;
    public static String EXTERNAL_ADDRESS = null;

    public static String connectMessage(String name) {
        return CONNECT_MESSAGE + " " + EXTERNAL_ADDRESS + " " + name;
    }

    public static void sendSFX(int effect) {
        if (MySingletons.isLobby()) {
            MySingletons.getMyResources().getSFXInterface().executeEffect(effect);
            MySingletons.getServer().broadcastMessage(SFX_MESSAGE + " " + effect);
        } else {
            MySingletons.getClient().sendMessage(SFX_MESSAGE + " " + effect);
        }
    }

    public static String sendGameOptionsMessage(GameOptions gameOptions) throws Exception {
        String serializedGameOptions;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(gameOptions);
        serializedGameOptions = Arrays.toString(baos.toByteArray());
        return SENDING_GAME_OPTIONS_MESSAGE + " " + serializedGameOptions;
    }

    public static String sendTankMessage(Tank tank) throws Exception {
        String serializedTank;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(tank);
        serializedTank = Arrays.toString(baos.toByteArray());
        return SENDING_TANK_MESSAGE + " " + EXTERNAL_ADDRESS + " " + serializedTank;
    }

    public static String hitMessage(String address) {
        return HIT_MESSAGE + " " + address;
    }

    public static String namesListMessage(String namesString) {
        return NAMES_LIST_MESSAGE + " " + namesString;
    }

    public static void serverProcessMessage(String message) { //TODO: game ending
        String[] separated = message.split(" ");
        switch (separated[0]) {
            case CONNECT_MESSAGE: {
                StartActivityMessageListener SAMListener = MySingletons.getMyResources().getSAMListener();
                SAMListener.serverAddPlayer(separated[1], separated[2]);
                break;
            }
            case SENDING_TANK_MESSAGE: {
                Tank deserializedTank = deserializeTank(message, separated[1]);
                game.getOtherTanks().put(separated[1], deserializedTank);
                if ((game.getOtherTanks().size()) == MySingletons.getServer().getConnectionsQuantity()) {
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
            case SFX_MESSAGE: {
                MySingletons.getMyResources().getSFXInterface().executeEffect(Integer.parseInt(separated[1]));
                MySingletons.getServer().broadcastMessage(message);
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
                MySingletons.getMyResources().getSAMListener().clientUpdateUI(namesText, separated.length - 1);
                break;
            }
            case SENDING_GAME_OPTIONS_MESSAGE: {
                GameOptions deserializedGameOptions = deserializeGameOptions(message);
                MySingletons.getMyResources().getSAMListener().clientStartGame(deserializedGameOptions);
                break;
            }
            case SENDING_TANK_MESSAGE: {
                if (!separated[1].equals(EXTERNAL_ADDRESS)) {
                    Tank deserializedTank = deserializeTank(message, separated[1]);
                    game.getOtherTanks().put(separated[1], deserializedTank);
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
            case SFX_MESSAGE: {
                MySingletons.getMyResources().getSFXInterface().executeEffect(Integer.parseInt(separated[1]));
            }
        }
    }

    public static void sendMyTank() {
        try {
            if (MySingletons.isLobby()) {
                MySingletons.getServer().broadcastMessage(MessageManager.sendTankMessage(game.getMyTank().getSimpleVersion()));
            } else {
                MySingletons.getClient().sendMessage(MessageManager.sendTankMessage(game.getMyTank().getSimpleVersion()));
            }
        } catch (Exception e) {
            Log.d("MyTag", "Sending myTank error");
            e.printStackTrace();
        }
    }

    private static void serverBroadcastAllTanks() {
        try {
            for (Tank tank : game.getOtherTanks().values()) {
                MySingletons.getServer().broadcastMessage(sendTankMessage(tank));
            }
            MySingletons.getServer().broadcastMessage(sendTankMessage(game.getMyTank().getSimpleVersion()));
        } catch (Exception e) {
            Log.d("MyTag", "Server sending tank error");
            e.printStackTrace();
        }
    }

    public static void setGame(Game game) {
        MessageManager.game = game;
    }

    private static GameOptions deserializeGameOptions(String message) {
        String temp = message.replaceFirst(SENDING_GAME_OPTIONS_MESSAGE + " ", "");
        String[] byteValues = temp.substring(1, temp.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];
        for (int i = 0, len = bytes.length; i < len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois;
        GameOptions deserializedGameOptions;
        try {
            ois = new ObjectInputStream(bais);
            deserializedGameOptions = (GameOptions) ois.readObject();
            return deserializedGameOptions;
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

