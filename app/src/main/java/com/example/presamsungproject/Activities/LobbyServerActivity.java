package com.example.presamsungproject.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.Map;
import com.example.presamsungproject.R;

import java.util.HashMap;

public class LobbyServerActivity extends AppCompatActivity {
    private String name;
    private ImageView menuImage, backgroundImage;
    private TextView tv_myIP;
    private static final String txtWait = "Number of people waiting: ";
    private static final String txtMyIP = "My IP: ";
    private static Handler handler;
    private static TextView tv_number, tv_nicks;

    public static Map map;
    public static HashMap<String, String> players = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_lobby);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (savedInstanceState == null)
            return;

        menuImage = findViewById(R.id.asl_menu_image);
        backgroundImage = findViewById(R.id.asl_background_image);
        tv_number = findViewById(R.id.asl_textview_number);
        tv_nicks = findViewById(R.id.asl_textview_nicks);
        tv_myIP = findViewById(R.id.asl_my_ip);
        handlerInit();

        Bitmap background = new Map().getDrawnMap(getApplicationContext());
        backgroundImage.setImageBitmap(background);
        menuImage.setImageResource(R.drawable.white350_300);
        tv_myIP.setText(txtMyIP + MessageManager.EXTERNAL_ADDRESS);

        name = getIntent().getStringExtra("name");

        players.put(MessageManager.EXTERNAL_ADDRESS, name);
        updateUI();

        Server.startServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            Log.d("MyTag", "Server destroying");
            Server.stopServer();
            players.clear();
        }
    }

    public static void updateUI() {
        handler.sendEmptyMessage(1);
    }

    private void handlerInit() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                tv_number.setText(txtWait + players.size());
                String namesText = "";
                for (String s : players.values()) {
                    namesText += s + "\n";
                }
                tv_nicks.setText(namesText);
                return false;
            }
        });
    }

    public void startGameClick(View view) {
        map = new Map();
        String messageToAll = null;
        try {
            messageToAll = MessageManager.sendMapMessage(map);
            Server.broadcastMessage(messageToAll);
        } catch (Exception e) {
            Log.d("MyTag", "Error during serializing map");
            e.printStackTrace();
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isLobby", true);
        intent.putExtra("name", name);
        intent.putExtra("team", 1);
        startActivity(intent);
    }

    /*private static boolean isPortAvailable(int port) {
        try (ServerSocket ss = new ServerSocket(port); DatagramSocket ds = new DatagramSocket(port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int findUnusedPort() {
        int result;
        while (true) {
            result = 4440 + (int) (Math.random() * 10);
            if (isPortAvailable(result))
                return result;
        }
    }*/
}