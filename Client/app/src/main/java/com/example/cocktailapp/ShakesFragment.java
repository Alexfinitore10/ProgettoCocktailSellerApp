package com.example.cocktailapp;




import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ShakesFragment extends Fragment {
    private ShakesRecyclerViewAdapter adapter;
    private ArrayList<ShakesLayoutClass> list;
    private RecyclerView recyclerView;
    private Client client;
    private ArrayList<Shake> shakes;
    private String allShakes;
    private CartObserver model;
    private ExecutorService executor;
    private Handler handler;
    private RecyclerView.AdapterDataObserver observer;
    private boolean isObserverRegistered = false;


    public ShakesFragment() {
        // Required empty public constructor
    }


    public static ShakesFragment newInstance() {
        return new ShakesFragment();
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
        

        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                recyclerView.post(() -> fillShakes(itemCount));
            }
        };

        if(model.getAllShakes() != null){
            allShakes = model.getAllShakes();
        }else{
            allShakes = "";
        }

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        if(allShakes.isEmpty()){
            executor.execute(() -> {
                try {
                    allShakes = getAllShakes();
                } catch (IOException e) {
                    Log.e("ShakesFragment", "Impossibile recuperare la lista dei frullati: " +e.getMessage());
                    allShakes = "";
                } catch (InterruptedException e){
                    Log.e("ShakesFragment", "Errore esecuzione recupero lista dei frullati: " +e.getMessage());
                    allShakes = "";
                } catch(Exception e){
                    Log.e( "ShakesFragment", "Errore generico riempimento lista dei frullati: " +e.getMessage());
                    allShakes = "";
                }
                model.setAllShakes(allShakes);
                handler.post(() -> {
                    try {
                        shakes = Shake.parseShake(allShakes);
                    } catch (IndexOutOfBoundsException e) {
                        Log.e( "ShakesFragment", "Non ci sono frullati nella lista: " +e.getMessage());
                        shakes = new ArrayList<>();
                    } catch(Exception e){
                        Log.e( "ShakesFragment", "Errore parsing lista dei frullati: " +e.getMessage());
                        shakes = new ArrayList<>();
                    }
                    for (Shake s : shakes) {
                        list.add(new ShakesLayoutClass(s.getNome(),s.getIngredienti(),s.getPrezzo(),s.getQuantita()));
                    }
                    adapter = new ShakesRecyclerViewAdapter(list,getContext(),shakes,model);
                    recyclerView.setAdapter(adapter);
                    if(adapter != null && !isObserverRegistered) {
                        adapter.registerAdapterDataObserver(observer);
                        isObserverRegistered = true;
                        observerCall();
                    }else if(adapter == null){
                        Log.e("ShakesFragment", "onCreateView: adapter null quando allShakes vuoto");
                    }else {
                        observerCall();
                    }
                });
            });
        }else{
            try {
                shakes = Shake.parseShake(allShakes);
            } catch (IndexOutOfBoundsException e) {
                Log.e( "ShakesFragment", "Non ci sono frullati nella lista: " +e.getMessage());
                shakes = new ArrayList<>();
            } catch(Exception e){
                Log.e( "ShakesFragment", "Errore parsing lista dei frullati: " +e.getMessage());
                shakes = new ArrayList<>();
            }
            for (Shake s : shakes) {
                list.add(new ShakesLayoutClass(s.getNome(),s.getIngredienti(),s.getPrezzo(),s.getQuantita()));
            }

            adapter = new ShakesRecyclerViewAdapter(list,getContext(),shakes,model);
            recyclerView.setAdapter(adapter);
            if(adapter != null && !isObserverRegistered) {
                adapter.registerAdapterDataObserver(observer);
                isObserverRegistered = true;
                observerCall();
            }else if(adapter == null){
                Log.e("ShakesFragment", "onCreateView: adapter null quando allShakes NON vuoto");
            }else{
                observerCall();
            }
        }






    }

    private void observerCall(){
        model.getResetShakes().observe(getViewLifecycleOwner(), resetShakes -> {
            Log.d("ShakesFragment", "resetShakes: " + resetShakes);
            if (resetShakes) {
                executor.execute(() -> {
                    try {
                        allShakes = getAllShakes();
                    } catch (IOException e) {
                        Log.e("ShakesFragment", "Impossibile recuperare la lista dei frullati: " +e.getMessage());
                        allShakes = "";
                    } catch (InterruptedException e){
                        Log.e("ShakesFragment", "Errore esecuzione recupero lista dei frullati: " +e.getMessage());
                        allShakes = "";
                    } catch(Exception e){
                        Log.e( "ShakesFragment", "Errore generico riempimento lista dei frullati: " +e.getMessage());
                        allShakes = "";
                    }
                    model.setAllShakes(allShakes);
                    handler.post(() -> {
                        shakes.clear();
                        try {
                            shakes = Shake.parseShake(allShakes);
                        } catch (IndexOutOfBoundsException e) {
                            Log.e("ShakesFragment", "Non ci sono frullati nella lista: " +e.getMessage());
                            shakes = new ArrayList<>();
                        }catch(Exception e){
                            Log.e("ShakesFragment", "Errore parsing lista dei frullati: " +e.getMessage());
                            shakes = new ArrayList<>();
                        }
                        int listSize = list.size();
                        list.clear();
                        adapter.notifyItemRangeRemoved(0, listSize);
                    });
                });

            }
        });
    }

    private String getAllShakes() throws IOException, InterruptedException{
        String command = "4";
        client.sendData(command);
        client.setSocketTimeout(3000);
        return client.bufferedReceive();
    }

    public void fillShakes(int listSize){
        adapter.setShakeslist(shakes);
        for (Shake s: shakes) {
            list.add(new ShakesLayoutClass(s.getNome(), s.getIngredienti(), s.getPrezzo(), s.getQuantita()));
        }
        adapter.notifyItemRangeInserted(0, listSize);
        model.setResetShakes(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null && isObserverRegistered) {
            adapter.unregisterAdapterDataObserver(observer);
            isObserverRegistered = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && !isObserverRegistered) {
            adapter.registerAdapterDataObserver(observer);
            isObserverRegistered = true;
        }
    }

}