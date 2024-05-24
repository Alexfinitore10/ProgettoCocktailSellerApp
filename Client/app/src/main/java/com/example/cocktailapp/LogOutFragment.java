package com.example.cocktailapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class LogOutFragment extends Fragment {
    private Button LogOut;
    private Client client;

    public LogOutFragment() {
        // Required empty public constructor
    }


    public static LogOutFragment newInstance(String param1, String param2) {
        LogOutFragment fragment = new LogOutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_out, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       client = Client.getIstanza();

       LogOut = view.findViewById(R.id.LogOutButton);

       LogOut.setOnClickListener(v -> {
           client.setLogged(false);
           startActivity(new Intent(getContext(),MainActivity.class));
       });



    }
}