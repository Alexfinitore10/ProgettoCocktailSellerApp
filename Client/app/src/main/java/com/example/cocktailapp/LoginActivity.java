package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {
    private Button sendLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sendLoginButton = findViewById(R.id.SendLogInButton);

        sendLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(this,ShopActivity.class));
        });
    }
}