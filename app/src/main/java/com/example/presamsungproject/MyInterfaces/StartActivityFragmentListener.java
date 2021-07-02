package com.example.presamsungproject.MyInterfaces;

import androidx.fragment.app.Fragment;
import com.example.presamsungproject.Models.GameOptions;

public interface StartActivityFragmentListener {
    void addFragment(Fragment fragment);

    void removeFragment(Fragment fragment);

    void showProblem(String text);

    void startLobbyFragment(String name, boolean isLobbyCreator);

    void notifyGameCreating(String name, GameOptions gameOptions, boolean isLobby);

    void gameOptionsChanged(GameOptions gameOptions);
}
