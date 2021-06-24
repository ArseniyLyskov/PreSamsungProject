package com.example.presamsungproject.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.*;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.ConnectionObjects.Server;
import com.example.presamsungproject.Map;
import com.example.presamsungproject.MyInterfaces.ClientUpdatableUI;
import com.example.presamsungproject.MySingletons;
import com.example.presamsungproject.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class LobbyClientActivity extends AppCompatActivity implements ClientUpdatableUI {
    private String name;
    private int team;
    private TextView tv_upper_text, tv_number, tv_nicks;
    private EditText editText;
    private TryToConnectToServer tryToConnectToServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_lobby);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (savedInstanceState == null)
            return;

        MySingletons.setLobby(false);

        ImageView menuImage = findViewById(R.id.acl_menu_image);
        ImageView backgroundImage = findViewById(R.id.acl_background_image);
        tv_number = findViewById(R.id.acl_textview_number);
        tv_nicks = findViewById(R.id.acl_textview_nicks);
        tv_upper_text = findViewById(R.id.acl_upper_text);
        editText = findViewById(R.id.acl_edittext);

        backgroundImage.setImageBitmap(MySingletons.getMyResources().getPaintedWallPaper());
        backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
        menuImage.setImageResource(R.drawable.white350_300);

        name = getIntent().getStringExtra("name");
        updateUI("", 0);
        MySingletons.getMyResources().setClientUpdatableUI(this);
    }

    @Override
    protected void onDestroy() {
        if (MySingletons.getClient() != null) {
            if (tryToConnectToServer.isAlive())
                tryToConnectToServer.interrupt();
            updateUI("", 0);
        }
        super.onDestroy();
    }

    public void connectToLobbyClick(View view) { //TODO: changing to "disconnect from lobby" and process that
        String serverIP = editText.getText().toString();
        if (serverIP.equals("")) {
            Toast.makeText(getApplicationContext(), "You didn't enter server IP", Toast.LENGTH_SHORT).show();
            return;
        }
        tryToConnectToServer = new TryToConnectToServer(serverIP, true);
        tryToConnectToServer.start();
    }

    @Override
    public void updateUI(String nicks, int players_quantity) {
        if (nicks.equals("") || players_quantity == 0) {
            tv_upper_text.setText("Enter lobby creator IP");
            tv_number.setText("Number of people waiting: ");
            tv_number.setVisibility(View.INVISIBLE);
            tv_nicks.setText("");
        } else {
            tv_upper_text.setText("Waiting for the game to start...");
            tv_number.setText("Number of people waiting: " + players_quantity);
            tv_number.setVisibility(View.VISIBLE);
            tv_nicks.setText(nicks);
        }
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
    public void startGame(Map map) {
        MySingletons.startGame(map, name, team);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void setTeam(int team) {
        this.team = team;
    }

    class TryToConnectToServer extends Thread {
        private final String serverIP;
        private final boolean startClientIfSuccess;

        public TryToConnectToServer(String serverIP, boolean startClientIfSuccess) {
            this.serverIP = serverIP;
            this.startClientIfSuccess = startClientIfSuccess;
        }

        @Override
        public void run() {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(serverIP, Server.serverPort), 1000);
                socket.close();
                if (startClientIfSuccess) {
                    MySingletons.createClient(serverIP);
                    MySingletons.getClient().sendMessage(MessageManager.connectMessage(name));
                }
            } catch (IOException e) {
                showToast("Error during connection. Possibly a typo in the IP " +
                        "or there is no lobby in the local network.", Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        }
    }
}