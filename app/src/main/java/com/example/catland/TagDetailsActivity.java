package com.example.catland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TagDetailsActivity extends AppCompatActivity {

    private TextView tagInfoText;
    private Button btnWrite, btnUpdate, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_details);

        tagInfoText = findViewById(R.id.tagInfoText);
        btnWrite = findViewById(R.id.btnWrite);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        // Get data from intent
        Intent intent = getIntent();
        String rawData = intent.getStringExtra("nfc_data");
        String ownerCin = intent.getStringExtra("owner_id");

        // Clean and display readable data
        if (rawData != null && rawData.contains("[owner_id=")) {
            rawData = rawData.substring(0, rawData.indexOf("[owner_id=")).trim();
        }
        tagInfoText.setText(rawData != null ? rawData : "Aucune donnée lisible.");

        // Get current user CIN
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String userCin = prefs.getString("cin", null);

        // Determine access
        boolean userIsOwner = userCin != null && userCin.equals(ownerCin);
        boolean tagIsEmpty = (ownerCin == null || ownerCin.isEmpty());

        btnWrite.setEnabled(userIsOwner || tagIsEmpty);
        btnUpdate.setEnabled(userIsOwner);
        btnDelete.setEnabled(userIsOwner);

        btnWrite.setOnClickListener(v -> {
            Intent i = new Intent(this, WriteNFCActivity.class);
            startActivity(i);
        });

        btnUpdate.setOnClickListener(v -> {
            Intent i = new Intent(this, WriteNFCActivity.class);
            i.putExtra("mode", "update");
            startActivity(i);
        });

        btnDelete.setOnClickListener(v -> {
            Intent i = new Intent(this, WriteNFCActivity.class);
            i.putExtra("mode", "delete");
            startActivity(i);
        });
    }
}