package com.example.cocktailapp.ActivityView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.*;

import com.example.cocktailapp.Model.Client;
import com.example.cocktailapp.R;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SignUpActivity extends AppCompatActivity {
    private Button SignUpButton;
    private EditText Email, Password;
    private String risposta;
    private Client client;
    private ExecutorService executor;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        client = Client.getIstance();
        Email = findViewById(R.id.EmailEditTextSignUp);
        Password = findViewById(R.id.PasswordEditTextSignUp);
        SignUpButton = findViewById(R.id.SendSignUpButton);


        SignUpButton.setOnClickListener(v -> {

            if(Email.getText().toString().isEmpty() && Password.getText().toString().isEmpty()){
                Toast.makeText(this, "Inserisci email e password", Toast.LENGTH_SHORT).show();
                return;
            }else if(Email.getText().toString().isEmpty()){
                Toast.makeText(this, "Inserisci email", Toast.LENGTH_SHORT).show();
                return;
            }else if(Password.getText().toString().isEmpty()){
                Toast.makeText(this, "Inserisci password", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isValidEmail = client.checkEmailRegex(Email.getText().toString());
            if(!isValidEmail){
                Toast.makeText(this, "Email non valida", Toast.LENGTH_SHORT).show();
                return;
            }
            executor = Executors.newSingleThreadExecutor();
            handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    risposta = sendSignup(Email.getText().toString(), Password.getText().toString());
                    handler.post(() -> {
                        if(risposta.equals("OK")){
                            Toast.makeText(this, parseRispostaRegistrazione(risposta), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this,ShopActivity.class));
                        }else{
                            Toast.makeText(this, parseRispostaRegistrazione(risposta), Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                executor.shutdown();

        });



    }

    private String sendSignup(String input_email, String input_password) {
        String password;

        try {
            password = client.hash(input_password);
        } catch (NoSuchAlgorithmException e) {
            Log.e("sendSignup", "Errore nella hashing della password:" +e.getMessage());
            return  "";
        }

        String dati = "2`"+input_email+"`"+password+"`";
        String risposta = "";

        try {
            client.sendData(dati);
            risposta = client.receiveData();
            return risposta;
        } catch (IOException e) {
            Log.e("sendSignup", "Errore nell'invio/ricezione di dati al server:" +e.getMessage());
        }catch (InterruptedException e) {
            Log.e("sendSignup", "Errore socket sendSignup ricezione:" +e.getMessage());
        }

        return risposta;
    }

    private String parseRispostaRegistrazione(String risposta) {
        switch (risposta) {
            case "NOK_Already":
                return "Registrazione fallita, Utente già registrato";
            case "OK":
                return "Registrazione effettuata con successo";
            case "NOK_Registration":
                return "Errore di registrazione causato dal Server";
            case "NOK_Unknown":
                return "Errore Generico";
            default:
                return "Risposta dal server non valida";
        }
    }


}