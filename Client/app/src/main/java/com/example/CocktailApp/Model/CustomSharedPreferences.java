package com.example.CocktailApp.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

/** Wrapper class around {@link SharedPreferences} */
public class CustomSharedPreferences {
    private static final String _TAG = "[CustomSharedPreferences] ";
    private static final String PREF_NAME = "app_settings";

    private static CustomSharedPreferences instance;
    private static SharedPreferences preferences;

    private CustomSharedPreferences(@NonNull Context context) { preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); }
    private CustomSharedPreferences(@NonNull Context context, String key) { preferences = context.getApplicationContext().getSharedPreferences(key, Context.MODE_PRIVATE); }

    public static CustomSharedPreferences getInstance(@NonNull Context context) {
        if (instance == null)
            instance = new CustomSharedPreferences(context);
        return instance;
    }

    public static CustomSharedPreferences getInstance(@NonNull Context context, boolean forceInstantiation) {
        if (forceInstantiation)
            instance = new CustomSharedPreferences(context);
        return instance;
    }

    public static CustomSharedPreferences getInstance(@NonNull Context context, String preferencesName) {
        if (instance == null)
            instance = new CustomSharedPreferences(context, preferencesName);
        return instance;
    }

    public String read(String key, String defaultValue) { return preferences.getString(key, defaultValue); }
    public Integer read(String key, Integer defaultValue) { return preferences.getInt(key, defaultValue); }
    public Long read(String key, Long defaultValue) { return preferences.getLong(key, defaultValue); }
    public Boolean read(String key, Boolean defaultValue) { return preferences.getBoolean(key, defaultValue); }

    public boolean contains(String key){ return preferences.contains(key); }

    public <T> void write(String key, T value) {
        SharedPreferences.Editor editor = preferences.edit();

        // Retrieve the type of value using the simple name of the underlying class as given in the source code
        // avoiding using instanceof for each possible type (Supported type are String, Integer, Long, Boolean)
        switch (value.getClass().getSimpleName()) {
            case "String" -> editor.putString(key, (String) value);
            case "Integer" -> editor.putInt(key, (Integer) value);
            case "Long" -> editor.putLong(key, (Long) value);
            case "Boolean" -> editor.putBoolean(key, (Boolean) value);
            default -> Log.d(_TAG, "Unknown type <" + value.getClass().getSimpleName() + "> was given");
        }
        editor.apply();
    }

    public void clear() { preferences.edit().clear().apply(); }


    public boolean isEmpty(){
        return preferences.getAll().isEmpty();
    }
}
