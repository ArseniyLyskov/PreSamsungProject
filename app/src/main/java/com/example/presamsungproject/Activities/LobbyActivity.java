package com.example.presamsungproject.Activities;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.TreeSet;

public class LobbyActivity extends AppCompatActivity {
    private Button button;
    private String name;
    private ImageView imageView, imageView2;
    private long id;

    public boolean isLobby;
    public TreeSet<String> names;
    public TextView tv_number, tv_nicks;
    public Handler handler;
    public static final String txtWait = "Number of people waiting: ";

    {
        names = new TreeSet<>();
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if(savedInstanceState == null)
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

        if(!isLobby)
            button.setVisibility(View.INVISIBLE);

        if(isLobby) {
            id = 0;
            names.add(name);
            tv_number.setText(txtWait + names.size());
            tv_nicks.setText(name + "\n");
            MessagesReceiver messagesReceiver = new MessagesReceiver("230.0.0.0", 4446, this);
            messagesReceiver.start();

        } else {
            MessagesReceiver messagesReceiver = new MessagesReceiver("230.0.0.0", 4446, this);
            messagesReceiver.start();
            String string = StringConverter.sendReadyToBattle(name);
            BroadcastingMessage message = new BroadcastingMessage(string, "255.255.255.255", 4446);
            message.start();
        }
    }
}
