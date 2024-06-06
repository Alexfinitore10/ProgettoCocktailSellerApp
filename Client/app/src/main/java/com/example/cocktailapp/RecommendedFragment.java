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
import android.widget.Toast;

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
    private ExecutorService executor;
    private Handler handler;
    private RecyclerView.AdapterDataObserver observer;
    private boolean isObserverRegistered = false;


    public RecommendedFragment() {
        // Required empty public constructor
    }


    public static RecommendedFragment newInstance() {
        return new RecommendedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = Client.getIstance();
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
        list = new ArrayList<>();
        recommendedCocktails = new ArrayList<>();
        recommendedShakes = new ArrayList<>();
        allCocktails = new ArrayList<>();
        allShakes = new ArrayList<>();

        recyclerView = view.findViewById(R.id.RecommendedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                recyclerView.post(() -> fillRecommended());
            }
        };

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

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());


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
                        recommendedCocktails = Cocktail.setRecommendedCocktails(recommendedCocktailsString);
                        allCocktails = Cocktail.parseCocktails(allCocktailsString);
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
                        recommendedShakes = Shake.setRecommendedShakes(recommendedShakesString);
                        allShakes = Shake.parseShake(allShakesString);
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
                    if(adapter != null && !isObserverRegistered) {
                        adapter.registerAdapterDataObserver(observer);
                        isObserverRegistered = true;
                    }else if(adapter == null){
                        Log.e("RecommendedFragment", "onCreateView: adapter null quando recommended cocktails/shakes vuoto");
                    }else{
                        observerCall();
                    }

                });
            });
        }else{
            if(!isGetCocktailsOk()){
                Toast.makeText(getContext(), "Non ci sono cocktail da consigliare", Toast.LENGTH_SHORT).show();
            }else{
                recommendedCocktails = Cocktail.setRecommendedCocktails(recommendedCocktailsString);
                allCocktails = Cocktail.parseCocktails(allCocktailsString);

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
            if(!isGetShakesOk()){
                Toast.makeText(getContext(), "Non ci sono frullati da consigliare", Toast.LENGTH_SHORT).show();
            }else{
                recommendedShakes = Shake.setRecommendedShakes(recommendedShakesString);
                allShakes = Shake.parseShake(allShakesString);

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
            if(adapter != null && !isObserverRegistered) {
                adapter.registerAdapterDataObserver(observer);
                isObserverRegistered = true;
                observerCall();
            }else if(adapter == null){
                Log.e("RecommendedFragment", "onCreateView: adapter null quando recommended cocktails/shakes non vuoti");
            }else{
                observerCall();
            }
        }


    }

    private void observerCall() {
        model.getResetRecommended().observe(getViewLifecycleOwner(), resetRecommended -> {
            if(resetRecommended){
                executor.execute(() -> {
                    recommendedCocktailsString = getRecommendedCocktails();
                    allCocktailsString = getAllCocktails();
                    recommendedShakesString = getRecommendedShakes();
                    allShakesString = getAllShakes();

                    handler.post(() -> {
                        int listSize = list.size();
                        recommendedCocktails.clear();
                        allCocktails.clear();
                        recommendedShakes.clear();
                        allShakes.clear();


                        if (isGetCocktailsOk()) {
                            model.setRecommendedCocktails(recommendedCocktailsString);
                            model.setAllCocktails(allCocktailsString);
                            recommendedCocktails = Cocktail.setRecommendedCocktails(recommendedCocktailsString);
                            allCocktails = Cocktail.parseCocktails(allCocktailsString);

                            for (Cocktail cocktail : recommendedCocktails) {
                                for (Cocktail c : allCocktails) {
                                    if (cocktail.getNome().equals(c.getNome())) {
                                        cocktail.setQuantita(c.getQuantita());
                                    }
                                }
                            }

                        }

                        if(isGetShakesOk()){
                            model.setRecommendedShakes(recommendedShakesString);
                            model.setAllShakes(allShakesString);
                            recommendedShakes = Shake.setRecommendedShakes(recommendedShakesString);
                            allShakes = Shake.parseShake(allShakesString);

                            for(Shake shake : recommendedShakes){
                                for(Shake s : allShakes) {
                                    if (shake.getNome().equals(s.getNome())) {
                                        shake.setQuantita(s.getQuantita());
                                    }
                                }
                            }
                        }


                        list.clear();
                        adapter.notifyItemRangeRemoved(0, listSize);

                    });
                });
            }
        });
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

    private void fillRecommended() {
        int addedItem = 0;

        adapter.setCocktailList(recommendedCocktails);
        if(!recommendedCocktails.isEmpty()){
            for(Cocktail c : recommendedCocktails){
                list.add(new RecommendedLayoutClass(c));
                addedItem++;
            }
        }


        adapter.setShakeList(recommendedShakes);
        if(!recommendedShakes.isEmpty()){
            for(Shake s : recommendedShakes){
                list.add(new RecommendedLayoutClass(s));
                addedItem++;
            }
        }

        adapter.notifyItemRangeInserted(0, addedItem);
        model.setResetRecommended(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null && isObserverRegistered) {
            adapter.unregisterAdapterDataObserver(observer);
            isObserverRegistered = false;
        }
    }
}