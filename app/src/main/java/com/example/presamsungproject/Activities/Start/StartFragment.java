package com.example.presamsungproject.Activities.Start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.ConnectionObjects.Assistive.ExternalAddressFinder;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.Models.MySingletons;
import com.example.presamsungproject.Models.MySoundEffects;
import com.example.presamsungproject.MyInterfaces.StartActivityFragmentListener;
import com.example.presamsungproject.R;

public class StartFragment extends Fragment {
    private final StartActivityFragmentListener SAFListener;
    private EditText editText;

    public StartFragment(StartActivityFragmentListener SAFListener) {
        this.SAFListener = SAFListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, null);

        editText = v.findViewById(R.id.fs_edittext);
        Button joinButton = v.findViewById(R.id.fs_join_button);
        Button createButton = v.findViewById(R.id.fs_create_button);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySingletons.getMyResources().getSFXInterface().executeEffect(MySoundEffects.CLICK);
                if (!isNickNameEntered() || !isConnectedToLocalNetwork())
                    return;
                SAFListener.startLobbyFragment(editText.getText().toString(), false);
                MySingletons.setLobby(false);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySingletons.getMyResources().getSFXInterface().executeEffect(MySoundEffects.CLICK);
                if (!isNickNameEntered() || !isConnectedToLocalNetwork())
                    return;
                SAFListener.startLobbyFragment(editText.getText().toString(), true);
                MySingletons.setLobby(true);
            }
        });

        return v;
    }

    private boolean isNickNameEntered() {
        if (editText.getText().toString().equals("")) {
            SAFListener.showProblem("You didn't enter your nickname");
            return false;
        } else if (editText.getText().toString().contains(" ")) {
            SAFListener.showProblem("Your nickname mustn't contain a spaces");
            return false;
        } else return true;
    }

    private boolean isConnectedToLocalNetwork() {
        boolean connected = false;
        if (MessageManager.EXTERNAL_ADDRESS != null)
            connected = MessageManager.EXTERNAL_ADDRESS.length() > 4
                    && MessageManager.EXTERNAL_ADDRESS.length() < 16;
        if (!connected) {
            SAFListener.showProblem("Error. Check your connection to Wi-Fi.");
            ExternalAddressFinder.tryToFind(getContext());
        }
        return connected;
    }
}
