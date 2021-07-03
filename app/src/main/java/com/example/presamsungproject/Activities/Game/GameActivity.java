package com.example.presamsungproject.Activities.Game;

import android.graphics.Color;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.Models.Game;
import com.example.presamsungproject.Models.Resources;
import com.example.presamsungproject.Models.SoundEffects;
import com.example.presamsungproject.R;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class GameActivity extends AppCompatActivity
        implements GameUIUpdateListener {
    private Game game;
    private JoystickView jstickL, jstickR;
    private TextView fps_tv;
    private FrameLayout drawViewContainer, fragmentContainer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        game = MessageManager.getCurrentGame();

        drawActivity();

        SoundEffects.getInstance().executeEffect(SoundEffects.TRACK);

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
    }

    @Override
    protected void onPause() {
        SoundEffects.getInstance().pauseEffects();
        super.onPause();
    }

    private void drawActivity() {
        drawViewContainer = findViewById(R.id.ag_draw_view_container);
        fragmentContainer = findViewById(R.id.ag_fragment_container);

        drawViewContainer.addView(new DrawView(getApplicationContext()));

        jstickL = findViewById(R.id.ag_jstickL);
        jstickL.setButtonColor(Color.argb(41, 0, 0, 0));
        jstickL.setButtonSizeRatio(0.35f);
        jstickL.setBackgroundSizeRatio(0.6f);
        jstickL.setBackgroundColor(Color.argb(25, 0, 0, 0));
        jstickL.setBorderWidth(5);
        jstickL.setFixedCenter(true);

        jstickR = findViewById(R.id.ag_jstickR);
        jstickR.setButtonColor(Color.argb(41, 0, 0, 0));
        jstickR.setButtonSizeRatio(0.35f);
        jstickR.setBackgroundSizeRatio(0.6f);
        jstickR.setBackgroundColor(Color.argb(25, 0, 0, 0));
        jstickR.setBorderWidth(5);
        jstickR.setFixedCenter(true);

        fps_tv = findViewById(R.id.ag_fps_tv);

        Resources.getInstance().setGUIUListener(this);
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
}