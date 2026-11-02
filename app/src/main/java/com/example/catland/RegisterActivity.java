package com.example.catland;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    // ... existing code ...
    private Button takePictureButton, registerButtonMain;
    private TextView scannedFullNameTextView, scannedCinTextView;

    private static final int REQUEST_IMAGE_PICK = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;


    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final String TAG = "RegisterActivity";

    private String extractedCin = null;
    private String extractedFullName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        takePictureButton = findViewById(R.id.takePictureButton);
        registerButtonMain = findViewById(R.id.registerButton);
        scannedFullNameTextView = findViewById(R.id.scannedFullNameTextView);
        scannedCinTextView = findViewById(R.id.scannedCinTextView);

        takePictureButton.setOnClickListener(v -> showImageSourceDialog());


        registerButtonMain.setOnClickListener(v -> {
            if (extractedCin != null && extractedFullName != null) {
                CsvHelper csvHelper = new CsvHelper(this);
                boolean registered = csvHelper.registerOwner(extractedCin, extractedFullName);

                if (registered) {
                    new AlertDialog.Builder(this)
                            .setTitle("Inscription Réussie")
                            .setMessage("CIN: " + extractedCin + "\nNom Complet: " + extractedFullName + "\nVous pouvez maintenant vous connecter.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            })
                            .show();
                } else {
                    Toast.makeText(this, "Ce CIN est déjà enregistré. Veuillez vous connecter.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Veuillez d'abord scanner votre carte d'identité.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Aucune application caméra trouvée", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImageSourceDialog() {
        String[] options = {"Utiliser la Caméra", "Choisir depuis la Galerie"};
        new AlertDialog.Builder(this)
                .setTitle("Sélectionner une source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        } else {
                            openCamera();
                        }
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission caméra refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = null;

            if (requestCode == REQUEST_IMAGE_PICK && data.getData() != null) {
                try {
                    Uri imageUri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data.getExtras() != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
            }

            if (bitmap != null) {
                runTextRecognition(bitmap);
            } else {
                Toast.makeText(this, "Erreur lors de la lecture de l'image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void runTextRecognition(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image).addOnSuccessListener(visionText -> {
            String resultText = visionText.getText();
            Log.d(TAG, "Raw OCR Text (Register): \n" + resultText);

            // Attempt to extract from MRZ first
            extractedCin = extractCinFromMRZ(resultText);
            extractedFullName = extractFullNameFromMRZ(resultText);
            String extractionSource = "MRZ";

            if (extractedCin == null || extractedFullName == null) {
                Log.d(TAG, "MRZ extraction failed or incomplete. Trying fallback.");
                extractionSource = "Fallback";
                // Fallback to general extraction if MRZ fails
                if (extractedCin == null) {
                    extractedCin = extractCINGeneral(resultText);
                }
                if (extractedFullName == null) {
                    extractedFullName = extractFullNameGeneral(resultText);
                }
            }

            Log.d(TAG, "Extracted CIN (Register) via " + extractionSource + ": " + extractedCin);
            Log.d(TAG, "Extracted Full Name (Register) via " + extractionSource + ": " + extractedFullName);

            if (extractedFullName != null) {
                scannedFullNameTextView.setText(extractedFullName);
            } else {
                scannedFullNameTextView.setText("N/A");
            }
            if (extractedCin != null) {
                scannedCinTextView.setText(extractedCin);
            } else {
                scannedCinTextView.setText("N/A");
            }

            if (extractedCin != null && extractedFullName != null) {
                new AlertDialog.Builder(this).setTitle("Informations Extraites (" + extractionSource + ")").setMessage("CIN: " + extractedCin + "\nNom Complet: " + extractedFullName + "\n\nCliquez sur 'S'INSCRIRE' pour finaliser.").setPositiveButton("OK", null).show();
                Toast.makeText(this, "Informations extraites. Cliquez sur S'INSCRIRE.", Toast.LENGTH_LONG).show();
            } else {
                String message = "Impossible d'extraire les informations requises. Vérifier qualité d'image. Réessayez.\n";
                if (extractedFullName == null) message += "- Nom Complet non trouvé.\n";
                if (extractedCin == null) message += "- CIN non trouvé.\n";
                Toast.makeText(this, message.trim(), Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(this).setTitle("Erreur OCR").setMessage("Texte brut (pour débogage):\n" + resultText).setPositiveButton("OK", null).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "OCR Failure (Register): ", e);
            Toast.makeText(this, "Échec OCR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            scannedFullNameTextView.setText("Erreur OCR");
            scannedCinTextView.setText("Erreur OCR");
        });
    }

    private String extractCinFromMRZ(String text) {
        String[] lines = text.split("\n");
        // Pattern for the first line of MRZ containing CIN for Moroccan ID: IDMARO<...>CIN<<<<
        // Example: IDMAROPIAMLDO<1HH83691<<<<<<<<<<<<
        // We need to capture the CIN part like HH83691
        Pattern mrzCinPattern = Pattern.compile("^(?:ID|I<)MARO[A-Z0-9<]{0,15}?([A-Z]{1,2}[0-9]{5,7})");
        for (String line : lines) {
            String cleanedLine = line.replaceAll("\\s+", ""); // Remove spaces
            Matcher matcher = mrzCinPattern.matcher(cleanedLine);
            if (matcher.find()) {
                Log.d(TAG, "MRZ CIN Line Matched: " + line);
                return matcher.group(1).toUpperCase(); // Group 1 is the CIN
            }
        }
        return null;
    }

    private String extractFullNameFromMRZ(String text) {
        String[] lines = text.split("\n");
        // Pattern for the third line of MRZ: LASTNAME<<FIRSTNAME<<<<
        // Example: ZENBILE<<SAMIA<<<<<<<<<<<<<<<<<<<<<
        Pattern mrzNamePattern = Pattern.compile("^([A-Z]+)<<([A-Z]+(?:<[A-Z]+)*)<*");
        for (String line : lines) {
            String cleanedLine = line.replaceAll("\\s+", ""); // Remove spaces
            Matcher matcher = mrzNamePattern.matcher(cleanedLine);
            if (matcher.find()) {
                Log.d(TAG, "MRZ Name Line Matched: " + line);
                String lastName = matcher.group(1);
                String firstName = matcher.group(2).replace("<", " "); // Replace < between first names with space
                return firstName.trim() + " " + lastName.trim(); // Format as FirstName LastName
            }
        }
        return null;
    }

    private String extractFullNameGeneral(String text) {
        // ... (Keep your existing general full name extraction logic here as a fallback)
        String[] lines = text.split("\n");
        String firstName = null;
        String lastName = null;

        List<String> nonNameKeywords = Arrays.asList("ROYAUME", "MAROC", "CARTE", "NATIONALE", "IDENTITE", "IDENTITY", "CARD", "ADRESSE", "VALABLE", "JUSQU'AU", "SEXE", "DATE", "LIEU", "NAISSANCE", "SIGNATURE", "TITULAIRE", "EMPREINTE", "DIGITALE", "CAN");

        for (int i = 0; i < lines.length - 2; i++) {
            String line1 = lines[i].trim();
            String line2 = lines[i + 1].trim();
            String line3Lower = lines[i + 2].trim().toLowerCase();

            boolean line1IsPotentialName = line1.matches("^[A-ZÀ-ÖØ-Þ\\s'-]+$") && line1.length() > 2 && !isNumeric(line1) && !containsKeywords(line1, nonNameKeywords);
            boolean line2IsPotentialName = line2.matches("^[A-ZÀ-ÖØ-Þ\\s'-]+$") && line2.length() > 2 && !isNumeric(line2) && !containsKeywords(line2, nonNameKeywords);

            if (line1IsPotentialName && line2IsPotentialName) {
                if (line3Lower.startsWith("né le") || line3Lower.startsWith("née le") || line3Lower.startsWith("a ")) {
                    if (line1.split("\\s+").length <= 2 && line2.split("\\s+").length <= 2) {
                        if (line1.length() < line2.length() && line1.length() > 1 && line2.length() > 1) {
                            firstName = line1;
                            lastName = line2;
                        } else if (line2.length() < line1.length() && line1.length() > 1 && line2.length() > 1) {
                            firstName = line2;
                            lastName = line1;
                        } else {
                            firstName = line1;
                            lastName = line2;
                        }
                        return firstName + " " + lastName;
                    }
                }
            }
        }

        Pattern fullNamePattern = Pattern.compile("^([A-ZÀ-ÖØ-Þ'-]+)\\s+([A-ZÀ-ÖØ-Þ'-]+)$");
        for (String line : lines) {
            String currentLine = line.trim();
            if (containsKeywords(currentLine, nonNameKeywords) || isNumeric(currentLine) || currentLine.length() < 5) {
                continue;
            }
            Matcher matcher = fullNamePattern.matcher(currentLine);
            if (matcher.matches()) {
                String part1 = matcher.group(1);
                String part2 = matcher.group(2);
                if (part1.length() > 1 && part2.length() > 1) {
                    if (!nonNameKeywords.contains(part1.toUpperCase()) && !nonNameKeywords.contains(part2.toUpperCase())) {
                        return currentLine;
                    }
                }
            }
        }

        String longestCandidate = null;
        for (String line : lines) {
            String currentLine = line.trim();
            if (currentLine.matches("^[A-ZÀ-ÖØ-Þ\\s'-]+$") && currentLine.contains(" ") && !containsKeywords(currentLine, nonNameKeywords) && currentLine.length() > 4) {
                if (longestCandidate == null || currentLine.length() > longestCandidate.length()) {
                    if (currentLine.split("\\s+").length >= 2 && currentLine.split("\\s+").length <= 3) {
                        longestCandidate = currentLine;
                    }
                }
            }
        }
        if (longestCandidate != null) return longestCandidate;

        return null;
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        return str.matches("\\d+");
    }

    private boolean containsKeywords(String line, List<String> keywords) {
        if (line == null || line.isEmpty()) return false;
        String upperLine = line.toUpperCase();
        for (String keyword : keywords) {
            if (upperLine.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String extractCINGeneral(String text) {
        // ... (Keep your existing general CIN extraction logic here as a fallback)
        Pattern cinPattern = Pattern.compile("\\b([A-Z]{1,2}\\s?\\d{5,7})\\b");
        String bestCandidate = null;

        for (String line : text.split("\n")) {
            String currentLine = line.trim();
            Matcher matcher = cinPattern.matcher(currentLine);
            while (matcher.find()) {
                String potentialCin = matcher.group(1).replaceAll("\\s", "");

                if (currentLine.toLowerCase().contains("valable") || currentLine.toLowerCase().contains("jusqu") || currentLine.matches(".*\\d{2}[./-]\\d{2}[./-]\\d{4}.*") || currentLine.matches(".*\\d{4}[./-]\\d{2}[./-]\\d{2}.*")) {
                    continue;
                }

                if (currentLine.toUpperCase().startsWith("N°") || currentLine.toUpperCase().startsWith("CIN") || currentLine.toUpperCase().startsWith("ID") && !currentLine.toUpperCase().startsWith("IDMARO")) { // Avoid MRZ line here
                    return potentialCin.toUpperCase();
                }

                if (bestCandidate == null) {
                    bestCandidate = potentialCin;
                } else {
                    if (currentLine.replaceAll("[^A-Za-z0-9]", "").length() < 20) {
                        bestCandidate = potentialCin;
                    }
                }
            }
        }
        return bestCandidate != null ? bestCandidate.toUpperCase() : null;
    }
}