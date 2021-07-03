package com.example.presamsungproject.Activities.Start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.Models.*;
import com.example.presamsungproject.R;

import java.util.HashMap;
import java.util.HashSet;

public class ServerFragment extends Fragment {
    private String name;
    private StartActivityFragmentListener SAFListener;
    private final HashMap<String, String> players = new HashMap<>();
    private final HashSet<String> prepared = new HashSet<>();
    private TextView number, nicks;
    private GameOptions gameOptions = null;
    private GameOptionsFragment gameOptionsFragment;

    public void setParams(StartActivityFragmentListener SAFListener, String name) {
        this.SAFListener = SAFListener;
        this.name = name;
        gameOptionsFragment = new GameOptionsFragment();
        gameOptionsFragment.setParams(SAFListener, players);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lobby_server, null);

        TextView ip = v.findViewById(R.id.fls_ip);
        ip.setText(InfoSingleton.getInstance().getEXTERNAL_ADDRESS());
        number = v.findViewById(R.id.fls_number);
        nicks = v.findViewById(R.id.fls_nicks);
        Button startGame = v.findViewById(R.id.fls_button);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                if (gameOptions == null) {
                    MapOptions mapOptions = new MapOptions(1, 15, 15, 25, 25, 20, 40);
                    Map map = new Map(players.size(), mapOptions);
                    gameOptions = new GameOptions(map, players.size(), 3, true, false);
                }
                SAFListener.notifyGameCreating(name, gameOptions, true);
                prepared.add(InfoSingleton.getInstance().getEXTERNAL_ADDRESS());
            }
        });
        Button back = v.findViewById(R.id.fls_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                SAFListener.removeFragment(ServerFragment.this);
            }
        });
        Button params = v.findViewById(R.id.fls_params);
        params.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                if (gameOptionsFragment.isAdded())
                    SAFListener.removeFragment(gameOptionsFragment);
                else
                    SAFListener.addFragment(gameOptionsFragment);
            }
        });
        players.put(InfoSingleton.getInstance().getEXTERNAL_ADDRESS(), name);
        updateUI();

        Server.createInstance();

        return v;
    }

    @Override
    public void onDestroy() {
        if (gameOptionsFragment.isAdded()) {
            SAFListener.removeFragment(gameOptionsFragment);
        }
        players.clear();
        updateUI();
        Server.getInstance().stop();
        super.onDestroy();
    }

    public void updateUI() {
        number.setText("Number of people waiting: " + players.size());
        StringBuilder namesText = new StringBuilder();
        for (String s : players.values()) {
            namesText.append(s).append("\n");
        }
        nicks.setText(namesText.toString());
    }

    public void addPlayer(String address, String name) {
        players.put(address, name);
        String messageToAll = MessageManager.namesListMessage(getNamesString());
        Server.getInstance().broadcastMessage(messageToAll);
    }

    public void removePlayer(String address) {
        players.remove(address);
        prepared.remove(address);
        String messageToAll = MessageManager.namesListMessage(getNamesString());
        Server.getInstance().broadcastMessage(messageToAll);
    }

    public String getNamesString() {
        StringBuilder string = new StringBuilder();
        for (String s : players.values()) {
            string.append(s).append(" ");
        }
        return string.toString();
    }

    public HashMap<String, String> getPlayers() {
        return players;
    }

    public void setGameOptions(GameOptions gameOptions) {
        this.gameOptions = gameOptions;
    }

    public boolean isLastPrepared(String address) {
        prepared.add(address);
        return prepared.size() == players.size();
    }
}
