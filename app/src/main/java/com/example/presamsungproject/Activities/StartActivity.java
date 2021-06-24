package com.example.presamsungproject.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.presamsungproject.ConnectionObjects.MessageManager;
import com.example.presamsungproject.MySingletons;
import com.example.presamsungproject.R;

public class StartActivity extends AppCompatActivity {
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (savedInstanceState == null)
            return;

        MySingletons.createMyResources(getApplicationContext());

        editText = findViewById(R.id.as_edittext);
        ImageView menuImage = findViewById(R.id.as_menu_image);
        ImageView backgroundImage = findViewById(R.id.as_background_image);

        backgroundImage.setImageBitmap(MySingletons.getMyResources().getPaintedWallPaper());
        backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
        menuImage.setImageResource(R.drawable.white350_200);

        MessageManager.tryToFindExternalAddress();
    }

    public void joinLobbyClick(View view) {
        if (!isConnectedToLocalNetwork())
            return;
        if (isNickNameEntered()) {
            String name = editText.getText().toString();

            Intent intent = new Intent(this, LobbyClientActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
        }
    }

    public void createLobbyClick(View view) {
        if (!isConnectedToLocalNetwork())
            return;
        if (isNickNameEntered()) {
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
        } else if (editText.getText().toString().contains(" ")) {
            Toast.makeText(getApplicationContext(), "Your nickname mustn't contain a spaces", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    private boolean isConnectedToLocalNetwork() {
        boolean connected = false;
        if (MessageManager.EXTERNAL_ADDRESS != null)
            connected = MessageManager.EXTERNAL_ADDRESS.length() > 4
                    && MessageManager.EXTERNAL_ADDRESS.length() < 16;
        if (!connected) {
            Toast.makeText(getApplicationContext(), "Error. Check your connection to Wi-Fi.", Toast.LENGTH_SHORT).show();
            MessageManager.tryToFindExternalAddress();
        }
        return connected;
    }
}