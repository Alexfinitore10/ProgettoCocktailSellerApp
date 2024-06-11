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

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CartFragment extends Fragment {
    private CartRecyclerViewAdapter adapter;
    private ArrayList<CartLayoutClass> list;
    private RecyclerView recyclerView;
    private Carrello carrello;
    private ArrayList<Cocktail> cocktailsList;
    private ArrayList<Shake> shakesList;
    private Client client;
    private String allCocktails,allShakes;
    private CartObserver model;
    private static CartFragment instance;

//    public CartFragment() {
//        // Required empty public constructor
//    }

    private CartFragment() {

    }
    public static CartFragment getInstance() {
        if (instance == null) {
            instance = new CartFragment();
        }
        return instance;
    }


//    public static CartFragment newInstance() {
//        return new CartFragment();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = Client.getIstance();
        carrello = Carrello.getInstance();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
        list = new ArrayList<>();
        cocktailsList = new ArrayList<>();
        shakesList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.CartRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(model.getAllCocktails() != null){
            allCocktails = model.getAllCocktails();
        }else{
            allCocktails = "";
        }
        if(model.getAllShakes() != null){
            allShakes = model.getAllShakes();
        }else{
            allShakes = "";
        }



        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        if(allCocktails.isEmpty() || allShakes.isEmpty()){
            executor.execute(() -> {
                boolean parsingCocktailSuccessful = false;
                boolean parsingShakesSuccessful = false;

                do {
                    try {
                        allCocktails = getAllCocktails();
                        model.setAllCocktails(allCocktails);
                        cocktailsList = Cocktail.parseCocktails(allCocktails);
                        parsingCocktailSuccessful = true;
                    } catch (IOException e) {
                        Log.e("CartFragment", "Impossibile recuperare la lista dei cocktail: " +e.getMessage());
                    } catch (InterruptedException e){
                        Log.e("CartFragment", "Errore esecuzione recupero lista dei cocktail: " +e.getMessage());
                    } catch (IndexOutOfBoundsException e) {
                        Log.e("CartFragment", "Non ci sono cocktails nella lista: " +e.getMessage());
                    } catch (Exception e){
                        Log.e("CartFragment", "Errore parsing lista dei cocktail: " +e.getMessage());
                    }
                } while (!parsingCocktailSuccessful);

                do{
                    Log.d("ShakesFragment", "parsingSuccessful:" +parsingShakesSuccessful);
                    try {
                        allShakes = getAllShakes();
                        Log.d("CartFragment", "allShakes:" +allShakes);
                        model.setAllShakes(allShakes);
                        shakesList = Shake.parseShakes(allShakes);
                        Log.d("CartFragment", "shakesList:" +shakesList);
                        parsingShakesSuccessful = true;
                    } catch (IOException e) {
                        Log.e("CartFragment", "Impossibile recuperare la lista dei frullati: " +e.getMessage());
                    } catch (InterruptedException e){
                        Log.e("CartFragment", "Errore esecuzione recupero lista dei frullati: " +e.getMessage());
                    } catch (IndexOutOfBoundsException e) {
                        Log.e("CartFragment", "Non ci sono frullati nella lista: " +e.getMessage());
                    } catch (Exception e){
                        Log.e("CartFragment", "Errore parsing lista dei frullati: " +e.getMessage());
                    }
                }while(!parsingShakesSuccessful);

                handler.post(() -> {
                    adapter = new CartRecyclerViewAdapter(list,getContext(), cocktailsList, shakesList,model);
                    recyclerView.setAdapter(adapter);
                });
            });
        }else {
            cocktailsList = Cocktail.parseCocktails(allCocktails);
            shakesList = Shake.parseShakes(allShakes);
            adapter = new CartRecyclerViewAdapter(list, getContext(), cocktailsList, shakesList, model);
            recyclerView.setAdapter(adapter);

        }

        executor.shutdown();

        model.getToAddItems().observe(getViewLifecycleOwner(), queue -> {
            while(!queue.isEmpty()){
                CartLayoutClass item = queue.poll();
                list.add(item);
                adapter.notifyItemInserted(list.size() - 1);
            }
            model.getToAddItems().removeObservers(getViewLifecycleOwner());
        });

        model.getToUpdateItem().observe(getViewLifecycleOwner(), queue -> {
            while (!queue.isEmpty()) {
                CartLayoutClass item = queue.poll();
                int index = getElementIndex(item);

                if(index != -1){
                    list.set(index,item);
                    adapter.notifyItemChanged(index);
                }else{
                    Log.e("CartFragment", "Item not found in list");
                }
            }
            model.getToUpdateItem().removeObservers(getViewLifecycleOwner());
        });

        model.getResetCart().observe(getViewLifecycleOwner(), resetCart -> {
            Log.d("CartFragment", "resetCart: " + resetCart);
            if (resetCart) {
                int listSize = list.size();
                list.clear();
                adapter.notifyItemRangeRemoved(0, listSize);
                model.setResetCart(false);
            }
            model.getResetCart().removeObservers(getViewLifecycleOwner());
        });

        model.getIsLoggedIn().observe(getViewLifecycleOwner(), loggedIn -> {
            Log.v("CartFragment", "getIsLoggedIn");
            if (!loggedIn && !list.isEmpty()) {
                int listSize = list.size();
                list.clear();
                adapter.notifyItemRangeRemoved(0, listSize);
            }
            Log.v("CartFragment", "unregistering getIsLoggedIn");
            model.getIsLoggedIn().removeObservers(getViewLifecycleOwner());
        });
        

    }

//    public void observerCall(){
//        model.getToAddItems().observe(getViewLifecycleOwner(), queue -> {
//            while(!queue.isEmpty()){
//                CartLayoutClass item = queue.poll();
//                list.add(item);
//                adapter.notifyItemInserted(list.size() - 1);
//            }
//        });
//
//        model.getToUpdateItem().observe(getViewLifecycleOwner(), queue -> {
//            while (!queue.isEmpty()) {
//                CartLayoutClass item = queue.poll();
//                int index = getElementIndex(item);
//
//                if(index != -1){
//                    list.set(index,item);
//                    adapter.notifyItemChanged(index);
//                }else{
//                    Log.e("CartFragment", "Item not found in list");
//                }
//            }
//        });
//
//        model.getResetCart().observe(getViewLifecycleOwner(), resetCart -> {
//            Log.d("CartFragment", "resetCart: " + resetCart);
//            if (resetCart) {
//                int listSize = list.size();
//                list.clear();
//                adapter.notifyItemRangeRemoved(0, listSize);
//                model.setResetCart(false);
//            }
//        });
//
//        model.getIsLoggedIn().observe(getViewLifecycleOwner(), loggedIn -> {
//            if (!loggedIn) {
//                int listSize = list.size();
//                list.clear();
//                adapter.notifyItemRangeRemoved(0, listSize);
//            }
//        });
//    }
    private String getAllCocktails() throws IOException, InterruptedException {
        String command = "3";
        client.sendData(command);
        return client.bufferedReceive();
    }

    private String getAllShakes() throws IOException, InterruptedException {
        String command = "4";
        client.sendData(command);
        return client.bufferedReceive();
    }

    private int getElementIndex(CartLayoutClass item){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getBevanda().getNome().equals(item.getBevanda().getNome())){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("CartFragment", "onResume");

        model.getToAddItems().observe(getViewLifecycleOwner(), queue -> {
            Log.v("CartFragment", "getToAddItems");
            while(!queue.isEmpty()){
                CartLayoutClass item = queue.poll();
                list.add(item);
                adapter.notifyItemInserted(list.size() - 1);
            }
            Log.v("CartFragment", "unregistering getToAddItems");
            model.getToAddItems().removeObservers(getViewLifecycleOwner());
        });

        model.getToUpdateItem().observe(getViewLifecycleOwner(), queue -> {
            Log.v("CartFragment", "getToUpdateItem");
            while (!queue.isEmpty()) {
                CartLayoutClass item = queue.poll();
                int index = getElementIndex(item);

                if(index != -1){
                    list.set(index,item);
                    adapter.notifyItemChanged(index);
                }else{
                    Log.e("CartFragment", "Item not found in list");
                }
            }
            Log.v( "CartFragment", "unregistering getToUpdateItem");
            model.getToUpdateItem().removeObservers(getViewLifecycleOwner());
        });

        model.getResetCart().observe(getViewLifecycleOwner(), resetCart -> {
            Log.v("CartFragment", "resetCart: " + resetCart);
            if (resetCart) {
                int listSize = list.size();
                list.clear();
                adapter.notifyItemRangeRemoved(0, listSize);
                model.setResetCart(false);
            }
            Log.v("CartFragment", "unregistering getResetCart");
            model.getResetCart().removeObservers(getViewLifecycleOwner());
        });

        model.getIsLoggedIn().observe(getViewLifecycleOwner(), loggedIn -> {
            Log.v("CartFragment", "getIsLoggedIn");
            if (!loggedIn && !list.isEmpty()) {
                int listSize = list.size();
                list.clear();
                adapter.notifyItemRangeRemoved(0, listSize);
            }
            Log.v("CartFragment", "unregistering getIsLoggedIn");
            model.getIsLoggedIn().removeObservers(getViewLifecycleOwner());
        });

    }
}