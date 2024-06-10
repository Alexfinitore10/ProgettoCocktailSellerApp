package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private Button sendLoginButton;
    private EditText EmailEditText, PasswordEditText;
    private Client client;
    private String risposta;
    private ExecutorService executor;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        client = Client.getIstance();
        sendLoginButton = findViewById(R.id.SendLogInButton);
        EmailEditText = findViewById(R.id.EmailEditText);
        PasswordEditText = findViewById(R.id.PasswordEditText);


        sendLoginButton.setOnClickListener(v -> {
            if(EmailEditText.getText().toString().isEmpty() && PasswordEditText.getText().toString().isEmpty()){
                Toast.makeText(this, "Inserisci email e password", Toast.LENGTH_SHORT).show();
                return;
            }else if(EmailEditText.getText().toString().isEmpty()){
                Toast.makeText(this, "Inserisci email", Toast.LENGTH_SHORT).show();
                return;
            }else if(PasswordEditText.getText().toString().isEmpty()){
                Toast.makeText(this, "Inserisci password", Toast.LENGTH_SHORT).show();
                return;
            }

            executor = Executors.newSingleThreadExecutor();
            handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                risposta = sendLogin(EmailEditText.getText().toString(), PasswordEditText.getText().toString());
                handler.post(() -> {
                    if(risposta.equals("OK")){
                        client.setLogged(true);
                        Toast.makeText(this, "Login effettuato", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this,ShopActivity.class));
                    }else{
                        Toast.makeText(this, "Login fallito: email o password errati", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            executor.shutdown();
        });

    }

    private String sendLogin(String input_email, String input_password) {

        String password;

        try {
            password = client.hash(input_password);
        } catch (NoSuchAlgorithmException e) {
            Log.e("sendLogin", "Errore nella hashing della password:" +e.getMessage());
            return  "";
        }

        String dati = "1`"+input_email+"`"+password+"`";
        String risposta = "";

        try {
            int bytes_sent = client.sendData(dati);
            Log.d("sendLogin", "Bytes sent: " + bytes_sent);
            client.setSocketTimeout(5000);
            risposta = client.receiveData();
            return risposta;
        } catch (IOException e) {
            Log.e("sendLogin", "Errore nell'invio/ricezione di dati al server:" +e.getMessage());
        } catch (InterruptedException e) {
            Log.e("sendLogin", "Errore socket sendLogin invio:" +e.getMessage());
        } catch (Exception e){
            Log.e("sendLogin", "Errore socket sendLogin:" +e.getMessage());
        }

        return risposta;
    }



}