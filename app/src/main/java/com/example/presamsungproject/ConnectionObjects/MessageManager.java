package com.example.presamsungproject.ConnectionObjects;

import android.util.Log;
import com.example.presamsungproject.Activities.Start.StartActivityMessageListener;
import com.example.presamsungproject.GameObjects.Tank;
import com.example.presamsungproject.Models.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class MessageManager {
    public static final String SENDING_TANK_MESSAGE = "SENDING_TANK";
    private static final String CONNECT_MESSAGE = "CONNECTED";
    private static final String NAMES_LIST_MESSAGE = "NAMES_LIST";
    private static final String SENDING_GAME_OPTIONS_MESSAGE = "SENDING_GAME_OPTIONS";
    private static final String CLIENT_READY_MESSAGE = "CLIENT_READY";
    private static final String ALL_READY_MESSAGE = "ALL_READY";
    private static final String HIT_MESSAGE = "HIT";
    private static final String SFX_MESSAGE = "SFX";
    private static Game currentGame;

    public static String connectMessage(String name) {
        return CONNECT_MESSAGE + " " + InfoSingleton.getInstance().getEXTERNAL_ADDRESS() + " " + name;
    }

    public static void sendSFX(int effect) {
        if (InfoSingleton.getInstance().isLobby()) {
            SoundEffects.getInstance().executeEffect(effect);
            Server.getInstance().broadcastMessage(SFX_MESSAGE + " " + effect);
        } else {
            Client.getInstance().sendMessage(SFX_MESSAGE + " " + effect);
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
        return SENDING_TANK_MESSAGE + " " + InfoSingleton.getInstance().getEXTERNAL_ADDRESS() + " " + serializedTank;
    }

    public static String hitMessage(String address) {
        return HIT_MESSAGE + " " + address;
    }

    public static String namesListMessage(String namesString) {
        return NAMES_LIST_MESSAGE + " " + namesString;
    }

    public static String allReadyMessage() {
        return ALL_READY_MESSAGE;
    }

    public static String clientReadyMessage() {
        return CLIENT_READY_MESSAGE + " " + InfoSingleton.getInstance().getEXTERNAL_ADDRESS();
    }

    public static void serverProcessMessage(String message) { //TODO: game ending
        String[] separated = message.split(" ");
        switch (separated[0]) {
            case CONNECT_MESSAGE: {
                StartActivityMessageListener SAMListener = Resources.getInstance().getSAMListener();
                SAMListener.serverAddPlayer(separated[1], separated[2]);
                if(currentGame != null)
                    if(currentGame.getControlledTank() != null)
                        broadcastAllTanks();
                break;
            }
            case CLIENT_READY_MESSAGE: {
                if (Resources.getInstance().getSAMListener().serverIsLastPrepared(separated[1])) {
                    Server.getInstance().broadcastMessage(allReadyMessage());
                    Resources.getInstance().getSAMListener().notifyGameStarting();
                    sendControlledTank();
                }
                break;
            }
            case SENDING_TANK_MESSAGE: {
                Tank deserializedTank = deserializeTank(message, separated[1]);
                currentGame.getOtherTanks().put(separated[1], deserializedTank);
                Server.getInstance().broadcastMessage(message);
                break;
            }
            case HIT_MESSAGE: {
                if (separated[1].equals(InfoSingleton.getInstance().getEXTERNAL_ADDRESS())) {
                    currentGame.getControlledTank().minusHealth();
                    sendControlledTank();
                } else
                    Server.getInstance().specificMessage(separated[1], hitMessage(separated[1]));
                break;
            }
            case SFX_MESSAGE: {
                SoundEffects.getInstance().executeEffect(Integer.parseInt(separated[1]));
                Server.getInstance().broadcastMessage(message);
                break;
            }
        }
    }

    public static void clientProcessMessage(String message) {
        String[] separated = message.split(" ");
        switch (separated[0]) {
            case NAMES_LIST_MESSAGE: {
                StringBuilder namesText = new StringBuilder();
                for (int i = 1; i < separated.length; i++) {
                    namesText.append(separated[i]).append("\n");
                }
                Resources.getInstance().getSAMListener().clientUpdateUI(namesText.toString(), separated.length - 1);
                break;
            }
            case SENDING_GAME_OPTIONS_MESSAGE: {
                GameOptions deserializedGameOptions = deserializeGameOptions(message);
                Resources.getInstance().getSAMListener().clientCreateGame(deserializedGameOptions);
                break;
            }
            case SENDING_TANK_MESSAGE: {
                if (!separated[1].equals(InfoSingleton.getInstance().getEXTERNAL_ADDRESS())) {
                    Tank deserializedTank = deserializeTank(message, separated[1]);
                    currentGame.getOtherTanks().put(separated[1], deserializedTank);
                }
                break;
            }
            case ALL_READY_MESSAGE: {
                Resources.getInstance().getSAMListener().notifyGameStarting();
                sendControlledTank();
                break;
            }
            case HIT_MESSAGE: {
                if (separated[1].equals(InfoSingleton.getInstance().getEXTERNAL_ADDRESS())) {
                    currentGame.getControlledTank().minusHealth();
                    sendControlledTank();
                }
                break;
            }
            case SFX_MESSAGE: {
                SoundEffects.getInstance().executeEffect(Integer.parseInt(separated[1]));
            }
        }
    }

    public static void sendControlledTank() {
        try {
            if (InfoSingleton.getInstance().isLobby()) {
                Server.getInstance().broadcastMessage(MessageManager.sendTankMessage(currentGame.getControlledTank().getSimpleVersion()));
            } else {
                Client.getInstance().sendMessage(MessageManager.sendTankMessage(currentGame.getControlledTank().getSimpleVersion()));
            }
        } catch (Exception e) {
            Log.d("MyTag", "Sending controlled tank error");
            e.printStackTrace();
        }
    }

    private static void broadcastAllTanks() {
        for (Tank tank : currentGame.getOtherTanks().values()) {
            try {
                Server.getInstance().broadcastMessage(sendTankMessage(tank));
            } catch (Exception e) {
                Log.d("MyTag", "Sending tank error");
                e.printStackTrace();
            }
        }
        sendControlledTank();
    }

    public static void updateGame(Game game) {
        MessageManager.currentGame = game;
    }

    public static Game getCurrentGame() {
        return currentGame;
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
            deserializedTank.scaleTo(currentGame.getScale() / (float) deserializedTank.getScale());
            return deserializedTank;
        } catch (Exception e) {
            Log.d("MyTag", "Deserializing tank error");
            e.printStackTrace();
        }
        return null;
    }
}

