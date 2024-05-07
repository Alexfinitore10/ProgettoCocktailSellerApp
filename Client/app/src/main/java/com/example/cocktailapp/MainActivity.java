package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private Button loginButton;
    private Button signupButton;
    protected Client client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = Client.getIstanza();

        signupButton = findViewById(R.id.RegisterButton);

        loginButton = findViewById(R.id.LoginButton);

        signupButton.setOnClickListener(v -> {
            startActivity(new Intent(this,SignUpActivity.class));
        });

        loginButton.setOnClickListener(v -> {
               startActivity(new Intent(this,LoginActivity.class));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //client.closeConnection();
    }
}