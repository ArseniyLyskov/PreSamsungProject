package com.example.presamsungproject.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.presamsungproject.ConnectionObjects.BroadcastingMessage;
import com.example.presamsungproject.ConnectionObjects.MessagesReceiver;
import com.example.presamsungproject.ConnectionObjects.StringConverter;
import com.example.presamsungproject.Map;
import com.example.presamsungproject.R;
import com.example.presamsungproject.ReceiverStorage;

import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class LobbyActivity extends AppCompatActivity {
    private Button button;
    private String name;
    private ImageView imageView, imageView2;
    private int port;
    private long id;

    public boolean isLobby;
    public TreeSet<String> names;
    public TextView tv_number, tv_nicks;
    public Handler handler;
    public ArrayList<Integer> ports;
    public static final String txtWait = "Number of people waiting: ";

    {
        names = new TreeSet<>();
        handler = new Handler();
        ports = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (savedInstanceState == null)
            return;

        button = findViewById(R.id.al_button);
        imageView = findViewById(R.id.al_imageview);
        imageView2 = findViewById(R.id.al_imageview2);
        tv_number = findViewById(R.id.al_textview_number);
        tv_nicks = findViewById(R.id.al_textview_nicks);

        Bitmap background = new Map(getApplicationContext()).getBitmap();
        imageView.setImageBitmap(background);
        imageView2.setImageResource(R.drawable.white350_300);

        isLobby = getIntent().getBooleanExtra("isLobby", false);
        name = getIntent().getStringExtra("name");

        if (!isLobby)
            button.setVisibility(View.INVISIBLE);

        if (isLobby) {
            port = 4444;
            ports.add(port);
            id = 0;
            names.add(name);
            tv_number.setText(txtWait + names.size());
            tv_nicks.setText(name + "\n");
            MessagesReceiver messagesReceiver = new MessagesReceiver("230.0.0.0", port, this);
            messagesReceiver.start();
            ReceiverStorage.setMessagesReceiver(messagesReceiver);

        } else {
            port = findUnusedPort();
            MessagesReceiver messagesReceiver = new MessagesReceiver("230.0.0.0", port, this);
            messagesReceiver.start();
            ReceiverStorage.setMessagesReceiver(messagesReceiver);
            String string = StringConverter.sendReadyToBattle(name, port);
            BroadcastingMessage message = new BroadcastingMessage(string, "255.255.255.255", 4444);
            message.start();
        }
    }

    public void startGameClick(View view) {
        Map map = new Map(getApplicationContext());
        String string_map = map.mapToString();
        goToMainActivity(string_map);
    }

    public void goToMainActivity (String string_map) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isLobby", isLobby);
        intent.putExtra("port", port);
        intent.putIntegerArrayListExtra("ports", ports);
        intent.putExtra("name", name);

        intent.putExtra("map", string_map);

        if (isLobby) {
            for (Integer port : ports) {
            BroadcastingMessage message = new BroadcastingMessage(string_map, "255.255.255.255", port);
            message.start();
            }
        }

        /*BroadcastingMessage message = new BroadcastingMessage(string_map, "255.255.255.255", 4444);
        message.start();*/

        startActivity(intent);
    }

    private static boolean isPortAvailable(int port) {
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
    }
}
