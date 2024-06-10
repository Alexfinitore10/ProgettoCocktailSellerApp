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
    private static ShakesFragment instance;



//    public ShakesFragment() {
//        // Required empty public constructor
//    }

    private ShakesFragment(){

    }

    public static ShakesFragment getInstance(){
        if(instance == null){
            instance = new ShakesFragment();
        }
        return instance;
    }

//    public static ShakesFragment newInstance() {
//        return new ShakesFragment();
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
                    }else if(adapter == null){
                        Log.e("ShakesFragment", "onCreateView: adapter null quando allShakes vuoto");
                    }
                });
            });
            executor.shutdown();
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
            }else if(adapter == null){
                Log.e("ShakesFragment", "onCreateView: adapter null quando allShakes NON vuoto");
            }
        }

        //observerCall();

        model.getResetShakes().observe(getViewLifecycleOwner(), resetShakes -> {
            Log.i("ShakesFragment", "Sto nell'observer e resetShakes: " + resetShakes);
            if (resetShakes) {
                ExecutorService localExecutor = Executors.newSingleThreadExecutor();
                Handler localHandler = new Handler(Looper.getMainLooper());
                localExecutor.execute(() -> {

                    boolean parsingSuccessful = false;

                    do{
                        Log.d("ShakesFragment", "parsingSuccessful:" +parsingSuccessful);
                        try {
                            allShakes = getAllShakes();
                            Log.d("ShakesFragment", "allShakes:" +allShakes);
                            model.setAllShakes(allShakes);
                            shakes = Shake.parseShake(allShakes);
                            Log.d("ShakesFragment", "cocktails:" +shakes);
                            parsingSuccessful = true;
                        } catch (IOException e) {
                            Log.e("ShakesFragment", "Impossibile recuperare la lista dei frullati: " +e.getMessage());
                        } catch (InterruptedException e){
                            Log.e("ShakesFragment", "Errore esecuzione recupero lista dei frullati: " +e.getMessage());
                        } catch (IndexOutOfBoundsException e) {
                            Log.e("ShakesFragment", "Non ci sono frullati nella lista: " +e.getMessage());
                        } catch (Exception e){
                            Log.e("ShakesFragment", "Errore parsing lista dei frullati: " +e.getMessage());
                        }
                    }while(!parsingSuccessful);
                    localHandler.post(() -> {
                        int listSize = list.size();
                        list.clear();
                        adapter.notifyItemRangeRemoved(0, listSize);
                    });
                });

                localExecutor.shutdown();
            }
            Log.i("ShakesFragment", "observerCall: rimozione observer");
            model.getResetShakes().removeObservers(getViewLifecycleOwner());
        });




    }

//    private void observerCall(){
//        model.getResetShakes().observe(getViewLifecycleOwner(), resetShakes -> {
//            Log.d("ShakesFragment", "observerCall: Sto nell'observer e resetShakes: " + resetShakes);
//            if (resetShakes) {
//                executor = Executors.newSingleThreadExecutor();
//                handler = new Handler(Looper.getMainLooper());
//                executor.execute(() -> {
//
//                    boolean parsingSuccessful = false;
//
//                    do{
//                        Log.d("ShakesFragment", "parsingSuccessful:" +parsingSuccessful);
//                        try {
//                            allShakes = getAllShakes();
//                            Log.d("ShakesFragment", "allShakes:" +allShakes);
//                            model.setAllShakes(allShakes);
//                            shakes = Shake.parseShake(allShakes);
//                            Log.d("ShakesFragment", "cocktails:" +shakes);
//                            parsingSuccessful = true;
//                        } catch (IOException e) {
//                            Log.e("ShakesFragment", "Impossibile recuperare la lista dei frullati: " +e.getMessage());
//                        } catch (InterruptedException e){
//                            Log.e("ShakesFragment", "Errore esecuzione recupero lista dei frullati: " +e.getMessage());
//                        } catch (IndexOutOfBoundsException e) {
//                            Log.e("ShakesFragment", "Non ci sono frullati nella lista: " +e.getMessage());
//                        } catch (Exception e){
//                            Log.e("ShakesFragment", "Errore parsing lista dei frullati: " +e.getMessage());
//                        }
//                    }while(!parsingSuccessful);
//                    handler.post(() -> {
//                        int listSize = list.size();
//                        list.clear();
//                        adapter.notifyItemRangeRemoved(0, listSize);
//                    });
//                });
//
//                executor.shutdown();
//            }
//            model.getResetShakes().removeObservers(getViewLifecycleOwner());
//        });
//    }

    private String getAllShakes() throws IOException, InterruptedException{
        Log.i("ShakesFragment", "getAllShakes");
        String command = "4";
        client.sendData(command);
        client.setSocketTimeout(5000);
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
    public void onResume() {
        super.onResume();
        if (adapter != null && !isObserverRegistered) {
            adapter.registerAdapterDataObserver(observer);
            isObserverRegistered = true;
        }else{
            Log.e("ShakesFragment", "onResume: adapter null");
        }
        //observerCall();

        model.getResetShakes().observe(getViewLifecycleOwner(), resetShakes -> {
            Log.i("ShakesFragment", "Sto nell'observer e resetShakes: " + resetShakes);
            if (resetShakes) {
                ExecutorService localExecutor = Executors.newSingleThreadExecutor();
                Handler localHandler = new Handler(Looper.getMainLooper());
                localExecutor.execute(() -> {

                    boolean parsingSuccessful = false;

                    do{
                        Log.d("ShakesFragment", "parsingSuccessful:" +parsingSuccessful);
                        try {
                            allShakes = getAllShakes();
                            Log.d("ShakesFragment", "allShakes:" +allShakes);
                            model.setAllShakes(allShakes);
                            shakes = Shake.parseShake(allShakes);
                            Log.d("ShakesFragment", "cocktails:" +shakes);
                            parsingSuccessful = true;
                        } catch (IOException e) {
                            Log.e("ShakesFragment", "Impossibile recuperare la lista dei frullati: " +e.getMessage());
                        } catch (InterruptedException e){
                            Log.e("ShakesFragment", "Errore esecuzione recupero lista dei frullati: " +e.getMessage());
                        } catch (IndexOutOfBoundsException e) {
                            Log.e("ShakesFragment", "Non ci sono frullati nella lista: " +e.getMessage());
                        } catch (Exception e){
                            Log.e("ShakesFragment", "Errore parsing lista dei frullati: " +e.getMessage());
                        }
                    }while(!parsingSuccessful);
                    localHandler.post(() -> {
                        int listSize = list.size();
                        list.clear();
                        adapter.notifyItemRangeRemoved(0, listSize);
                    });
                });

                localExecutor.shutdown();
            }
            Log.i("ShakesFragment", "observerCall: rimozione observer");
            model.getResetShakes().removeObservers(getViewLifecycleOwner());
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null && isObserverRegistered) {
            adapter.unregisterAdapterDataObserver(observer);
            isObserverRegistered = false;
        }else if(adapter == null){
            Log.e("ShakesFragment", "onResume: adapter null quando recommended cocktails/shakes vuoto e isObserverRegistered: "+isObserverRegistered);
        }
//        Log.d("ShakesFragment", "observerCall: sto rimovendo l'observer");
//        model.getResetShakes().removeObservers(getViewLifecycleOwner());
    }

}