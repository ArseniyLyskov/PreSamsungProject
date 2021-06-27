package com.example.presamsungproject.Activities.Start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.Models.Map;
import com.example.presamsungproject.Models.MySingletons;
import com.example.presamsungproject.Models.MySoundEffects;
import com.example.presamsungproject.MyInterfaces.StartActivityFragmentListener;
import com.example.presamsungproject.R;

import java.util.HashMap;

public class ServerFragment extends Fragment {
    private final String name;
    private final StartActivityFragmentListener SAFListener;
    private final HashMap<String, String> players = new HashMap<>();
    private TextView number, nicks;

    public ServerFragment(StartActivityFragmentListener SAFListener, String name) {
        this.SAFListener = SAFListener;
        this.name = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lobby_server, null);

        TextView ip = v.findViewById(R.id.fls_ip);
        ip.setText(MessageManager.EXTERNAL_ADDRESS);
        number = v.findViewById(R.id.fls_number);
        nicks = v.findViewById(R.id.fls_nicks);
        Button button = v.findViewById(R.id.fls_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySingletons.getMyResources().getSFXInterface().executeEffect(MySoundEffects.CLICK);
                Map map = new Map(10, 10, 25, 25);
                SAFListener.notifyGameCreating(map, name, 1);
            }
        });
        addPlayer(MessageManager.EXTERNAL_ADDRESS, name);
        updateUI();

        MySingletons.createServer();

        return v;
    }

    @Override
    public void onDestroy() {
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
}
