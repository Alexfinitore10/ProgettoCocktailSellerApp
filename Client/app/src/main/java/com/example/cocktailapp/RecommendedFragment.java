package com.example.cocktailapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RecommendedFragment extends Fragment {
    private Client client;


    public RecommendedFragment() {
        // Required empty public constructor
    }


    public static RecommendedFragment newInstance() {
        RecommendedFragment fragment = new RecommendedFragment();
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
        return inflater.inflate(R.layout.fragment_recommended, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       // client = Client.getIstance();


    }
    //TODO: get recommended cocktails and shakes (Aspetta che venga implementato meglio lato server)
    public String getRecommendedCocktails() {
        String command = "9";
        client.sendData(command);
        return client.bufferedReceive();
    }

    public String getRecommendedShakes() {
        String command = "10";
        client.sendData(command);
        return client.bufferedReceive();
    }
}