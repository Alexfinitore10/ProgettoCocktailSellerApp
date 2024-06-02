package com.example.cocktailapp;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class CocktailFragment extends Fragment {
    private CocktailRecyclerViewAdapter adapter;
    private ArrayList<CocktailLayoutClass> list;
    private RecyclerView recyclerView;
    private Client client;
    private ArrayList<Cocktail> cocktails;
    private String allCocktails;
    private Carrello carrello;
    private CartObserver model;
    private ExecutorService executor;
    private Handler handler;



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
        client = Client.getIstance();
        
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
        list = new ArrayList<>();
        cocktails = new ArrayList<>();
        recyclerView = view.findViewById(R.id.CocktailRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(model.getAllCocktails() != null){
            allCocktails = model.getAllCocktails();
        }else{
            allCocktails = "";
        }



        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        if(allCocktails.isEmpty()){
            executor.execute(() -> {
                allCocktails = getAllCocktails();
                model.setAllCocktails(allCocktails);
                handler.post(() -> {

                    cocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(allCocktails);
                    for (Cocktail c : cocktails) {
                        list.add(new CocktailLayoutClass(c.getNome(),c.getIngredienti(),c.getGradazione_alcolica(),c.getPrezzo(),c.getQuantita()));
                    }

                    adapter = new CocktailRecyclerViewAdapter(list,getContext(),cocktails,model);
                    recyclerView.setAdapter(adapter);
                });
            });
        }else{
            cocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(allCocktails);
            for (Cocktail c : cocktails) {
                list.add(new CocktailLayoutClass(c.getNome(),c.getIngredienti(),c.getGradazione_alcolica(),c.getPrezzo(),c.getQuantita()));
            }

            adapter = new CocktailRecyclerViewAdapter(list,getContext(),cocktails,model);
            recyclerView.setAdapter(adapter);
        }


        
        


        model.getResetCocktails().observe(getViewLifecycleOwner(), resetCocktails -> {
            Log.d("CocktailFragment", "resetCocktails: " + resetCocktails);
             if (resetCocktails) {
                 executor.execute(() -> {
                     allCocktails = getAllCocktails();
                     model.setAllCocktails(allCocktails);
                     handler.post(() -> {
                         cocktails.clear();
                         cocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(allCocktails);
                         list.clear();
                         for (int i = 0; i < cocktails.size(); i++) {
                             list.add(new CocktailLayoutClass(cocktails.get(i).getNome(),cocktails.get(i).getIngredienti(),cocktails.get(i).getGradazione_alcolica(),cocktails.get(i).getPrezzo(),cocktails.get(i).getQuantita()));
                         }
                         adapter.notifyDataSetChanged();
                         model.setResetCocktails(false);
                     });
                 });

             }
        });


    }
    private String getAllCocktails(){
        String command = "3";
        client.sendData(command);
        client.setSocketTimeout(3000);
        return client.bufferedReceive();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}

