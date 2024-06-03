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


public class RecommendedFragment extends Fragment {
    private Client client;
    private RecommendedRecyclerViewAdapter adapter;
    private ArrayList<RecommendedLayoutClass> list;
    private RecyclerView recyclerView;
    private ArrayList<Cocktail> recommendedCocktails,allCocktails;
    private ArrayList<Shake> recommendedShakes,allShakes;
    private String recommendedCocktailsString, recommendedShakesString, allCocktailsString, allShakesString;
    private CartObserver model;


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
        recommendedCocktails = new ArrayList<>();
        recommendedShakes = new ArrayList<>();
        allCocktails = new ArrayList<>();
        allShakes = new ArrayList<>();

        recyclerView = view.findViewById(R.id.RecommendedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
        if(model.getAllCocktails() != null){
            allCocktailsString = model.getAllCocktails();
        }else{
            allCocktailsString = "";
        }

        if(model.getRecommendedCocktails() != null){
            recommendedCocktailsString = model.getRecommendedCocktails();
        }else{
            recommendedCocktailsString = "";
        }

        if(model.getAllShakes() != null){
            allShakesString = model.getAllShakes();
        }else{
            allShakesString = "";
        }

        if(model.getRecommendedShakes() != null){
            recommendedShakesString = model.getRecommendedShakes();
        }else{
            recommendedShakesString = "";
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());


        if(recommendedCocktails.isEmpty() || recommendedShakes.isEmpty()) {

            executor.execute(() -> {
                recommendedCocktailsString = getRecommendedCocktails();
                allCocktailsString = getAllCocktails();
                recommendedShakesString = getRecommendedShakes();
                allShakesString = getAllShakes();

                handler.post(() -> {
                    if (isGetCocktailsOk()) {
                        model.setRecommendedCocktails(recommendedCocktailsString);
                        model.setAllCocktails(allCocktailsString);
                        recommendedCocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(recommendedCocktailsString);
                        allCocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(allCocktailsString);
                        for (Cocktail cocktail : recommendedCocktails) {
                            for (Cocktail c : allCocktails) {
                                if (cocktail.getNome().equals(c.getNome())) {
                                    cocktail.setQuantita(c.getQuantita());
                                }
                            }
                        }
                        for (Cocktail cocktail : recommendedCocktails) {
                            list.add(new RecommendedLayoutClass(cocktail));
                        }
                    }

                    if (isGetShakesOk()) {
                        model.setRecommendedShakes(recommendedShakesString);
                        model.setAllShakes(allShakesString);
                        recommendedShakes = (ArrayList<Shake>) Shake.setShakes(recommendedShakesString);
                        allShakes = (ArrayList<Shake>) Shake.setShakes(allShakesString);
                        for (Shake shake : recommendedShakes) {
                            for (Shake s : allShakes) {
                                if (shake.getNome().equals(s.getNome())) {
                                    shake.setQuantita(s.getQuantita());
                                }
                            }
                        }
                        for (Shake shake : recommendedShakes) {
                            list.add(new RecommendedLayoutClass(shake));
                        }
                    }


                    adapter = new RecommendedRecyclerViewAdapter(list, getContext(), recommendedCocktails, recommendedShakes, model);
                    recyclerView.setAdapter(adapter);

                });
            });
        }else{
            recommendedCocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(recommendedCocktailsString);
            recommendedShakes = (ArrayList<Shake>) Shake.setShakes(recommendedShakesString);
            allCocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(allCocktailsString);
            allShakes = (ArrayList<Shake>) Shake.setShakes(allShakesString);

            for (Cocktail cocktail : recommendedCocktails) {
                for (Cocktail c : allCocktails) {
                    if (cocktail.getNome().equals(c.getNome())) {
                        cocktail.setQuantita(c.getQuantita());
                    }
                }
            }
            for (Cocktail cocktail : recommendedCocktails) {
                list.add(new RecommendedLayoutClass(cocktail));
            }

            for (Shake shake : recommendedShakes) {
                for (Shake s : allShakes) {
                    if (shake.getNome().equals(s.getNome())) {
                        shake.setQuantita(s.getQuantita());
                    }
                }
            }
            for (Shake shake : recommendedShakes) {
                list.add(new RecommendedLayoutClass(shake));
            }

            adapter = new RecommendedRecyclerViewAdapter(list, getContext(), recommendedCocktails, recommendedShakes, model);
            recyclerView.setAdapter(adapter);
        }

        executor.shutdown();

//        Runnable getCocktailsTask = () -> {
//            recommendedCocktailsString = getRecommendedCocktails();
//            allCocktailsString = getAllCocktails();
//        };
//        Thread getCocktailsThread = new Thread(getCocktailsTask);
//        getCocktailsThread.start();
//
//        try {
//            getCocktailsThread.join();
//        } catch (InterruptedException e) {
//            Log.e("RecommendedFragment cocktail thread","Errore nella join del thread: " + e.getMessage());
//        }
//        Runnable getShakesTask = () -> {
//            recommendedShakesString = getRecommendedShakes();
//            allShakesString = getAllShakes();
//        };
//        Thread getShakesThread = new Thread(getShakesTask);
//        getShakesThread.start();
//
//        try{
//            getShakesThread.join();
//        }catch(InterruptedException e){
//            Log.e("RecommendedFragment shake thread","Errore nella join del thread: " + e.getMessage());
//        }
//        if(isGetCocktailsOk()){
//            recommendedCocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(recommendedCocktailsString);
//            allCocktails = (ArrayList<Cocktail>) Cocktail.setCocktails(allCocktailsString);
//            for(Cocktail cocktail : recommendedCocktails){
//                for(Cocktail c : allCocktails){
//                    if(cocktail.getNome().equals(c.getNome())){
//                        cocktail.setQuantita(c.getQuantita());
//                    }
//                }
//            }
//            for(Cocktail cocktail : recommendedCocktails){
//                list.add(new RecommendedLayoutClass(cocktail));
//            }
//        }

//        if(isGetShakesOk()){
//            recommendedShakes = (ArrayList<Shake>) Shake.setShakes(recommendedShakesString);
//            allShakes = (ArrayList<Shake>) Shake.setShakes(allShakesString);
//            for(Shake shake : recommendedShakes){
//                for(Shake s : allShakes) {
//                    if (shake.getNome().equals(s.getNome())) {
//                        shake.setQuantita(s.getQuantita());
//                    }
//                }
//            }
//            for(Shake shake : recommendedShakes){
//                list.add(new RecommendedLayoutClass(shake));
//            }
//        }




    }

    private String getRecommendedCocktails() {
        String command = "9";
        client.sendData(command);
        client.setSocketTimeout(3000);
        return client.bufferedReceive();
    }

    private String getAllCocktails(){
        String command = "3";
        client.sendData(command);
        client.setSocketTimeout(3000);
        return client.bufferedReceive();
    }

    private boolean isGetCocktailsOk() {
        switch (recommendedCocktailsString) {
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
        client.setSocketTimeout(3000);
        return client.bufferedReceive();
    }
    private String getAllShakes() {
        String command = "4";
        client.sendData(command);
        client.setSocketTimeout(3000);
        return client.bufferedReceive();
    }


    private boolean isGetShakesOk() {
        switch (recommendedShakesString) {
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