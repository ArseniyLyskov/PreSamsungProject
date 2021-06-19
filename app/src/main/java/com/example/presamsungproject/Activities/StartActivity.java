package com.example.presamsungproject.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.Map;
import com.example.presamsungproject.MyPaints;
import com.example.presamsungproject.R;

public class StartActivity extends AppCompatActivity {
    private EditText editText;
    private ImageView menuImage, backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if(savedInstanceState == null)
            return;

        MyPaints.paintsInit();

        editText = findViewById(R.id.as_edittext);
        menuImage = findViewById(R.id.as_menu_image);
        backgroundImage = findViewById(R.id.as_background_image);

        Bitmap background = new Map().getDrawnMap(getApplicationContext());
        backgroundImage.setImageBitmap(background);
        menuImage.setImageResource(R.drawable.white350_200);

        MessageManager.findExternalAddress();
    }

    public void joinLobbyClick(View view) {
        if(!isConnectedToWiFI())
            return;
        if(isNickNameEntered()) {
            String name = editText.getText().toString();

            Intent intent = new Intent(this, LobbyClientActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
        }
    }

    public void createLobbyClick(View view) {
        if(!isConnectedToWiFI())
            return;
        if(isNickNameEntered()) {
            String name = editText.getText().toString();

            Intent intent = new Intent(this, LobbyServerActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
        }
    }

    private boolean isNickNameEntered() {
        if (editText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "You didn't enter your nickname", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }

    private boolean isConnectedToWiFI() {
        boolean connected = false;
        if(MessageManager.EXTERNAL_ADDRESS != null)
            connected = MessageManager.EXTERNAL_ADDRESS.length() > 4
                    && MessageManager.EXTERNAL_ADDRESS.length() < 16;
        else
            MessageManager.findExternalAddress();
        if(!connected)
            Toast.makeText(getApplicationContext(), "Error. Check your connection to Wi-Fi.", Toast.LENGTH_SHORT).show();
        return connected;
    }

}