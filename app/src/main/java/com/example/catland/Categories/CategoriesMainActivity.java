package com.example.catland.Categories;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catland.Adapters.CatAdapter;
import com.example.catland.Models.Cat;
import com.example.catland.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class CategoriesMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CatAdapter adapter;
    private List<Cat> breedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_main);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        breedList = new ArrayList<>();
        breedList = new ArrayList<>();

        breedList.add(new Cat("Abyssin", "Éthiopie", "Race élégante, intelligente et joueuse.", R.drawable.abyssin));
        breedList.add(new Cat("American Bobtail", "États-Unis", "Curieux, affectueux, joueur.", R.drawable.american_bobtail));
        breedList.add(new Cat("American Curl", "États-Unis", "Affectueux, intelligent, sociable.", R.drawable.american_curl));
        breedList.add(new Cat("American Shorthair", "États-Unis", "Gentil, doux, adaptable.", R.drawable.american_shorthair));
        breedList.add(new Cat("American Wirehair", "États-Unis", "Curieux, doux, joueur.", R.drawable.american_wirehair));
        breedList.add(new Cat("Angora Turc", "Turquie", "Actif, intelligent, affectueux.", R.drawable.angora_turc));
        breedList.add(new Cat("Bengal", "États-Unis", "Énergique, curieux, sociable.", R.drawable.bengal));
        breedList.add(new Cat("British Shorthair", "Royaume-Uni", "Calme, affectueux, loyal.", R.drawable.british_shorthair));
        breedList.add(new Cat("Maine Coon", "États-Unis", "Amical, intelligent, joueur.", R.drawable.maine_coon));
        breedList.add(new Cat("Persan", "Iran", "Paisible, doux, affectueux.", R.drawable.persian));
        breedList.add(new Cat("Ragdoll", "États-Unis", "Docile, calme, affectueux.", R.drawable.ragdoll));
        breedList.add(new Cat("Siamois", "Thaïlande", "Chaud, vocal, loyal.", R.drawable.siamese));
        breedList.add(new Cat("Scottish Fold", "Écosse", "Calme, amical, doux.", R.drawable.scottish_fold));
        breedList.add(new Cat("Sphynx", "Canada", "Énergique, affectueux, sociable.", R.drawable.sphynx));
        breedList.add(new Cat("Norwegian Forest", "Norvège", "Indépendant, robuste, amical.", R.drawable.norwegian_forest));
        breedList.add(new Cat("Russian Blue", "Russie", "Calme, loyal, réservé.", R.drawable.russian_blue));
        breedList.add(new Cat("Exotic Shorthair", "États-Unis", "Paisible, doux, câlin.", R.drawable.exotic_shorthair));
        breedList.add(new Cat("Devon Rex", "Royaume-Uni", "Joueur, curieux, espiègle.", R.drawable.devon_rex));
        breedList.add(new Cat("Cornish Rex", "Royaume-Uni", "Énergique, joueur, sociable.", R.drawable.cornish_rex));
        breedList.add(new Cat("Himalayan", "États-Unis", "Doux, calme, affectueux.", R.drawable.himalayan));
        breedList.add(new Cat("Savannah", "États-Unis", "Exotique, actif, intelligent.", R.drawable.savannah));
        breedList.add(new Cat("Tonkinese", "Canada", "Sociable, intelligent, joueur.", R.drawable.tonkinese));

        adapter = new CatAdapter(this, breedList);
        recyclerView.setAdapter(adapter);
    }
}
