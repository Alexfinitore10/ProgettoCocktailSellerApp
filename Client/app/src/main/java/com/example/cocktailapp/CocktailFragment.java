package com.example.cocktailapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class CocktailFragment extends Fragment {
    private CocktailRecyclerViewAdapter adapter;
    private ArrayList<CocktailLayoutClass> list;
    private RecyclerView recyclerView;
    private Client client;
    private ArrayList<Cocktail> cocktails;
    private String allCocktails;
    private Carrello carrello;


    public CocktailFragment() {
        // Required empty public constructor
    }

    public static CocktailFragment newInstance() {
        CocktailFragment fragment = new CocktailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carrello = Carrello.getInstance();
    
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cocktail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CartObserver model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
        client = Client.getIstance();
        list = new ArrayList<>();
        cocktails = new ArrayList<>();

        Runnable getCocktailsTask = () -> allCocktails = getAllCocktails();
        Thread getCocktailsThread = new Thread(getCocktailsTask);
        getCocktailsThread.start();

        try {
            getCocktailsThread.join();
        } catch (InterruptedException e) {
            Log.e("onViewCreated CocktailFragment","Errore nella join del thread: " + e.getMessage());
        }


        recyclerView = view.findViewById(R.id.CocktailRecyclerView);

        cocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(allCocktails);

        for (Cocktail c : cocktails) {
            list.add(new CocktailLayoutClass(c.getNome(),c.getIngredienti(),c.getGradazione_alcolica(),c.getPrezzo(),c.getQuantita()));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CocktailRecyclerViewAdapter(list,getContext(),cocktails,model);
        recyclerView.setAdapter(adapter);


        model.getPaymentSuccess().observe(getViewLifecycleOwner(), paymentMade -> {
             if (paymentMade) {
                 Runnable getAgainCocktailsTask = () -> allCocktails = getAllCocktails();
                 Thread getAgainCocktailsThread = new Thread(getAgainCocktailsTask);
                 getAgainCocktailsThread.start();
                 try {
                     getAgainCocktailsThread.join();
                 } catch (InterruptedException e) {
                     Log.e("onViewCreated CocktailFragment","Errore nella join del thread: " + e.getMessage());
                 }
                 cocktails.clear();
                 cocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(allCocktails);
                 list.clear();
                 for (Cocktail c : cocktails) {
                     list.add(new CocktailLayoutClass(c.getNome(),c.getIngredienti(),c.getGradazione_alcolica(),c.getPrezzo(),c.getQuantita()));
                 }
                 adapter.notifyDataSetChanged();
             }
        });


    }

    private String getAllCocktails(){
        String command = "3";
        client.sendData(command);
        return client.bufferedReceive();
    }



}

