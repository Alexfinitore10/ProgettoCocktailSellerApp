package com.example.CocktailApp.ActivityView;

import android.content.Intent;
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


public class SignUpActivity extends AppCompatActivity {
    private EditText Email, Password;
    private String risposta;
    private Client client;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        client = Client.getIstance();
        Email = findViewById(R.id.EmailEditTextSignUp);
        Password = findViewById(R.id.PasswordEditTextSignUp);
        Button signUpButton = findViewById(R.id.SendSignUpButton);


        signUpButton.setOnClickListener(v -> {

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
            // executor = Executors.newSingleThreadExecutor();
            // handler = new Handler(Looper.getMainLooper());

            //     executor.execute(() -> {
            //         risposta = sendSignup(Email.getText().toString(), Password.getText().toString());
            //         handler.post(() -> {
            //             if(risposta.equals("OK")){
            //                 Toast.makeText(this, parseRispostaRegistrazione(risposta), Toast.LENGTH_SHORT).show();
            //                 startActivity(new Intent(this, ShopActivity.class));
            //             }else{
            //                 Toast.makeText(this, parseRispostaRegistrazione(risposta), Toast.LENGTH_SHORT).show();
            //             }
            //         });
            //     });

            //     executor.shutdown();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> sendSignup(Email.getText().toString(), Password.getText().toString()));

            String risposta;
            try {
                risposta = future.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, "Errore durante l'esecuzione della registrazione", e);
                return;
            } catch (TimeoutException e) {
                Log.e(TAG, "Timeout durante l'esecuzione della registrazione", e);
                Toast.makeText(this, "Registrazione timed out", Toast.LENGTH_SHORT).show();
                return;
            }

            executor.shutdown();

            new Handler(Looper.getMainLooper()).post(() -> {
                String message = parseRispostaRegistrazione(risposta);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                if(risposta.equals("OK")){
                    startActivity(new Intent(this, ShopActivity.class));
                }
            });

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
                Log.e(TAG,"Risposta: " +risposta);
                return "Risposta dal server non valida";
        }
    }


}