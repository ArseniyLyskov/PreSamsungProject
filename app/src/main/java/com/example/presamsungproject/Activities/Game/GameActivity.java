package com.example.presamsungproject.Activities.Game;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.presamsungproject.Activities.General.FragmentListener;
import com.example.presamsungproject.Activities.General.ProblemFragment;
import com.example.presamsungproject.Activities.General.ProblemListener;
import com.example.presamsungproject.Activities.Start.StartActivity;
import com.example.presamsungproject.ConnectionObjects.Client;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.Models.Game;
import com.example.presamsungproject.Models.InfoSingleton;
import com.example.presamsungproject.Models.Resources;
import com.example.presamsungproject.Models.SoundEffects;
import com.example.presamsungproject.R;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class GameActivity extends AppCompatActivity
        implements GameUIUpdateListener, FragmentListener, ProblemListener {
    private Game game;
    private JoystickView jstickL, jstickR;
    private TextView fps_tv;
    private Vibrator vibrator;
    private ProblemFragment problemFragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        game = MessageManager.getCurrentGame();

        drawActivity();

        SoundEffects.getInstance().executeEffect(SoundEffects.TRACK);

        Resources.getInstance().setPListener(this);

        jstickL.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                game.setlJangle(angle);
                game.setlJstrength(strength);
            }
        });
        jstickR.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                game.setrJangle(angle);
                game.setrJstrength(strength);
            }
        });

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundEffects.getInstance().resumeEffects();
        if (Client.getInstance() != null)
            Client.getInstance().restart();
        if (Server.getInstance() != null)
            Server.getInstance().recreate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundEffects.getInstance().pauseEffects();
        if (Client.getInstance() != null)
            Client.getInstance().pause();
        if (Server.getInstance() != null)
            Server.getInstance().pause();
    }

    private void drawActivity() {
        FrameLayout drawViewContainer = findViewById(R.id.ag_draw_view_container);

        drawViewContainer.addView(new DrawView(getApplicationContext()));

        jstickL = findViewById(R.id.ag_jstickL);
        jstickL.setButtonColor(Color.argb(75, 0, 0, 0));
        jstickL.setButtonSizeRatio(0.35f);
        jstickL.setBackgroundSizeRatio(0.6f);
        jstickL.setBackgroundColor(Color.argb(50, 0, 0, 0));
        jstickL.setBorderWidth(5);
        jstickL.setFixedCenter(true);

        jstickR = findViewById(R.id.ag_jstickR);
        jstickR.setButtonColor(Color.argb(75, 0, 0, 0));
        jstickR.setButtonSizeRatio(0.35f);
        jstickR.setBackgroundSizeRatio(0.6f);
        jstickR.setBackgroundColor(Color.argb(50, 0, 0, 0));
        jstickR.setBorderWidth(5);
        jstickR.setFixedCenter(true);

        fps_tv = findViewById(R.id.ag_fps_tv);

        Resources.getInstance().setGUIUListener(this);
    }

    private void returnToStartActivity() {
        SoundEffects.getInstance().stopEffect(SoundEffects.TRACK);
        if (InfoSingleton.getInstance().isLobby()) {
            if (Server.getInstance() != null)
                Server.getInstance().stop();
        }
        if (!InfoSingleton.getInstance().isLobby()) {
            if (Client.getInstance() != null)
                Client.getInstance().stop();
        }
        MessageManager.updateGame(null);
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finishAndRemoveTask();
    }

    @Override
    public void showFPS(int fps) {
        fps_tv.setText("FPS: " + fps);
    }

    @Override
    public void vibrate(int millis) {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    @Override
    public void showProblem(String text) {
        FragmentListener FListener = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                problemFragment = new ProblemFragment(FListener, text);
                addFragment(problemFragment);
            }
        });
    }

    @Override
    public void addFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.ag_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void removeFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
        if (fragment == problemFragment)
            returnToStartActivity();
    }
}