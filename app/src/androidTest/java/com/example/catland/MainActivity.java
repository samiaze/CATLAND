package com.example.catland;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    LinearLayout btnScanner, btnAuthentifier, btnConseilSante, btnCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScanner = findViewById(R.id.btnScanner);
        btnAuthentifier = findViewById(R.id.btnAuthentifier);
        btnConseilSante = findViewById(R.id.btnConseilSante);
        btnCategories = findViewById(R.id.btnCategories);

        btnScanner.setOnClickListener(view -> startActivity(new Intent(this, ReadNFCActivity.class)));
        btnAuthentifier.setOnClickListener(view -> startActivity(new Intent(this, OCRAuthChoiceActivity.class)));
//        btnConseilSante.setOnClickListener(view -> startActivity(new Intent(this, SanteMainActivity.class)));
//        btnCategories.setOnClickListener(view -> startActivity(new Intent(this, CategoryListActivity.class)));
    }
}
