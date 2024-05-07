package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;


public class SignUpActivity extends AppCompatActivity {
    private Button SignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SignUpButton = findViewById(R.id.SendSignUpButton);

        SignUpButton.setOnClickListener(v -> {

        });
    }
}