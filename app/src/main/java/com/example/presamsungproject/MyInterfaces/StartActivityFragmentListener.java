package com.example.presamsungproject.MyInterfaces;

import androidx.fragment.app.Fragment;
import com.example.presamsungproject.Models.Map;

public interface StartActivityFragmentListener {
    void addFragment(Fragment fragment);

    void removeFragment(Fragment fragment);

    void showProblem(String text);

    void startLobbyFragment(String name, boolean isLobbyCreator);

    void notifyGameCreating(Map map, String name, int team);
}
