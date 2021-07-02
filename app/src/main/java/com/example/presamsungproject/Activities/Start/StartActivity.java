package com.example.presamsungproject.Activities.Start;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.presamsungproject.Activities.Game.GameActivity;
import com.example.presamsungproject.Activities.ProblemFragment;
import com.example.presamsungproject.ConnectionObjects.Assistive.ExternalAddressFinder;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.Models.GameOptions;
import com.example.presamsungproject.Models.MySingletons;
import com.example.presamsungproject.Models.MySoundEffects;
import com.example.presamsungproject.MyInterfaces.StartActivityFragmentListener;
import com.example.presamsungproject.MyInterfaces.StartActivityMessageListener;
import com.example.presamsungproject.R;

public class StartActivity extends AppCompatActivity
        implements StartActivityFragmentListener, StartActivityMessageListener {
    private Fragment aboutFragment;
    private ServerFragment serverFragment;
    private ClientFragment clientFragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (savedInstanceState == null)
            return;

        MySingletons.createMyResources(getApplicationContext());

        ExternalAddressFinder.tryToFind(getApplicationContext());

        MySingletons.getMyResources().getSFXInterface().executeEffect(MySoundEffects.MAIN_THEME);

        MySingletons.getMyResources().setSAMListener(this);

        ImageView backgroundImage = findViewById(R.id.as_background_image);
        backgroundImage.setImageBitmap(MySingletons.getMyResources().getPaintedWallPaper());
        backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);

        Fragment startFragment = new StartFragment(this);
        aboutFragment = new AboutFragment(this);
        addFragment(startFragment);
    }

    private void gotoGameActivity() {
        MySingletons.getMyResources().getSFXInterface().stopEffect(MySoundEffects.MAIN_THEME);
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void aboutClick(View view) {
        MySingletons.getMyResources().getSFXInterface().executeEffect(MySoundEffects.CLICK);
        if (!aboutFragment.isAdded())
            addFragment(aboutFragment);
        else
            removeFragment(aboutFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MySingletons.getMyResources() != null)
            MySingletons.getMyResources().getSFXInterface().resumeEffects();
    }

    @Override
    protected void onPause() {
        if (MySingletons.getMyResources() != null)
            MySingletons.getMyResources().getSFXInterface().pauseEffects();
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
            serverFragment = new ServerFragment(this, name);
            addFragment(serverFragment);
        } else {
            clientFragment = new ClientFragment(this, name);
            addFragment(clientFragment);
        }
    }

    @Override
    public void notifyGameStarting(String name, GameOptions gameOptions, boolean isLobby) {
        MySingletons.startGame(name, gameOptions);
        MySingletons.getGame().start();
        MessageManager.setGame(MySingletons.getGame());
        gotoGameActivity();
        if (isLobby) {
            try {
                for (String address : serverFragment.getPlayers().keySet()) {
                    if (!address.equals(MessageManager.EXTERNAL_ADDRESS)) {
                        GameOptions versionForAnotherPlayer = gameOptions.getVersionForAnotherPlayer();
                        String message = MessageManager.sendGameOptionsMessage(versionForAnotherPlayer);
                        MySingletons.getServer().specificMessage(address, message);
                    }
                }
            } catch (Exception e) {
                Log.d("MyTag", "Error during serializing gameOptions");
                e.printStackTrace();
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
    public void clientUpdateUI(String nicks, int players_quantity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clientFragment.updateUI(nicks, players_quantity);
            }
        });
    }

    @Override
    public void clientStartGame(GameOptions gameOptions) {
        clientFragment.startGame(gameOptions);
    }
}