package com.example.catland.Categories;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.catland.R;
import com.google.android.material.appbar.MaterialToolbar;

public class CatDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_detail);

        MaterialToolbar toolbar = findViewById(R.id.breedDetailToolbar);
        ImageView imageView = findViewById(R.id.breedDetailImage);
        TextView nameView = findViewById(R.id.breedDetailName);
        TextView originView = findViewById(R.id.breedDetailOrigin);
        TextView descriptionView = findViewById(R.id.breedDetailDescription);

        String name = getIntent().getStringExtra("name");
        String origin = getIntent().getStringExtra("origin");
        String description = getIntent().getStringExtra("description");
        int imageResId = getIntent().getIntExtra("image", 0);

        toolbar.setTitle(name);
        toolbar.setNavigationOnClickListener(v -> finish());

        nameView.setText(name);
        originView.setText(String.format("Origine : %s", origin));
        descriptionView.setText(description);
        imageView.setImageResource(imageResId);
    }
}
