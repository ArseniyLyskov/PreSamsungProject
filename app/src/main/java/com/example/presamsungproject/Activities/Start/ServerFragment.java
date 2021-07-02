package com.example.presamsungproject.Activities.Start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.Models.*;
import com.example.presamsungproject.MyInterfaces.StartActivityFragmentListener;
import com.example.presamsungproject.R;

import java.util.HashMap;

public class ServerFragment extends Fragment {
    private final String name;
    private final StartActivityFragmentListener SAFListener;
    private final HashMap<String, String> players = new HashMap<>();
    private TextView number, nicks;
    private GameOptions gameOptions = null;
    private final GameOptionsFragment gameOptionsFragment;

    public ServerFragment(StartActivityFragmentListener SAFListener, String name) {
        this.SAFListener = SAFListener;
        this.name = name;
        gameOptionsFragment = new GameOptionsFragment(SAFListener, players);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lobby_server, null);

        TextView ip = v.findViewById(R.id.fls_ip);
        ip.setText(MessageManager.EXTERNAL_ADDRESS);
        number = v.findViewById(R.id.fls_number);
        nicks = v.findViewById(R.id.fls_nicks);
        Button startGame = v.findViewById(R.id.fls_button);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySingletons.getMyResources().getSFXInterface().executeEffect(MySoundEffects.CLICK);
                if (gameOptions == null) {
                    MapOptions mapOptions = new MapOptions(1, 15, 15, 25, 25, 20, 40);
                    Map map = new Map(players.size(), mapOptions);
                    gameOptions = new GameOptions(map, players.size(), 3, true, false);
                }
                SAFListener.notifyGameStarting(name, gameOptions, true);
            }
        });
        Button back = v.findViewById(R.id.fls_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySingletons.getMyResources().getSFXInterface().executeEffect(MySoundEffects.CLICK);
                SAFListener.removeFragment(ServerFragment.this);
            }
        });
        Button params = v.findViewById(R.id.fls_params);
        params.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySingletons.getMyResources().getSFXInterface().executeEffect(MySoundEffects.CLICK);
                if (gameOptionsFragment.isAdded())
                    SAFListener.removeFragment(gameOptionsFragment);
                else
                    SAFListener.addFragment(gameOptionsFragment);
            }
        });
        players.put(MessageManager.EXTERNAL_ADDRESS, name);
        updateUI();

        MySingletons.createServer();

        return v;
    }

    @Override
    public void onDestroy() {
        if (gameOptionsFragment.isAdded())
            SAFListener.removeFragment(gameOptionsFragment);
        if (MySingletons.getServer() != null && MySingletons.getServer().isRunning()) {
            players.clear();
            updateUI();
            MySingletons.getServer().stop();
        }
        super.onDestroy();
    }

    public void updateUI() {
        number.setText("Number of people waiting: " + players.size());
        String namesText = "";
        for (String s : players.values()) {
            namesText += s + "\n";
        }
        nicks.setText(namesText);
    }

    public void addPlayer(String address, String name) {
        players.put(address, name);
        String messageToAll = MessageManager.namesListMessage(getNamesString());
        MySingletons.getServer().broadcastMessage(messageToAll);
    }

    public void removePlayer(String address) {
        players.remove(address);
        String messageToAll = MessageManager.namesListMessage(getNamesString());
        MySingletons.getServer().broadcastMessage(messageToAll);
    }

    public String getNamesString() {
        String string = "";
        for (String s : players.values()) {
            string += s + " ";
        }
        return string;
    }

    public HashMap<String, String> getPlayers() {
        return players;
    }

    public void setGameOptions(GameOptions gameOptions) {
        this.gameOptions = gameOptions;
    }
}
