package com.example.cocktailapp;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private Button loginButton;
    private Button signupButton;
    private Client client;
    private int backButtonCount;
    private ClientService clientService;
    private boolean isBound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ClientService.LocalBinder binder = (ClientService.LocalBinder) service;
            clientService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    
        Intent intent = new Intent(this, ClientService.class);
        startForegroundService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        

        signupButton = findViewById(R.id.RegisterButton);

        loginButton = findViewById(R.id.LoginButton);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backButtonCount++;
                // Show the pop-up dialog if the back button has been pressed 3 times
                if (backButtonCount == 1) {
                    showLogoutDialog(client);
                    backButtonCount = 0;
                }

            }
        });

        signupButton.setOnClickListener(v -> {
            startActivity(new Intent(this,SignUpActivity.class));
        });

        loginButton.setOnClickListener(v -> {
               startActivity(new Intent(this,LoginActivity.class));
        });
    }

    private void showLogoutDialog(Client client) {
        // Create a pop-up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Esci")
                .setMessage("Vuoi davvero uscire dall'applicazione?")
                .setPositiveButton("Si", (dialog, which) -> {
                    client.closeConnection();
                    finish();
                    System.exit(0);
                })
                .setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Change the color of the positive button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#F98500"));

        // Change the color of the negative button
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F98500"));


    }


}