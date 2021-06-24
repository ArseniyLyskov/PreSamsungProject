package com.example.presamsungproject.MyInterfaces;

import com.example.presamsungproject.Map;

public interface ClientUpdatableUI {
    void updateUI(String nicks, int players_quantity);
    void showToast(String message, int length);
    void startGame(Map map);
    void setTeam(int team);
}
