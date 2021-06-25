package com.example.presamsungproject.MyInterfaces;

import com.example.presamsungproject.Map;

import java.util.HashMap;

public interface ServerUpdatableUI {
    void updateUI();

    void showToast(String message, int length);

    HashMap<String, String> getPlayers();

    void startGame(Map map);
}
