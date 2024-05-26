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
    private CocktailRecyclerViewAdapter cocktailRecyclerViewAdapter;
    private ArrayList<CocktailLayoutClass> list;
    private RecyclerView recyclerView;
    private Client client;
    private ArrayList<Cocktail> cocktails;
    private String allCocktails;
    private Carrello carrello;
    private CartObserver model;


    public CocktailFragment() {
        // Required empty public constructor
    }

    public static CocktailFragment newInstance(String param1, String param2) {
        CocktailFragment fragment = new CocktailFragment();
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
        return inflater.inflate(R.layout.fragment_cocktail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
        carrello = Carrello.getInstance();
        client = Client.getIstance();
        list = new ArrayList<>();
        cocktails = new ArrayList<>();

        Runnable getCocktailsTask = () -> allCocktails = getAllCocktails(client);
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
        cocktailRecyclerViewAdapter = new CocktailRecyclerViewAdapter(list,getContext(),cocktails,model);
        recyclerView.setAdapter(cocktailRecyclerViewAdapter);



    }

    private String getAllCocktails(Client client){
        String command = "3";
        client.sendData(command);
        return client.bufferedReceive();
    }



}

