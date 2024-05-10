package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private Button sendLoginButton;
    private EditText EmailEditText, PasswordEditText;
    private Client client;
    private String risposta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       // client = Client.getIstanza();
        sendLoginButton = findViewById(R.id.SendLogInButton);
        EmailEditText = findViewById(R.id.EmailEditText);
        PasswordEditText = findViewById(R.id.PasswordEditText);


        sendLoginButton.setOnClickListener(v -> {
//            String email = EmailEditText.getText().toString();
//            String password = PasswordEditText.getText().toString();

//            Runnable LoginTask = () -> risposta = sendLogin(client, email, password);

//            try {
//                Thread LoginThread = new Thread(LoginTask);
//                LoginThread.start();
//                LoginThread.join();
//            } catch (InterruptedException e) {
//                Log.e("LoginActivity thread","Errore nella join del thread:" +e.getMessage());
//            }


//            if(risposta.equals("OK")){
//                Toast.makeText(this, "Login effettuato", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,ShopActivity.class));
//            }else{
//                Toast.makeText(this, "Login fallito: email o password errati", Toast.LENGTH_SHORT).show();
//            }

        });
    }

    private String sendLogin(Client client, String email, String password) {
        String dati = "1`"+email+"`"+password+"`";
        String risposta = "";

        try {
            client.sendData(dati);
            risposta = client.receiveData();
            return risposta;
        } catch (IOException e) {
            Log.e("sendLogin", "Errore nell'invio/ricezione di dati al server:" +e.getMessage());
        } catch (InterruptedException e) {
            Log.e("sendLogin", "Errore socket sendLogin invio:" +e.getMessage());
        }

        return risposta;
    }


}