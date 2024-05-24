package com.example.cocktailapp;


import android.content.Context;
import android.content.SharedPreferences;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CartRecyclerViewAdapter adapter;
    private ArrayList<CartLayoutClass> list;
    private RecyclerView recyclerView;
    private Carrello carrello;
    private ArrayList<Cocktail> cocktailList;
    private ArrayList<Shake> shakeList;
    private Client client;
    private String allCocktails;
    private String allShakes;

    public CartFragment() {
        // Required empty public constructor
    }


    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
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
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        carrello = Carrello.getInstance();
        client = Client.getIstanza();
        cocktailList = new ArrayList<>();
        shakeList = new ArrayList<>();


        Runnable getCocktailsTask = () -> allCocktails = getAllCocktails(client);
        Thread getCocktailsThread = new Thread(getCocktailsTask);
        getCocktailsThread.start();
        Runnable getShakesTask = () -> allShakes = getAllShakes(client);
        Thread getShakesThread = new Thread(getShakesTask);
        getShakesThread.start();

        try {
            getCocktailsThread.join();
        } catch (InterruptedException e) {
            Log.e("onViewCreated CartFragment","Errore nella join del thread getCocktails: " + e.getMessage());
        }

        try{
            getShakesThread.join();
        }catch(InterruptedException e){
            Log.e("onViewCreated CartFragment","Errore nella join del thread getShakes: " + e.getMessage());
        }

        for (String c : allCocktails.split("\\n")) {
            cocktailList.add(Cocktail.parseString(c));
        }

        for (String s : allShakes.split("\\n")) {
            shakeList.add(Shake.parseString(s));
        }



        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.CartRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartRecyclerViewAdapter(list,getContext(),cocktailList,shakeList);
        recyclerView.setAdapter(adapter);

        CartLayoutItemTransfer model = new ViewModelProvider(requireActivity()).get(CartLayoutItemTransfer.class);

        model.getToAddItems().observe(getViewLifecycleOwner(), queue -> {
            while(!queue.isEmpty()){
                CartLayoutClass item = queue.poll();
                list.add(item);
                adapter.notifyItemInserted(list.size() - 1);
            }
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
        });



    }
    private String getAllCocktails(Client client){
        String command = "3";
        client.sendData(command);
        return client.bufferedReceive();
    }

    private String getAllShakes(Client client) {
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



}