package com.example.catland.Sante;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import com.example.catland.R;

public class ArticleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        MaterialToolbar toolbar = findViewById(R.id.detailTopAppBar);
        TextView titleView = findViewById(R.id.detailTitle);
        TextView contentView = findViewById(R.id.detailContent);
        ImageView imageView = findViewById(R.id.detailImage);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        int imageResId = getIntent().getIntExtra("image", 0);

        titleView.setText(title);
        contentView.setText(content);
        imageView.setImageResource(imageResId);

        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}
