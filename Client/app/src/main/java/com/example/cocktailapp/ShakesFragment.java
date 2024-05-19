package com.example.cocktailapp;




import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class ShakesFragment extends Fragment {
    private ShakesRecyclerViewAdapter adapter;
    private ArrayList<ShakesLayoutClass> list;
    private RecyclerView recyclerView;
    private Client client;
    private ArrayList<Shake> shakes;
    private String allShakes;
    private Carrello carrello;



    public ShakesFragment() {
        // Required empty public constructor
    }


    public static ShakesFragment newInstance() {
        ShakesFragment fragment = new ShakesFragment();
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
        return inflater.inflate(R.layout.fragment_shakes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        carrello = Carrello.getInstance();
        client = Client.getIstanza();
        list = new ArrayList<>();
        shakes = new ArrayList<>();



        Runnable getShakesTask = () -> allShakes = getAllShakes(client);
        Thread getShakesThread = new Thread(getShakesTask);
        getShakesThread.start();

        try{
            getShakesThread.join();
        }catch(InterruptedException e){
            Log.e("onViewCreated ShakesFragment","Errore nella join del thread: " + e.getMessage());
        }

        recyclerView = view.findViewById(R.id.ShakesRecyclerView);

        for (String s : allShakes.split("\\n")) {
            shakes.add(Shake.parseString(s));
        }

        for(Shake s : shakes){
            list.add(new ShakesLayoutClass(s.getNome(),s.getIngredienti(),s.getPrezzo(),s.getQuantita()));
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShakesRecyclerViewAdapter(list,getContext(),shakes);
        recyclerView.setAdapter(adapter);
    }




    private String getAllShakes(Client client) {
        String command = "4";
        client.sendData(command);
        return client.bufferedReceive();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Ottieni l'istanza di SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("Coming Shake Flag", Context.MODE_PRIVATE);

        // Controlla se stai tornando da CartFragment
        boolean comingFromCartFragment = prefs.getBoolean("comingFromCartFragment", false);

        if (comingFromCartFragment) {
            carrello.setLastSize(carrello.getBeverages().size());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("comingFromCartFragment", false);
            editor.apply();
        }
    }

}