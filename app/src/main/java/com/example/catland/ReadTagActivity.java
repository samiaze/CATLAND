package com.example.catland;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;

public class ReadTagActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private static final String TAG = "ReadTagActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_tag);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("NFC non disponible")
                    .setMessage("Votre appareil ne prend pas en charge la technologie NFC.")
                    .setPositiveButton("Retour", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            new AlertDialog.Builder(this)
                    .setTitle("NFC désactivé")
                    .setMessage("Veuillez activer la fonction NFC dans les paramètres de votre appareil pour scanner.")
                    .setPositiveButton("Ouvrir les paramètres", (dialog, which) -> {
                        Intent intent = new Intent(android.provider.Settings.ACTION_NFC_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatch();

        Intent intent = getIntent();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            processTag(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            processTag(intent);
        }
    }

    private void enableForegroundDispatch() {
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[]{tagDetected};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    private void disableForegroundDispatch() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void processTag(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (rawMsgs != null && rawMsgs.length > 0) {
            NdefMessage message = (NdefMessage) rawMsgs[0];
            String payload = new String(message.getRecords()[0].getPayload(), StandardCharsets.UTF_8);
            Log.d(TAG, "Payload reçu :\n" + payload);

            String ownerId = null;
            if (payload.contains("[owner_id=")) {
                int start = payload.indexOf("[owner_id=") + 10;
                int end = payload.indexOf("]", start);
                if (end > start) {
                    ownerId = payload.substring(start, end);
                    Log.d(TAG, "Owner CIN extrait : " + ownerId);
                }
            }

            // Send to detail screen
            Intent detailIntent = new Intent(this, TagDetailsActivity.class);
            detailIntent.putExtra("nfc_data", payload);
            detailIntent.putExtra("owner_id", ownerId);
            startActivity(detailIntent);
            finish();
        } else {
            Toast.makeText(this, "Tag vide ou invalide.", Toast.LENGTH_SHORT).show();
        }
    }
}