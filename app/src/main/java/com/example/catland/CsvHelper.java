package com.example.catland;

import android.content.Context;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvHelper {
    private static final String FILE_NAME = "users.csv";
    private final File file;

    public CsvHelper(Context context) {
        file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("id_card,full_name\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isCinRegistered(String cin) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length > 0 && parts[0].equalsIgnoreCase(cin)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerOwner(String cin, String fullName) {
        if (isCinRegistered(cin)) return false;
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(cin + "," + fullName + "\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getFullName(String cin) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2 && parts[0].equalsIgnoreCase(cin)) {
                    return parts[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllCinList() {
        List<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length > 0) {
                    list.add(parts[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
