package com.example.presamsungproject.Activities.Start;

import androidx.fragment.app.Fragment;
import com.example.presamsungproject.Activities.General.FragmentListener;
import com.example.presamsungproject.Models.GameOptions;

public interface StartActivityFragmentListener extends FragmentListener {
    void addFragment(Fragment fragment);

    void removeFragment(Fragment fragment);

    void startLobbyFragment(String name, boolean isLobbyCreator);

    void notifyGameCreating(String name, GameOptions gameOptions, boolean isLobby);

    void gameOptionsChanged(GameOptions gameOptions);
}
