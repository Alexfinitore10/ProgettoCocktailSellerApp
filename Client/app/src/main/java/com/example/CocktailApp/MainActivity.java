package com.example.CocktailApp;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;

import com.example.CocktailApp.ActivityView.LoginActivity;
import com.example.CocktailApp.ActivityView.SignUpActivity;
import com.example.CocktailApp.Model.Client;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    private ExecutorService executor;
    private Client client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //Forza light mode
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            client = Client.getIstance();
        });


        Button signupButton = findViewById(R.id.RegisterButton);

        Button loginButton = findViewById(R.id.LoginButton);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLogoutDialog();
            }
        });

        signupButton.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));

        loginButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void showLogoutDialog() {
        // Create a pop-up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Esci")
                .setMessage("Vuoi davvero uscire dall'applicazione?")
                .setPositiveButton("Si", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Change the color of the positive button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#F98500"));

        // Change the color of the negative button
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F98500"));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }


}