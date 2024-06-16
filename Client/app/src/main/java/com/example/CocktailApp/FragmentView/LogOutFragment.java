package com.example.CocktailApp.FragmentView;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.CocktailApp.ActivityView.ShopActivity;
import com.example.CocktailApp.MainActivity;
import com.example.CocktailApp.Model.Carrello;
import com.example.CocktailApp.Model.Client;
import com.example.CocktailApp.R;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LogOutFragment extends Fragment {
    private static LogOutFragment instance;
    private static final String TAG = "LogOutFragment";
    private Client client;

    private LogOutFragment() {

    }

    public static LogOutFragment getInstance() {
        if(instance == null){
            Log.d(TAG,"creazione istanza");
            instance = new LogOutFragment();
        }
        return instance;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        client = Client.getIstance();
        return inflater.inflate(R.layout.fragment_log_out, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button LogOutButton = view.findViewById(R.id.LogOutButton);
        Carrello carrello = Carrello.getInstance();


        LogOutButton.setOnClickListener(v -> {
            carrello.emptyCarrello();
            new Thread(this::sendLogout).start();
            new Handler(Looper.getMainLooper()).post(() -> {
                client.setLogged(false);
                Toast.makeText(getContext(), "Logout effettuato", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), MainActivity.class));
            });
        });
    }

    private void sendLogout(){
        client.sendData("6");
    }


}