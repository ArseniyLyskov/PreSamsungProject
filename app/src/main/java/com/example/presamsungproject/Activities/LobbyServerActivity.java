package com.example.presamsungproject.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.Map;
import com.example.presamsungproject.MyInterfaces.ServerUpdatableUI;
import com.example.presamsungproject.MySingletons;
import com.example.presamsungproject.R;

import java.util.HashMap;

public class LobbyServerActivity extends AppCompatActivity implements ServerUpdatableUI {
    private String name;
    private TextView tv_number, tv_nicks;
    private final HashMap<String, String> players = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_lobby);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (savedInstanceState == null)
            return;

        MySingletons.setLobby(true);

        ImageView menuImage = findViewById(R.id.asl_menu_image);
        ImageView backgroundImage = findViewById(R.id.asl_background_image);
        tv_number = findViewById(R.id.asl_textview_number);
        tv_nicks = findViewById(R.id.asl_textview_nicks);
        TextView tv_myIP = findViewById(R.id.asl_my_ip);

        backgroundImage.setImageBitmap(MySingletons.getMyResources().getPaintedWallPaper());
        backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
        menuImage.setImageResource(R.drawable.white350_300);
        tv_myIP.setText("My IP: " + MessageManager.EXTERNAL_ADDRESS);

        name = getIntent().getStringExtra("name");

        players.put(MessageManager.EXTERNAL_ADDRESS, name);
        updateUI();
        MySingletons.getMyResources().setServerUpdatableUI(this);

        MySingletons.createServer();
    }

    @Override
    protected void onDestroy() {
        if(MySingletons.getServer() != null && MySingletons.getServer().isRunning()) {
            players.clear();
            updateUI();
            MySingletons.getServer().stop();
        }
        super.onDestroy();
    }

    public void startGameClick(View view) {
        Map map = new Map(10, 10, 25, 25);
        String messageToAll = null;
        try {
            messageToAll = MessageManager.sendMapMessage(map);
            MySingletons.getServer().broadcastMessage(messageToAll);
        } catch (Exception e) {
            Log.d("MyTag", "Error during serializing map");
            e.printStackTrace();
            return;
        }
        startGame(map);
    }

    @Override
    public void updateUI() {
        tv_number.setText("Number of people waiting: " + players.size());
        String namesText = "";
        for (String s : players.values()) {
            namesText += s + "\n";
        }
        tv_nicks.setText(namesText);
    }

    @Override
    public void showToast(String message, int length) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, length).show();
            }
        });
    }

    @Override
    public HashMap<String, String> getPlayers() {
        return players;
    }

    @Override
    public void startGame(Map map) {
        MySingletons.startGame(map, name, 1);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
