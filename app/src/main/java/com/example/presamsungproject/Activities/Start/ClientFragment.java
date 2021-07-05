package com.example.presamsungproject.Activities.Start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.ConnectionObjects.Assistive.ConnectionToServerTester;
import com.example.presamsungproject.ConnectionObjects.Client;
import com.example.presamsungproject.Models.GameOptions;
import com.example.presamsungproject.Models.Resources;
import com.example.presamsungproject.Models.SoundEffects;
import com.example.presamsungproject.R;

public class ClientFragment extends Fragment {
    private String name;
    private StartActivityFragmentListener SAFListener;
    private TextView upper_text, number, nicks;
    private EditText editText;
    private Button button;

    public void setParams(StartActivityFragmentListener SAFListener, String name) {
        this.SAFListener = SAFListener;
        this.name = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lobby_client, null);

        upper_text = v.findViewById(R.id.flc_upper_text);
        number = v.findViewById(R.id.flc_number);
        nicks = v.findViewById(R.id.flc_nicks);
        editText = v.findViewById(R.id.flc_edittext);
        button = v.findViewById(R.id.flc_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                if (button.getText().toString().equals("Connect to lobby via ip")) {
                    String serverIP = editText.getText().toString();
                    if (serverIP.equals("")) {
                        Resources.getInstance().getPListener().showProblem("You didn't enter server IP");
                        return;
                    }
                    ConnectionToServerTester.testConnection(serverIP, name, 500, true);
                } else if (button.getText().toString().equals("Disconnect from lobby")) {
                    Client.getInstance().stop();
                }
            }
        });
        Button back = v.findViewById(R.id.flc_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                SAFListener.removeFragment(ClientFragment.this);
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        updateUI("", 0);
        if(Client.getInstance() != null)
            Client.getInstance().pause();
        super.onDestroy();
    }

    public void updateUI(String nicksString, int players_quantity) {
        if (nicksString.equals("") || players_quantity == 0) {
            upper_text.setText("Enter lobby creator IP");
            number.setText("Number of people waiting: ");
            number.setVisibility(View.INVISIBLE);
            editText.setText("");
            editText.setHint("Enter here");
            editText.setEnabled(true);
            button.setText("Connect to lobby via ip");
            nicks.setText("");
        } else {
            upper_text.setText("Waiting for the game to start...");
            number.setText("Number of people waiting: " + players_quantity);
            number.setVisibility(View.VISIBLE);
            editText.setText("");
            editText.setHint("");
            editText.setEnabled(false);
            button.setText("Disconnect from lobby");
            nicks.setText(nicksString);
        }
    }

    public void createGame(GameOptions gameOptions) {
        SAFListener.notifyGameCreating(name, gameOptions, false);
    }
}