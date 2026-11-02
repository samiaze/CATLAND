package com.example.catland;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class WriteNFCActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private Tag currentTag;

    private EditText editCatName, editCategory, editColor, editSex, editAge, editPhone, editEmail;
    private CheckBox chipCheckbox;
    private Button actionButton;

    private String mode;
    private String userCin, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_nfc);

        editCatName = findViewById(R.id.editCatName);
        editCategory = findViewById(R.id.editCategory);
        editColor = findViewById(R.id.editColor);
        editSex = findViewById(R.id.editSex);
        editAge = findViewById(R.id.editAge);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);
        chipCheckbox = findViewById(R.id.chipCheckbox);
        actionButton = findViewById(R.id.btnAction);

        mode = getIntent().getStringExtra("mode");

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        userCin = prefs.getString("cin", "UNKNOWN");
        userName = prefs.getString("name", "UNKNOWN");

        if (Objects.equals(userCin, "UNKNOWN") || userName.equals("UNKNOWN")) {
            Toast.makeText(this, "Session expirée. Veuillez vous reconnecter.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class); // Replace with your actual login activity class
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        if ("delete".equals(mode)) {
            editCatName.setVisibility(View.GONE);
            editCategory.setVisibility(View.GONE);
            editColor.setVisibility(View.GONE);
            editSex.setVisibility(View.GONE);
            editAge.setVisibility(View.GONE);
            editPhone.setVisibility(View.GONE);
            editEmail.setVisibility(View.GONE);
            chipCheckbox.setVisibility(View.GONE);
            actionButton.setText("Supprimer les données");
        } else {
            actionButton.setText("Écrire sur la puce");
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            new AlertDialog.Builder(this).setTitle("NFC non disponible").setMessage("Votre appareil ne prend pas en charge la technologie NFC.").setPositiveButton("Retour", (dialog, which) -> finish()).setCancelable(false).show();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            new AlertDialog.Builder(this).setTitle("NFC désactivé").setMessage("Veuillez activer la fonction NFC dans les paramètres de votre appareil.").setPositiveButton("Ouvrir les paramètres", (dialog, which) -> {
                Intent intent = new Intent(android.provider.Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            }).setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss()).show();
        }

        actionButton.setOnClickListener(v -> {
            if (currentTag == null) {
                Toast.makeText(this, "Aucune puce détectée", Toast.LENGTH_SHORT).show();
                return;
            }

            String textToWrite;
            if ("delete".equals(mode)) {
                textToWrite = "";
            } else {
                String cat = editCatName.getText().toString().trim();
                String breed = editCategory.getText().toString().trim();
                String color = editColor.getText().toString().trim();
                String sex = editSex.getText().toString().trim();
                String age = editAge.getText().toString().trim();
                String chip = chipCheckbox.isChecked() ? "Oui" : "Non";
                String phone = editPhone.getText().toString().trim();
                String email = editEmail.getText().toString().trim();

                String template = "Nom du chat : %s\n" + "Race : %s\n" + "Couleur : %s\n" + "Sexe : %s\n" + "Âge : %s\n" + "Puce électronique : %s\n\n" + "Propriétaire : %s\n" + "Téléphone : %s\n" + "Adresse e-mail : %s\n\n" + "Si vous trouvez ce chat, merci de contacter immédiatement sa propriétaire.\n" + "[owner_id=%s]";

                textToWrite = String.format(template, cat.isEmpty() ? "-" : cat, breed.isEmpty() ? "-" : breed, color.isEmpty() ? "-" : color, sex.isEmpty() ? "-" : sex, age.isEmpty() ? "-" : age, chip, userName, phone.isEmpty() ? "-" : phone, email.isEmpty() ? "-" : email, userCin);
            }


            writeToTag(currentTag, textToWrite);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Toast.makeText(this, "Puce détectée", Toast.LENGTH_SHORT).show();
    }

    private void enableForegroundDispatch() {
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        IntentFilter[] filters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    private void disableForegroundDispatch() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void writeToTag(Tag tag, String text) {
        try {
            NdefRecord record = NdefRecord.createTextRecord("fr", text);
            NdefMessage message = new NdefMessage(new NdefRecord[]{record});
            Ndef ndef = Ndef.get(tag);

            ndef.connect();
            ndef.writeNdefMessage(message);
            ndef.close();

            Toast.makeText(this, "Opération réussie", Toast.LENGTH_LONG).show();
            Intent back = new Intent(this, UserHomeActivity.class);
            back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(back);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Échec de l'écriture", Toast.LENGTH_LONG).show();
            Log.e("WriteNFC", "Erreur : ", e);
        }
    }
}