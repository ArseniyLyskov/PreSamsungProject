package com.example.presamsungproject.Activities.Game;

import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.Models.Game;
import com.example.presamsungproject.Models.MySoundEffects;
import com.example.presamsungproject.MyInterfaces.GameUIUpdateListener;
import com.example.presamsungproject.Models.MySingletons;
import com.example.presamsungproject.R;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class GameActivity extends AppCompatActivity
        implements GameUIUpdateListener {
    private Game game;
    private JoystickView jstickL, jstickR;
    private TextView fps_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (savedInstanceState == null)
            return;

        game = MySingletons.getGame();
        game.start();
        MessageManager.setGame(game);

        drawActivity();

        MySingletons.getMyResources().getSFXInterface().executeEffect(MySoundEffects.TRACK);

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

    private void drawActivity() { //TODO: xml
        FrameLayout frameLayout = findViewById(R.id.activity_main);

        jstickL = new JoystickView(this);
        jstickL.setLayoutParams(new FrameLayout.LayoutParams(500, 500, Gravity.LEFT | Gravity.BOTTOM));
        jstickL.setButtonColor(Color.argb(41, 0, 0, 0));
        jstickL.setButtonSizeRatio(0.35f);
        jstickL.setBackgroundSizeRatio(0.6f);
        //jstickL.setBorderColor(Color.rgb(50, 50, 50));
        jstickL.setBackgroundColor(Color.argb(25, 0, 0, 0));
        jstickL.setBorderWidth(5);
        jstickL.setFixedCenter(true);

        jstickR = new JoystickView(this);
        jstickR.setLayoutParams(new FrameLayout.LayoutParams(500, 500, Gravity.RIGHT | Gravity.BOTTOM));
        jstickR.setButtonColor(Color.argb(41, 0, 0, 0));
        jstickR.setButtonSizeRatio(0.35f);
        jstickR.setBackgroundSizeRatio(0.6f);
        //jstickR.setBorderColor(Color.rgb(50, 50, 50));
        jstickR.setBackgroundColor(Color.argb(25, 0, 0, 0));
        jstickR.setBorderWidth(5);
        jstickR.setFixedCenter(true);

        fps_tv = new TextView(this);
        fps_tv.setTextColor(Color.BLACK);
        fps_tv.setBackgroundColor(Color.WHITE);
        fps_tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.TOP));

        frameLayout.addView(new DrawView(this, game));
        frameLayout.addView(jstickL);
        frameLayout.addView(jstickR);
        frameLayout.addView(fps_tv);

        MySingletons.getMyResources().setGUIUListener(this);
    }

    @Override
    public void updateUI(int fps) {
        fps_tv.setText("FPS: " + fps);
    }
}