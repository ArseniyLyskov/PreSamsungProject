package com.example.presamsungproject;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {
    private Game game = new Game();
    private FrameLayout frameLayout;
    private JoystickView jstickL, jstickR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(new DrawView(this));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        game.start(getApplicationContext());

        drawActivity();

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


    private void drawActivity() {
        frameLayout = findViewById(R.id.activity_main);

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

        TextView fps_tv = new TextView(this);
        fps_tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.TOP));
        game.setFps_tv(fps_tv);

        frameLayout.addView(new DrawView(this, game));
        frameLayout.addView(jstickL);
        frameLayout.addView(jstickR);
        frameLayout.addView(fps_tv);
    }
}