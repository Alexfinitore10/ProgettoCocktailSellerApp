package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private Button sendLoginButton;
    private EditText EmailEditText;
    private EditText PasswordEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Client client = Client.getIstanza();
        sendLoginButton = findViewById(R.id.SendLogInButton);
        EmailEditText = findViewById(R.id.EmailEditText);
        PasswordEditText = findViewById(R.id.PasswordEditText);

        sendLoginButton.setOnClickListener(v -> {
            String email = EmailEditText.getText().toString();
            String password = PasswordEditText.getText().toString();
            String risposta = checkLogin(client,email, password);
            if(risposta.equals("OK")){
                Toast.makeText(this, "Login effettuato", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,ShopActivity.class));
            }else{
                Toast.makeText(this, "Login fallito: email o password errati", Toast.LENGTH_SHORT).show();
            }

        });
    }

    protected String checkLogin(Client client, String email, String password) {
        String data = "1`"+email+"`"+password+"`";
        String risposta = "";
        new Thread(() -> {
            try {
                client.sendData(data);
                client.receiveData(risposta);
            } catch (IOException e) {
                Log.e("Client LoginActivity","Errore nell'invio delle credenziali: "+e.getMessage());
            } catch (InterruptedException e) {
                Log.e("Client LoginActivity","Errore nel thread in checkLogin: "+e.getMessage());
            }
        }).start();
        Log.d("Client LoginActivity","Aspetto un po' prima di dare la risposta");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e("Client LoginActivity","Errore nella sleep di checkLogin: "+e.getMessage());
        }
        return risposta;
    }


}