package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private Button loginButton;
    private Button signupButton;
    private Client client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Runnable RunClientStarterMain = () -> {
//            client = Client.getIstanza();
//        };
//
//        try {
//            Thread ClientStarterMain = new Thread(RunClientStarterMain);
//            ClientStarterMain.start();
//            ClientStarterMain.join();
//        } catch (InterruptedException e) {
//            Log.e("MainActivity thread","Errore nella join del thread:" +e.getMessage());
//        }

        signupButton = findViewById(R.id.RegisterButton);

        loginButton = findViewById(R.id.LoginButton);

        signupButton.setOnClickListener(v -> {
            startActivity(new Intent(this,SignUpActivity.class));
        });

        loginButton.setOnClickListener(v -> {
               startActivity(new Intent(this,LoginActivity.class));
        });
    }


}