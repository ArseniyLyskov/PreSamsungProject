package com.example.presamsungproject.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.presamsungproject.ConnectionObjects.Client;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.Map;
import com.example.presamsungproject.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class LobbyClientActivity extends AppCompatActivity {
    private static String name;
    private ImageView menuImage, backgroundImage;
    private TextView tv_upper_text;
    private ScrollView scrollView;
    private EditText editText;
    private static final String txtWait = "Number of people waiting: ";
    private static Handler handler;
    private static String nicks;
    private static int players_quantity;
    private static Intent intent = null;

    public static int team;
    public static Map map;
    public static TextView tv_number, tv_nicks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_lobby);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (savedInstanceState == null)
            return;

        menuImage = findViewById(R.id.acl_menu_image);
        backgroundImage = findViewById(R.id.acl_background_image);
        tv_number = findViewById(R.id.acl_textview_number);
        tv_nicks = findViewById(R.id.acl_textview_nicks);
        tv_upper_text = findViewById(R.id.acl_upper_text);
        editText = findViewById(R.id.acl_edittext);
        scrollView = findViewById(R.id.acl_scrollview);
        handlerInit();

        Bitmap background = new Map().getDrawnMap(getApplicationContext());
        backgroundImage.setImageBitmap(background);
        menuImage.setImageResource(R.drawable.white350_300);

        name = getIntent().getStringExtra("name");

        intent = new Intent(this, MainActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            Log.d("MyTag", "Client destroying");
            Client.stopClient();
            tv_nicks.setText("");
            tv_number.setText(txtWait);
            tv_upper_text.setText("Enter lobby creator IP");
        }
    }

    public static void gotoMainActivity() {
        if (intent != null) {
            intent.putExtra("isLobby", false);
            intent.putExtra("name", name);
            intent.putExtra("team", team);
            handler.sendEmptyMessage(2);
        }
    }

    public void connectToLobbyClick(View view) { //TODO: changing to "disconnect from lobby" and process that
        String serverIP = editText.getText().toString();
        if (serverIP.equals("")) {
            Toast.makeText(getApplicationContext(), "You didn't enter server IP", Toast.LENGTH_SHORT).show();
            return;
        }
        connectToSocketWithCheck(serverIP);
    }

    public static void updateUI(String nicks, int players_quantity) {
        LobbyClientActivity.nicks = nicks;
        LobbyClientActivity.players_quantity = players_quantity;
        handler.sendEmptyMessage(1);
    }

    private void handlerInit() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: {
                        tv_number.setText(txtWait + players_quantity);
                        tv_nicks.setText(nicks);
                        break;
                    }
                    case 2: {
                        startActivity(intent);
                        break;
                    }
                }
                return false;
            }
        });
    }

    private void connectToSocketWithCheck(String serverIP) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(serverIP, Server.serverPort), 1000);
                    success = true;
                    Client.startClient(serverIP);
                    Client.sendMessage(MessageManager.connectMessage(name));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean finalSuccess = success;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!finalSuccess)
                            Toast.makeText(getApplicationContext(), "Error during connection. " +
                                    "Possibly a typo in the IP or there is no lobby in the local network.", Toast.LENGTH_LONG).show();
                        else {
                            tv_number.setVisibility(View.VISIBLE);
                            tv_upper_text.setText("Waiting for the game to start...");
                            scrollView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

}