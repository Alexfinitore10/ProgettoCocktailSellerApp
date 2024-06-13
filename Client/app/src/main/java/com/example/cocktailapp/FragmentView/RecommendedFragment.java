package com.example.cocktailapp.FragmentView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
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

import com.example.cocktailapp.Model.CartObserver;
import com.example.cocktailapp.Model.Client;
import com.example.cocktailapp.Model.Cocktail;
import com.example.cocktailapp.Model.Shake;
import com.example.cocktailapp.R;
import com.example.cocktailapp.Adapter.RecommendedLayoutClass;
import com.example.cocktailapp.Adapter.RecommendedRecyclerViewAdapter;

import java.io.IOException;
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
    private static RecommendedFragment instance;


//    public RecommendedFragment() {
//        // Required empty public constructor
//    }

    private RecommendedFragment() {

    }

    public static RecommendedFragment getInstance() {
        if (instance == null) {
            instance = new RecommendedFragment();
        }
        return instance;
    }


//    public static RecommendedFragment newInstance() {
//        return new RecommendedFragment();
//    }

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
        model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
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
                boolean parsingCocktailsSuccessful = false;
                boolean parsingShakesSuccessful = false;
                recommendedCocktailsString = getRecommendedCocktails();
                do {
                    try {
                        Log.i("RecommendedFragment","getAllCocktails r:132");
                        allCocktailsString = getAllCocktails();
                        model.setAllCocktails(allCocktailsString);
                        allCocktails = Cocktail.parseCocktails(allCocktailsString);
                        parsingCocktailsSuccessful = true;
                    } catch (IOException e) {
                        Log.e("RecommendedFragment", "Impossibile recuperare la lista dei cocktail: " +e.getMessage());
                        allCocktailsString = "";
                    } catch (InterruptedException e) {
                        Log.e("RecommendedFragment", "Errore esecuzione recupero lista dei cocktail: " +e.getMessage());
                        allCocktailsString = "";
                    }catch (Exception e){
                        Log.e("RecommendedFragment", "Errore generico riempimento lista dei cocktail: " +e.getMessage());
                        allCocktailsString = "";
                    }
                } while (!parsingCocktailsSuccessful);



                recommendedShakesString = getRecommendedShakes();

                do {
                    try {
                        Log.i("RecommendedFragment","getAllShakes r:160");
                        allShakesString = getAllShakes();
                        model.setAllShakes(allShakesString);
                        allShakes = Shake.parseShakes(allShakesString);
                        parsingShakesSuccessful = true;
                    }catch (IOException e){
                        Log.e("RecommendedFragment", "Impossibile recuperare la lista dei frullati: " +e.getMessage());
                        allShakesString = "";
                    }catch (InterruptedException e){
                        Log.e("RecommendedFragment", "Errore esecuzione recupero lista dei frullati: " +e.getMessage());
                        allShakesString = "";
                    }catch (Exception e){
                        Log.e("RecommendedFragment", "Errore generico riempimento lista dei frullati: " +e.getMessage());
                        allShakesString = "";
                    }
                } while (!parsingShakesSuccessful);


                handler.post(() -> {
                    if (isGetCocktailsOk()) {
                        model.setRecommendedCocktails(recommendedCocktailsString);
                        model.setAllCocktails(allCocktailsString);
                        recommendedCocktails = Cocktail.setRecommendedCocktails(recommendedCocktailsString);
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
                        getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), lifecycleOwner -> {
                            if (lifecycleOwner != null) {
                                observerCall(lifecycleOwner);
                            }
                        });
                    }else if(adapter == null){
                        Log.e("RecommendedFragment", "onCreateView: adapter null quando recommended cocktails/shakes vuoto");
                    }

                });
            });
            executor.shutdown();
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
                allShakes = Shake.parseShakes(allShakesString);

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
                getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), lifecycleOwner -> {
                    if (lifecycleOwner != null) {
                        observerCall(lifecycleOwner);
                    }
                });
            }else if(adapter == null){
                Log.e("RecommendedFragment", "onCreateView: adapter null quando recommended cocktails/shakes non vuoti");
            }
        }





    }

    private void observerCall(LifecycleOwner lifecycleOwner){
        model.getResetRecommended().observe(lifecycleOwner, resetRecommended -> {
            Log.d("RecommendedFragment", "observerCall: Sto nell'observer e resetRecommended e' " +resetRecommended);
            if(resetRecommended){
                ExecutorService localExecutor = Executors.newSingleThreadExecutor();
                Handler localHandler = new Handler(Looper.getMainLooper());
                localExecutor.execute(() -> {
                    boolean parsingCocktailsSuccessful = false;
                    boolean parsingShakesSuccessful = false;
                    recommendedCocktailsString = getRecommendedCocktails();
                    do {
                        try {
                            allCocktailsString = getAllCocktails();
                            model.setAllCocktails(allCocktailsString);
                            allCocktails = Cocktail.parseCocktails(allCocktailsString);
                            parsingCocktailsSuccessful = true;
                        } catch (IOException e) {
                            Log.e("RecommendedFragment", "Impossibile recuperare la lista dei cocktail: " +e.getMessage());
                            allCocktailsString = "";
                        } catch (InterruptedException e) {
                            Log.e("RecommendedFragment", "Errore esecuzione recupero lista dei cocktail: " +e.getMessage());
                            allCocktailsString = "";
                        }catch (Exception e){
                            Log.e("RecommendedFragment", "Errore generico riempimento lista dei cocktail: " +e.getMessage());
                            allCocktailsString = "";
                        }
                    } while (!parsingCocktailsSuccessful);

                    recommendedShakesString = getRecommendedShakes();
                    do {
                        try {
                            allShakesString = getAllShakes();
                            model.setAllShakes(allShakesString);
                            allShakes = Shake.parseShakes(allShakesString);
                            parsingShakesSuccessful = true;
                        }catch (IOException e){
                            Log.e("RecommendedFragment", "Impossibile recuperare la lista dei frullati: " +e.getMessage());
                            allShakesString = "";
                        }catch (InterruptedException e){
                            Log.e("RecommendedFragment", "Errore esecuzione recupero lista dei frullati: " +e.getMessage());
                            allShakesString = "";
                        }catch (Exception e){
                            Log.e("RecommendedFragment", "Errore generico riempimento lista dei frullati: " +e.getMessage());
                            allShakesString = "";
                        }
                    } while (!parsingShakesSuccessful);


                    localHandler.post(() -> {
                        int listSize = list.size();
                        recommendedCocktails.clear();
                        recommendedShakes.clear();


                        if (isGetCocktailsOk()) {
                            recommendedCocktails = Cocktail.setRecommendedCocktails(recommendedCocktailsString);

                            for (Cocktail cocktail : recommendedCocktails) {
                                for (Cocktail c : allCocktails) {
                                    if (cocktail.getNome().equals(c.getNome())) {
                                        cocktail.setQuantita(c.getQuantita());
                                    }
                                }
                            }

                        }

                        if(isGetShakesOk()){
                            recommendedShakes = Shake.setRecommendedShakes(recommendedShakesString);

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
                localExecutor.shutdown();
            }
            model.getResetRecommended().removeObservers(lifecycleOwner);
        });
    }

    private String getRecommendedCocktails(){
        Log.i("RecommendedFragment", "getRecommendedCocktails ");
        String command = "9";
        client.sendData(command);
        client.setSocketTimeout(5000);
        return client.bufferedReceive();
    }

    private String getAllCocktails() throws IOException, InterruptedException{
        Log.i("RecommendedFragment", "getAllCocktails ");
        String command = "3";
        client.sendData(command);
        client.setSocketTimeout(5000);
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
        Log.i("RecommendedFragment", "getRecommendedShakes ");
        String command = "10";
        client.sendData(command);
        client.setSocketTimeout(5000);
        return client.bufferedReceive();
    }
    private String getAllShakes() throws IOException, InterruptedException{
        Log.i("RecommendedFragment", "getAllShakes ");
        String command = "4";
        client.sendData(command);
        client.setSocketTimeout(5000);
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
        Log.d("RecommendedFragment", "fillRecommended: sto riempiendo la lista");
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
    public void onResume() {
        super.onResume();

        if (adapter != null && !isObserverRegistered) {
            Log.e("RecommendedFragment", "onResume: adapter not null");
            adapter.registerAdapterDataObserver(observer);
            isObserverRegistered = true;
            getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), lifecycleOwner -> {
                if (lifecycleOwner != null) {
                    observerCall(lifecycleOwner);
                }
            });
        }else if(adapter == null){
            Log.e("RecommendedFragment", "onResume: adapter null quando recommended cocktails/shakes vuoto e isObserverRegistered: "+isObserverRegistered);
        }



    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null && isObserverRegistered) {
            adapter.unregisterAdapterDataObserver(observer);
            isObserverRegistered = false;
        }else if(adapter == null){
            Log.e("RecommendedFragment", "onResume: adapter null quando recommended cocktails/shakes vuoto e isObserverRegistered: "+isObserverRegistered);
        }

    }

}