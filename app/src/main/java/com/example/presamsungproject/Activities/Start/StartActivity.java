package com.example.presamsungproject.Activities.Start;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.presamsungproject.Activities.Game.GameActivity;
import com.example.presamsungproject.Activities.General.LoadingFragment;
import com.example.presamsungproject.Activities.General.ProblemFragment;
import com.example.presamsungproject.ConnectionObjects.Assistive.ExternalAddressFinder;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.Models.*;
import com.example.presamsungproject.R;

public class StartActivity extends AppCompatActivity
        implements StartActivityFragmentListener, StartActivityMessageListener {
    private ServerFragment serverFragment;
    private ClientFragment clientFragment;
    private LoadingFragment loadingFragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Resources.createInstance(getApplicationContext());
        InfoSingleton.createInstance();
        ExternalAddressFinder.tryToFind(getApplicationContext());
        SoundEffects.getInstance().executeEffect(SoundEffects.MAIN_THEME);
        Resources.getInstance().setSAMListener(this);

        ImageView backgroundImage = findViewById(R.id.as_background_image);
        backgroundImage.setImageBitmap(Resources.getInstance().getPaintedWallPaper());
        backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);

        StartFragment startFragment = new StartFragment();
        startFragment.setParams(this);
        loadingFragment = new LoadingFragment();
        addFragment(startFragment);
    }

    private void gotoGameActivity() {
        SoundEffects.getInstance().stopEffect(SoundEffects.MAIN_THEME);
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundEffects.getInstance().resumeEffects();
    }

    @Override
    protected void onPause() {
        SoundEffects.getInstance().pauseEffects();
        super.onPause();
    }

    @Override
    public void addFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.as_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void removeFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void showProblem(String text) {
        StartActivityFragmentListener SAFListener = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProblemFragment problemFragment = new ProblemFragment(SAFListener, text);
                addFragment(problemFragment);
            }
        });
    }

    @Override
    public void startLobbyFragment(String name, boolean isLobbyCreator) {
        if (isLobbyCreator) {
            serverFragment = new ServerFragment();
            serverFragment.setParams(this, name);
            addFragment(serverFragment);
        } else {
            clientFragment = new ClientFragment();
            clientFragment.setParams(this, name);
            addFragment(clientFragment);
        }
    }

    @Override
    public void notifyGameCreating(String name, GameOptions gameOptions, boolean isLobby) {
        addFragment(loadingFragment);
        Game game = new Game(name, gameOptions);
        MessageManager.updateGame(game);
        if (isLobby) {
            try {
                for (String address : serverFragment.getPlayers().keySet()) {
                    if (!address.equals(InfoSingleton.getInstance().getEXTERNAL_ADDRESS())) {
                        GameOptions versionForAnotherPlayer = gameOptions.getVersionForAnotherPlayer();
                        String message = MessageManager.sendGameOptionsMessage(versionForAnotherPlayer);
                        Server.getInstance().specificMessage(address, message);
                    }
                }
            } catch (Exception e) {
                Log.d("MyTag", "Error during serializing gameOptions");
                e.printStackTrace();
            }
            if (Server.getInstance().getConnectionsQuantity() == 0) {
                notifyGameStarting();
            }
        }
    }

    @Override
    public void gameOptionsChanged(GameOptions gameOptions) {
        serverFragment.setGameOptions(gameOptions);
    }

    @Override
    public void serverAddPlayer(String address, String name) {
        serverFragment.addPlayer(address, name);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverFragment.updateUI();
            }
        });
    }

    @Override
    public void serverRemovePlayer(String address) {
        serverFragment.removePlayer(address);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverFragment.updateUI();
            }
        });
    }

    @Override
    public boolean serverIsLastPrepared(String address) {
        return serverFragment.isLastPrepared(address);
    }

    @Override
    public void clientUpdateUI(String nicks, int players_quantity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clientFragment.updateUI(nicks, players_quantity);
            }
        });
    }

    @Override
    public void clientCreateGame(GameOptions gameOptions) {
        clientFragment.createGame(gameOptions);
    }

    @Override
    public void notifyGameStarting() {
        removeFragment(loadingFragment);
        gotoGameActivity();
    }
}