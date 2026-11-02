package com.example.catland.Sante;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catland.Adapters.ArticleAdapter;
import com.example.catland.Models.Article;
import com.example.catland.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class SanteMainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> articles;

    private MaterialToolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sante_main);

        recyclerView = findViewById(R.id.recyclerViewSante);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        toolBar = findViewById(R.id.topAppBar);
        toolBar.setTitle("Conseils et Santé");


        articles = new ArrayList<>();
        // Ajoutez vos articles ici en utilisant l'ID de ressource drawable
        articles.add(new Article(
                "Comment soigner un chat qui vomit : un guide complet",
                "Les vomissements chez le chat sont un problème fréquent qui peut inquiéter les propriétaires. Bien qu'ils soient parfois bénins, ils peuvent aussi être le signe d'un problème de santé nécessitant une prise en charge. En tant que vétérinaire, je vais...",
                "Les vomissements chez le chat peuvent être causés par de nombreux facteurs, allant d'une simple indigestion à des problèmes de santé plus graves. Il est crucial d'observer attentivement votre chat et de consulter un vétérinaire si les vomissements sont fréquents, persistants, ou accompagnés d'autres symptômes comme la léthargie, la perte d'appétit ou la diarrhée. En attendant, vous pouvez retirer la nourriture pendant 12 heures, offrir de petites quantités d'eau, puis réintroduire progressivement une alimentation légère. Ne donnez jamais de médicaments humains à votre chat sans l'avis d'un professionnel.",
                R.drawable.cat_vomit, // <-- Remplacez par le nom de votre fichier image dans drawable
                "SANTÉ"
        ));

        articles.add(new Article(
                "Les calculs urinaires chez le chat : causes, symptômes et traitements",
                "Les calculs urinaires, parfois appelés \"sable dans la vessie\" ou \"calculs rénaux\", peuvent provoquer des douleurs et des complications sérieuses chez les chats. Il est essentiel de savoir les reconnaître et de comprendre comment les prévenir. Qu'est-ce que les calculs...",
                "Les calculs urinaires sont des dépôts minéraux qui se forment dans les voies urinaires du chat. Ils peuvent causer des blocages douloureux et potentiellement mortels. Les symptômes incluent des mictions fréquentes, des efforts pour uriner, du sang dans l'urine, et des miaulements de douleur. Le traitement dépend du type et de la taille des calculs, et peut inclure un régime alimentaire spécial, des médicaments, ou une intervention chirurgicale. La prévention passe souvent par une alimentation adaptée et une hydratation suffisante.",
                R.drawable.cat_calculs, // <-- Remplacez par le nom de votre fichier image dans drawable
                "SANTÉ"
        ));

        articles.add(new Article(
                "Mon chat a des hallucinations : comprendre le syndrome d'hyperesthésie féline",
                "Certains propriétaires observent chez leur chat des comportements soudains et inexplicables, tels que des léchages frénétiques, des spasmes cutanés ou des courses effrénées. Ces manifestations peuvent être liées au syndrome d'hyperesthésie féline, également connu sous le nom de \"rolling skin\"...",
                "Le syndrome d'hyperesthésie féline est un trouble neurologique qui provoque des sensations anormales chez le chat. Les symptômes peuvent varier, mais incluent souvent des ondulations de la peau le long du dos, des léchages excessifs, des automutilations, des vocalisations, et des épisodes de course effrénée. Le diagnostic est basé sur l'exclusion d'autres causes médicales. Le traitement peut impliquer des médicaments pour contrôler les symptômes et des modifications de l'environnement pour réduire le stress.",
                R.drawable.cat_helli, // <-- Remplacez par le nom de votre fichier image dans drawable
                "COMPORTEMENT"
        ));

        articles.add(new Article(
                "Comment accueillir un chaton",
                "Conseils pour accueillir un chaton à la maison : conseils pour une intégration réussie Accueillir un chaton est une étape excitante et émouvante. Que ce soit votre premier animal ou que vous ayez déjà un compagnon félin, cette arrivée nécessite une...",
                "Accueillir un chaton demande de la préparation. Assurez-vous d'avoir tout le nécessaire : nourriture de qualité pour chatons, gamelles, litière, jouets, et un endroit sûr où il pourra se retirer. Préparez une pièce dédiée pour les premiers jours afin qu'il s'adapte progressivement à son nouvel environnement. Présentez-le aux autres animaux de la maison lentement et sous surveillance. Offrez-lui beaucoup d'attention, de socialisation et de jeux pour l'aider à devenir un chat adulte bien équilibré.",
                R.drawable.cat_chaton, // <-- Remplacez par le nom de votre fichier image dans drawable
                "CHATON"
        ));

        articles.add(new Article(
                "Comprendre et gérer le DIABETE chez le chat : un guide complet",
                "Le diabète chez le chat est une maladie de plus en plus fréquente, souvent sous-estimée par les propriétaires. Pourtant, détecté à temps et géré correctement, le diabète du chat ne doit pas être perçu comme une fatalité. Avec des soins...",
                "Le diabète sucré chez le chat est une maladie métabolique caractérisée par une glycémie élevée. Les symptômes incluent une soif excessive, une augmentation de la miction, une perte de poids malgré un appétit normal ou augmenté, et parfois une léthargie. Le diagnostic est confirmé par des tests sanguins et urinaires. Le traitement implique généralement une injection quotidienne d'insuline et un régime alimentaire spécifique. Une gestion attentive et un suivi vétérinaire régulier sont essentiels pour maintenir la qualité de vie du chat.",
                R.drawable.cat_diabete,
                "SANTÉ"
        ));

        articles.add(new Article(
                "Mon chat urine en dehors de la litière",
                "Trouver de l'urine de chat en dehors de la litière n'est jamais agréable, que ce soit pour le chat ou pour son humain. Avant de faire une crise de nerf et d'envisager de mettre votre chat en pension chez votre...",
                "L'élimination inappropriée chez le chat peut avoir de nombreuses causes, médicales ou comportementales. Il est crucial de consulter un vétérinaire en premier lieu pour exclure un problème de santé (infection urinaire, calculs, diabète, etc.). Si aucune cause médicale n'est trouvée, le problème est probablement comportemental. Les causes courantes incluent le stress, l'anxiété, des problèmes avec la litière elle-même (type de litière, propreté, emplacement), ou des conflits avec d'autres animaux. Des modifications de l'environnement et des techniques de gestion du stress peuvent aider.",
                R.drawable.cat_litiere, // <-- Remplacez par le nom de votre fichier image dans drawable
                "COMPORTEMENT"
        ));

        // Add more articles as needed

        adapter = new ArticleAdapter(this, articles);
        recyclerView.setAdapter(adapter);
    }
}
