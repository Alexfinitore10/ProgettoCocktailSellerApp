package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class SignUpActivity extends AppCompatActivity {
    private Button SignUpButton;
    private EditText Email, Password;
    private String risposta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Client client = Client.getIstance();
        Email = findViewById(R.id.EmailEditTextSignUp);
        Password = findViewById(R.id.PasswordEditTextSignUp);
        SignUpButton = findViewById(R.id.SendSignUpButton);

        // Set a listener on the email EditText to check the email format on every keystroke
        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Must be empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Must be empty
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isValidEmail = client.checkEmailRegex(Email.getText().toString());
                SignUpButton.setEnabled(isValidEmail);
            }
        });


        SignUpButton.setOnClickListener(v -> {
            String email = Email.getText().toString();
            String password = Password.getText().toString();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Email o Password vuote", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isValidEmail = client.checkEmailRegex(Email.getText().toString());
            if(!isValidEmail){
                Toast.makeText(this, "Email non valida", Toast.LENGTH_SHORT).show();
                return;
            }


            Runnable SignupTask = () -> {
                risposta = sendSignup(client, email, password);
            };

            try {
                Thread SignupThread = new Thread(SignupTask);
                SignupThread.start();
                SignupThread.join();
            } catch (InterruptedException e) {
                Log.e("SignupActivity thread","Errore nella join del thread:" +e.getMessage());
            }

            if(risposta.equals("OK")){
                Toast.makeText(this, parseRispostaRegistrazione(risposta), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,ShopActivity.class));
            }else{
                Toast.makeText(this, parseRispostaRegistrazione(risposta), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private String sendSignup(Client client, String input_email, String input_password) {
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