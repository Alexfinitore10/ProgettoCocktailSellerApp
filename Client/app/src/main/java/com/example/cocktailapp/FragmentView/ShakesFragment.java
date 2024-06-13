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

import com.example.cocktailapp.Model.CartObserver;
import com.example.cocktailapp.Model.Client;
import com.example.cocktailapp.Model.Shake;
import com.example.cocktailapp.R;
import com.example.cocktailapp.Adapter.ShakesLayoutClass;
import com.example.cocktailapp.Adapter.ShakesRecyclerViewAdapter;

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
                boolean parsingSuccessful = false;

                do{
                    Log.d("ShakesFragment", "parsingSuccessful:" +parsingSuccessful);
                    try {
                        allShakes = getAllShakes();
                        Log.d("ShakesFragment", "allShakes:" +allShakes);
                        model.setAllShakes(allShakes);
                        shakes = Shake.parseShakes(allShakes);
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

                handler.post(() -> {

                    for (Shake s : shakes) {
                        list.add(new ShakesLayoutClass(s.getNome(),s.getIngredienti(),s.getPrezzo(),s.getQuantita()));
                    }
                    adapter = new ShakesRecyclerViewAdapter(list,getContext(),shakes,model);
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
                        Log.e("ShakesFragment", "onCreateView: adapter null quando allShakes vuoto");
                    }
                });
            });
            executor.shutdown();
        }else{
            try {
                shakes = Shake.parseShakes(allShakes);
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
                getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), lifecycleOwner -> {
                    if (lifecycleOwner != null) {
                        observerCall(lifecycleOwner);
                    }
                });
            }else if(adapter == null){
                Log.e("ShakesFragment", "onCreateView: adapter null quando allShakes NON vuoto");
            }
        }




    }

    private void observerCall(LifecycleOwner lifecycleOwner){
        model.getResetShakes().observe(lifecycleOwner, resetShakes -> {
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
                            shakes = Shake.parseShakes(allShakes);
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
            Log.e("ShakesFragment", "onResume: adapter not null");
            adapter.registerAdapterDataObserver(observer);
            isObserverRegistered = true;
            getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), lifecycleOwner -> {
                if (lifecycleOwner != null) {
                    observerCall(lifecycleOwner);
                }
            });
        }else{
            Log.e("ShakesFragment", "onResume: adapter null");
        }


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

    }

}