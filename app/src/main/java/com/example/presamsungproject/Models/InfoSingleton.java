package com.example.presamsungproject.Models;

public class InfoSingleton {
    private static InfoSingleton instance = null;
    private String EXTERNAL_ADDRESS = null;
    private boolean isLobby;

    public static void createInstance() {
        if (instance == null)
            instance = new InfoSingleton();
    }

    public static InfoSingleton getInstance() {
        return instance;
    }

    public String getEXTERNAL_ADDRESS() {
        return EXTERNAL_ADDRESS;
    }

    public void setEXTERNAL_ADDRESS(String EXTERNAL_ADDRESS) {
        this.EXTERNAL_ADDRESS = EXTERNAL_ADDRESS;
    }

    public boolean isLobby() {
        return isLobby;
    }

    public void setLobby(boolean lobby) {
        isLobby = lobby;
    }
}
