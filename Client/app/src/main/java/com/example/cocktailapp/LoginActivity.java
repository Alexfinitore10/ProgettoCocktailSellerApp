package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private Button sendLoginButton;
    private EditText EmailEditText, PasswordEditText;
    private Client client;
    private String risposta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        client = Client.getIstance();
        sendLoginButton = findViewById(R.id.SendLogInButton);
        EmailEditText = findViewById(R.id.EmailEditText);
        PasswordEditText = findViewById(R.id.PasswordEditText);


        sendLoginButton.setOnClickListener(v -> {
            String email = EmailEditText.getText().toString();
            String password = PasswordEditText.getText().toString();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Inserisci email e password", Toast.LENGTH_SHORT).show();
                return;
            }

            Runnable LoginTask = () -> risposta = sendLogin(email, password);

                Thread LoginThread = new Thread(LoginTask);
                LoginThread.start();
                try {
                    LoginThread.join();
                } catch (InterruptedException e) {
                    Log.e("sendLogin", "Errore nella join del thread:" +e.getMessage());
                }



            if(risposta.equals("OK")){
                client.setLogged(true);
                Toast.makeText(this, "Login effettuato", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,ShopActivity.class));
            }else{
                Toast.makeText(this, "Login fallito: email o password errati", Toast.LENGTH_SHORT).show();
            }

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
            client.sendData(dati);
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