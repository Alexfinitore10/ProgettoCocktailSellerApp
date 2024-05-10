package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.IOException;


public class SignUpActivity extends AppCompatActivity {
    private Button SignUpButton;
    private EditText Email, Password;
    private String risposta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//        Client client = Client.getIstanza();
//        Email = findViewById(R.id.EmailEditTextSignUp);
//        Password = findViewById(R.id.PasswordEditTextSignUp);
        SignUpButton = findViewById(R.id.SendSignUpButton);


        SignUpButton.setOnClickListener(v -> {
//            String email = Email.getText().toString();
//            String password = Password.getText().toString();
//
//            Runnable SignupTask = () -> risposta = sendSignup(client, email, password);
//
//            try {
//                Thread SignupThread = new Thread(SignupTask);
//                SignupThread.start();
//                SignupThread.join();
//            } catch (InterruptedException e) {
//                Log.e("SignupActivity thread","Errore nella join del thread:" +e.getMessage());
//            }

//            if(risposta.equals("OK")){
//                Toast.makeText(this, "Registrazione effettuata con successo", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,ShopActivity.class));
//            }else{
//                Toast.makeText(this, "Registrazione fallita", Toast.LENGTH_SHORT).show();
//            }

        });
    }

    private String sendSignup(Client client, String email, String password) {
        String dati = "2`"+email+"`"+password+"`";
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
}