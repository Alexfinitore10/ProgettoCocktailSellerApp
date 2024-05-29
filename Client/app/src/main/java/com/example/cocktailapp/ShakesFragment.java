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
        carrello = Carrello.getInstance();
        client = Client.getIstance();
        
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

        CartObserver model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
        list = new ArrayList<>();
        shakes = new ArrayList<>();


    
        Runnable getShakesTask = () -> allShakes = getAllShakes();
        Thread getShakesThread = new Thread(getShakesTask);
        getShakesThread.start();

        try{
            getShakesThread.join();
        }catch(InterruptedException e){
            Log.e("onViewCreated ShakesFragment","Errore nella join del thread: " + e.getMessage());
        }

        shakes = (ArrayList<Shake>) Shake.setShakes(allShakes);
        recyclerView = view.findViewById(R.id.ShakesRecyclerView);


        for(Shake s : shakes){
            list.add(new ShakesLayoutClass(s.getNome(),s.getIngredienti(),s.getPrezzo(),s.getQuantita()));
        }
    


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShakesRecyclerViewAdapter(list,getContext(),shakes,model);
        recyclerView.setAdapter(adapter);

        model.getPaymentSuccess().observe(getViewLifecycleOwner(), paymentMade -> {
            Log.d("onViewCreated ShakesFragment","paymentMade: " + paymentMade);
             if (paymentMade) {
                 Runnable getAgainShakesTask = () -> allShakes = getAllShakes();
                 Thread getAgainShakesThread = new Thread(getAgainShakesTask);
                 getAgainShakesThread.start();
                 try{
                     getAgainShakesThread.join();
                 }catch(InterruptedException e){
                     Log.e("onViewCreated ShakesFragment","Errore nella join del thread: " + e.getMessage());
                 }
                 shakes.clear();
                 shakes = (ArrayList<Shake>) Shake.setShakes(allShakes);
                 list.clear();
                 for (Shake s : shakes) {
                     list.add(new ShakesLayoutClass(s.getNome(),s.getIngredienti(),s.getPrezzo(),s.getQuantita()));
                 }
                 adapter.notifyDataSetChanged();
                 model.setPaymentSuccess(false);
             }

        });



    }




    private String getAllShakes() {
        String command = "4";
        client.sendData(command);
        client.setSocketTimeout(3000);
        return client.bufferedReceive();
    }


}