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
import com.example.presamsungproject.Map;
import com.example.presamsungproject.R;

public class StartActivity extends AppCompatActivity {
    private EditText editText;
    private ImageView imageView, imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if(savedInstanceState == null)
            return;

        editText = findViewById(R.id.as_edittext);
        imageView = findViewById(R.id.as_imageview);
        imageView2 = findViewById(R.id.as_imageview2);

        Bitmap background = new Map(getApplicationContext()).getBitmap();
        imageView.setImageBitmap(background);
        imageView2.setImageResource(R.drawable.white350_200);
    }

    public void joinLobbyClick(View view) {
        changeActivity(false);
    }

    public void crateLobbyClick(View view) {
        changeActivity(true);
    }

    private void changeActivity(boolean isLobby) {
        if (editText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "You didn't enter your nickname", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("isLobby", isLobby);
        intent.putExtra("name", editText.getText().toString());
        startActivity(intent);
    }

}
