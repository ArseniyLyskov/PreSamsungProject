package com.example.presamsungproject.Activities;

import android.content.pm.ActivityInfo;
import android.graphics.*;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.presamsungproject.ConnectionObjects.StringConverter;
import com.example.presamsungproject.DrawView;
import com.example.presamsungproject.Game;
import com.example.presamsungproject.Map;
import com.example.presamsungproject.R;
import io.github.controlwear.virtual.joystick.android.JoystickView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Game game;
    private FrameLayout frameLayout;
    private JoystickView jstickL, jstickR;
    private int port;
    private boolean isLobby;
    private boolean ready;

    {
        ready = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        isLobby = getIntent().getBooleanExtra("isLobby", false);
        //map_bitmap;

        if(savedInstanceState == null)
            return;

        port = getIntent().getIntExtra("port", -1);
        if(port == -1) {
            Toast.makeText(getApplicationContext(), "Didn't get port", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = getIntent().getStringExtra("name");

        ArrayList<Integer> ports = getIntent().getIntegerArrayListExtra("ports");

        game = new Game(new Map(this, getIntent().getStringExtra("map")), port, name, isLobby, ports);

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