package com.example.presamsungproject.MyInterfaces;

import com.example.presamsungproject.Models.GameOptions;

public interface StartActivityMessageListener {
    void serverAddPlayer(String address, String name);

    void serverRemovePlayer(String address);

    void clientUpdateUI(String nicks, int players_quantity);

    void clientStartGame(GameOptions gameOptions);
}