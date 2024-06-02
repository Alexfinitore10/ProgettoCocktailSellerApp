package com.example.cocktailapp;




import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import java.util.concurrent.atomic.AtomicBoolean;


public class ShakesFragment extends Fragment {
    private ShakesRecyclerViewAdapter adapter;
    private ArrayList<ShakesLayoutClass> list;
    private RecyclerView recyclerView;
    private Client client;
    private ArrayList<Shake> shakes;
    private String allShakes;
    private Carrello carrello;
    private CartObserver model;
    private ExecutorService executor;
    private Handler handler;




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

        model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
        list = new ArrayList<>();
        shakes = new ArrayList<>();
        recyclerView = view.findViewById(R.id.ShakesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(model.getAllShakes() != null){
            allShakes = model.getAllShakes();
        }else{
            allShakes = "";
        }

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        if(allShakes.isEmpty()){
            executor.execute(() -> {
                allShakes = getAllShakes();
                model.setAllShakes(allShakes);
                handler.post(() -> {
                    shakes = (ArrayList<Shake>) Shake.setShakes(allShakes);
                    for (Shake s : shakes) {
                        list.add(new ShakesLayoutClass(s.getNome(),s.getIngredienti(),s.getPrezzo(),s.getQuantita()));
                    }

                    adapter = new ShakesRecyclerViewAdapter(list,getContext(),shakes,model);
                    recyclerView.setAdapter(adapter);
                });
            });
        }else{
            shakes = (ArrayList<Shake>) Shake.setShakes(allShakes);
            for (Shake s : shakes) {
                list.add(new ShakesLayoutClass(s.getNome(),s.getIngredienti(),s.getPrezzo(),s.getQuantita()));
            }


            adapter = new ShakesRecyclerViewAdapter(list,getContext(),shakes,model);
            recyclerView.setAdapter(adapter);
        }



        model.getResetShakes().observe(getViewLifecycleOwner(), resetShakes -> {
            Log.d("ShakesFragment", "resetShakes: " + resetShakes);
            if (resetShakes) {
                executor.execute(() -> {
                    allShakes = ShakesFragment.this.getAllShakes();
                    model.setAllShakes(allShakes);
                    handler.post(() -> {
                        shakes.clear();
                        shakes = (ArrayList<Shake>) Shake.setShakes(allShakes);
                        list.clear();
                        for (int i = 0; i < shakes.size(); i++) {
                            list.add(new ShakesLayoutClass(shakes.get(i).getNome(), shakes.get(i).getIngredienti(), shakes.get(i).getPrezzo(), shakes.get(i).getQuantita()));
                        }
                        adapter.notifyDataSetChanged();
                        model.setResetShakes(false);
                    });
                });

            }
        });



    }

    private String getAllShakes() {
        String command = "4";
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