package com.example.presamsungproject.Activities.Start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.ConnectionObjects.Assistive.ConnectionToServerTester;
import com.example.presamsungproject.Models.Map;
import com.example.presamsungproject.Models.MySingletons;
import com.example.presamsungproject.Models.MySoundEffects;
import com.example.presamsungproject.MyInterfaces.StartActivityFragmentListener;
import com.example.presamsungproject.R;

public class ClientFragment extends Fragment {
    public int team;
    private final String name;
    private final StartActivityFragmentListener SAFListener;
    private TextView upper_text, number, nicks;

    public ClientFragment(StartActivityFragmentListener SAFListener, String name) {
        this.SAFListener = SAFListener;
        this.name = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lobby_client, null);

        upper_text = v.findViewById(R.id.flc_upper_text);
        number = v.findViewById(R.id.flc_number);
        nicks = v.findViewById(R.id.flc_nicks);
        EditText editText = v.findViewById(R.id.flc_edittext);
        Button button = v.findViewById(R.id.flc_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySingletons.getMyResources().getSFXInterface().executeEffect(MySoundEffects.CLICK);
                String serverIP = editText.getText().toString();
                if (serverIP.equals("")) {
                    SAFListener.showProblem("You didn't enter server IP");
                    return;
                }
                ConnectionToServerTester.testConnection(serverIP, true, name, SAFListener);
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        if (MySingletons.getClient() != null) {
            updateUI("", 0);
        }
        super.onDestroy();
    }

    public void updateUI(String nicksString, int players_quantity) {
        if (nicksString.equals("") || players_quantity == 0) {
            upper_text.setText("Enter lobby creator IP");
            number.setText("Number of people waiting: ");
            number.setVisibility(View.INVISIBLE);
            nicks.setText("");
        } else {
            upper_text.setText("Waiting for the game to start...");
            number.setText("Number of people waiting: " + players_quantity);
            number.setVisibility(View.VISIBLE);
            nicks.setText(nicksString);
        }
    }

    public void startGame(Map map) {
        MySingletons.startGame(map, name, team);
    }
}
