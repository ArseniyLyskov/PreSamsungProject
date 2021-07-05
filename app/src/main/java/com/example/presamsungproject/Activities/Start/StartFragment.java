package com.example.presamsungproject.Activities.Start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.example.presamsungproject.ConnectionObjects.Assistive.ExternalAddressFinder;
import com.example.presamsungproject.Models.InfoSingleton;
import com.example.presamsungproject.Models.Resources;
import com.example.presamsungproject.Models.SoundEffects;
import com.example.presamsungproject.R;

public class StartFragment extends Fragment {
    private StartActivityFragmentListener SAFListener;
    private AboutFragment aboutFragment;
    private EditText editText;

    public void setParams(StartActivityFragmentListener SAFListener) {
        this.SAFListener = SAFListener;
        aboutFragment = new AboutFragment();
        aboutFragment.setParams(SAFListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, null);

        editText = v.findViewById(R.id.fs_edittext);
        Button joinButton = v.findViewById(R.id.fs_join_button);
        Button createButton = v.findViewById(R.id.fs_create_button);
        Button aboutButton = v.findViewById(R.id.fs_about_button);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                if (!isNickNameEntered() || !isConnectedToLocalNetwork())
                    return;
                SAFListener.startLobbyFragment(editText.getText().toString(), false);
                InfoSingleton.getInstance().setLobby(false);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                if (!isNickNameEntered() || !isConnectedToLocalNetwork())
                    return;
                SAFListener.startLobbyFragment(editText.getText().toString(), true);
                InfoSingleton.getInstance().setLobby(true);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffects.getInstance().executeEffect(SoundEffects.CLICK);
                if (!aboutFragment.isAdded())
                    SAFListener.addFragment(aboutFragment);
                else
                    SAFListener.removeFragment(aboutFragment);
            }
        });

        return v;
    }

    private boolean isNickNameEntered() {
        if (editText.getText().toString().equals("")) {
            Resources.getInstance().getPListener().showProblem("You didn't enter your nickname");
            return false;
        } else if (editText.getText().toString().contains(" ")) {
            Resources.getInstance().getPListener().showProblem("Your nickname mustn't contain a spaces");
            return false;
        } else return true;
    }

    private boolean isConnectedToLocalNetwork() {
        boolean connected = false;
        if (InfoSingleton.getInstance().getEXTERNAL_ADDRESS() != null)
            connected = InfoSingleton.getInstance().getEXTERNAL_ADDRESS().length() > 4
                    && InfoSingleton.getInstance().getEXTERNAL_ADDRESS().length() < 16;
        if (!connected) {
            Resources.getInstance().getPListener().showProblem("Error. Check your connection to Wi-Fi.");
            ExternalAddressFinder.tryToFind(getContext());
        }
        return connected;
    }
}
