package com.example.CocktailApp.ActivityView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.CocktailApp.Model.Client;
import com.example.CocktailApp.R;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    private EditText EmailEditText, PasswordEditText;
    private Client client;
    private String risposta = "";
    private final static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        client = Client.getIstance();
        Button sendLoginButton = findViewById(R.id.SendLogInButton);
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


            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> sendLogin(EmailEditText.getText().toString(), PasswordEditText.getText().toString()));

            try {
                risposta = future.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, "Errore durante l'esecuzione del login" +e.getMessage());
            } catch (TimeoutException e) {
                Log.e(TAG, "Timeout durante l'esecuzione del login" +e.getMessage());
                Toast.makeText(this, "Login timed out", Toast.LENGTH_SHORT).show();
                return;
            }

            executor.shutdown();

            new Handler(Looper.getMainLooper()).post(() -> {
                if(risposta.equals("OK")){
                    client.setLogged(true);
                    Toast.makeText(this, "Login effettuato", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, ShopActivity.class));
                }else{
                    Toast.makeText(this, "Login fallito: email o password errati", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private String sendLogin(String input_email, String input_password) {

        String password;

        try {
            password = client.hash(input_password);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Errore nella hashing della password:" +e.getMessage());
            return  "";
        }

        String dati = "1`"+input_email+"`"+password+"`";


        try {
            int bytes_sent = client.sendData(dati);
            Log.d(TAG, "Bytes sent: " + bytes_sent);
            client.setSocketTimeout(5000);
            return client.receiveData();

        } catch (IOException e) {
            Log.e(TAG, "Errore nell'invio/ricezione di dati al server:" +e.getMessage());
            return "";
        } catch (InterruptedException e) {
            Log.e(TAG, "Errore socket sendLogin invio:" +e.getMessage());
            return "";
        } catch (Exception e){
            Log.e(TAG, "Errore socket sendLogin:" +e.getMessage());
            return "";
        }


    }



}