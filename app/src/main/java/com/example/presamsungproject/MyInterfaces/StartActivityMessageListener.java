package com.example.presamsungproject.MyInterfaces;

import com.example.presamsungproject.Models.Map;

import java.util.HashMap;

public interface StartActivityMessageListener {
    void serverAddPlayer(String address, String name);

    String serverGetNamesString();

    void clientUpdateUI(String nicks, int players_quantity);

    HashMap<String, String> serverGetPlayers();

    void clientStartGame(Map map);

    void clientSetTeam(int team);
}
