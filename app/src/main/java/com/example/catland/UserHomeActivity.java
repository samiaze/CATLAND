package com.example.catland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserHomeActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        welcomeText = findViewById(R.id.welcomeText);
        scanButton = findViewById(R.id.btnScanTag);

        // Get user info from shared preferences
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String name = prefs.getString("name", "Utilisateur");
        String cin = prefs.getString("cin", "N/A");

        welcomeText.setText(String.format("Bonjour, %s (%s)", name, cin));

        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserHomeActivity.this, ReadTagActivity.class);
            startActivity(intent);
        });
    }
}