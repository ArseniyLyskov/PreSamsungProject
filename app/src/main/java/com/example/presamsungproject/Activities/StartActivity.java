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
import com.example.presamsungproject.MyPaints;
import com.example.presamsungproject.R;

public class StartActivity extends AppCompatActivity {
    private EditText editText;
    //TODO: может стоит конвертировать в локальные переменные?
    private ImageView menuImage, backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //TODO: может это через Manifest прописать? Это не сработает везде.
        // Также не стоит забывать что это надо делать тогда для всех активностей
        // Может задуматься об активации immersiveMode и запрета на выключение экрана?
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(savedInstanceState == null)
            return;

        MyPaints.paintsInit(getApplicationContext());

        editText = findViewById(R.id.as_edittext);
        menuImage = findViewById(R.id.as_menu_image);
        backgroundImage = findViewById(R.id.as_background_image);

        backgroundImage.setImageBitmap(MyPaints.getPaintedWallPaper());
        backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
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
        } else if(editText.getText().toString().contains(" ")) {
            Toast.makeText(getApplicationContext(), "Your nickname mustn't contain a spaces", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }

    //TODO: если всегда идёт проеверка на отсутствие соединения
    // может стоит тогда написать метод который проверяет отсутствие подключения?
    private boolean isConnectedToWiFI() {
        boolean connected = false;
        if(MessageManager.EXTERNAL_ADDRESS != null)
            connected = MessageManager.EXTERNAL_ADDRESS.length() > 4
                    && MessageManager.EXTERNAL_ADDRESS.length() < 16;
        if(!connected) {
            Toast.makeText(getApplicationContext(), "Error. Check your connection to Wi-Fi.", Toast.LENGTH_SHORT).show();
            MessageManager.findExternalAddress();
        }
        return connected;
    }

}