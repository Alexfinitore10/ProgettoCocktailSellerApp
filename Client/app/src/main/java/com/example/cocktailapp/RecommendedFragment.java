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


public class RecommendedFragment extends Fragment {
    private Client client;
    private RecommendedRecyclerViewAdapter adapter;
    private ArrayList<RecommendedLayoutClass> list;
    private RecyclerView recyclerView;
    private ArrayList<Cocktail> recommendedCocktails;
    private ArrayList<Shake> recommendedShakes;
    private String allCocktails;
    private String allShakes;

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
        client = Client.getIstance();
        list = new ArrayList<>();

        CartObserver model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
        Runnable getCocktailsTask = () -> allCocktails = getRecommendedCocktails();
        Thread getCocktailsThread = new Thread(getCocktailsTask);
        getCocktailsThread.start();

        try {
            getCocktailsThread.join();
        } catch (InterruptedException e) {
            Log.e("RecommendedFragment cocktail thread","Errore nella join del thread: " + e.getMessage());
        }
        Runnable getShakesTask = () -> allShakes = getRecommendedShakes();
        Thread getShakesThread = new Thread(getShakesTask);
        getShakesThread.start();

        try{
            getShakesThread.join();
        }catch(InterruptedException e){
            Log.e("RecommendedFragment shake thread","Errore nella join del thread: " + e.getMessage());
        }
        if(isGetCocktailsOk()){
            recommendedCocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(allCocktails);
            for(Cocktail cocktail : recommendedCocktails){
                list.add(new RecommendedLayoutClass(cocktail));
            }
        }

        if(isGetShakesOk()){
            recommendedShakes = (ArrayList<Shake>) Shake.setShakes(allShakes);
            for(Shake shake : recommendedShakes){
                list.add(new RecommendedLayoutClass(shake));
            }
        }

        recyclerView = view.findViewById(R.id.RecommendedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecommendedRecyclerViewAdapter(list,getContext(),recommendedCocktails,recommendedShakes,model);
        recyclerView.setAdapter(adapter);

    }

    private String getRecommendedCocktails() {
        String command = "9";
        client.sendData(command);
        return client.bufferedReceive();
    }

    private boolean isGetCocktailsOk() {
        switch (allCocktails) {
            case "NOKERR":
                System.err.println("Non è stato possibile prendere i recommended dal server");
                return false;
            case "Ness":
                System.err.println("Nessun drink è presente ancora nei recommend, quindi non è possibile effettuare i recommend");
                return false;
            case "Pochi":
                System.err.println("Non ci sono abbastanza drinks per effettuare un recommend, acquista qualche drink prima");
                return false;
            default:
                return true;
        }
    }

    private String getRecommendedShakes() {
        String command = "10";
        client.sendData(command);
        return client.bufferedReceive();
    }

    private boolean isGetShakesOk() {
        switch (allShakes) {
            case "NOKERR":
                System.err.println("Non è stato possibile prendere i recommended dal server");
                return false;
            case "Ness":
                System.err.println("Nessun drink è presente ancora nei recommend, quindi non è possibile effettuare i recommend");
                return false;
            case "Pochi":
                System.err.println("Non ci sono abbastanza drinks per effettuare un recommend, acquista qualche drink prima");
                return false;
            default:
                return true;
        }
    }
}