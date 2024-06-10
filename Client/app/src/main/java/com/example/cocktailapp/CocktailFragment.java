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
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class CocktailFragment extends Fragment {
    private CocktailRecyclerViewAdapter adapter;
    private ArrayList<CocktailLayoutClass> list;
    private RecyclerView recyclerView;
    private Client client;
    private ArrayList<Cocktail> cocktails;
    private String allCocktails;
    private CartObserver model;
    private ExecutorService executor;
    private Handler handler;
    private RecyclerView.AdapterDataObserver observer;
    private boolean isObserverRegistered = false;
    private static CocktailFragment instance;


//    public CocktailFragment() {
//        // Required empty public constructor
//    }

    private CocktailFragment() {

    }

    public static CocktailFragment getInstance() {
        if (instance == null) {
            instance = new CocktailFragment();
        }
        return instance;
    }

//    public static CocktailFragment newInstance() {
//        return new CocktailFragment();
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
        return inflater.inflate(R.layout.fragment_cocktail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = new ArrayList<>();
        cocktails = new ArrayList<>();
        model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
        recyclerView = view.findViewById(R.id.CocktailRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                recyclerView.post(() -> fillCocktails(itemCount));
            }
        };

        if(model.getAllCocktails() != null){
            allCocktails = model.getAllCocktails();
        }else{
            allCocktails = "";
        }



        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        if(allCocktails.isEmpty()){
            executor.execute(() -> {
                boolean parsingSuccessful = false;

                do {
                    try {
                        allCocktails = getAllCocktails();
                        model.setAllCocktails(allCocktails);
                        cocktails = Cocktail.parseCocktails(allCocktails);
                        parsingSuccessful = true;
                    } catch (IOException e) {
                        Log.e("CocktailFragment", "Impossibile recuperare la lista dei cocktail: " +e.getMessage());
                    } catch (InterruptedException e){
                        Log.e("CocktailFragment", "Errore esecuzione recupero lista dei cocktail: " +e.getMessage());
                    } catch (IndexOutOfBoundsException e) {
                        Log.e("CocktailFragment", "Non ci sono cocktails nella lista: " +e.getMessage());
                    } catch (Exception e){
                        Log.e("CocktailFragment", "Errore parsing lista dei cocktail: " +e.getMessage());
                    }
                } while (!parsingSuccessful);

                handler.post(() -> {

                    for (Cocktail c : cocktails) {
                        list.add(new CocktailLayoutClass(c.getNome(),c.getIngredienti(),c.getGradazione_alcolica(),c.getPrezzo(),c.getQuantita()));
                    }
                    adapter = new CocktailRecyclerViewAdapter(list,getContext(),cocktails,model);
                    recyclerView.setAdapter(adapter);
                    if(adapter != null && !isObserverRegistered) {
                        adapter.registerAdapterDataObserver(observer);
                        isObserverRegistered = true;
                    }else if(adapter == null) {
                        Log.e("CocktailFragment", "onCreateView: adapter null quando allCocktails vuoto");
                    }

                });
            });

            executor.shutdown();
        }else{
            try {
                Log.d("CocktailFragment",cocktails.toString());
                cocktails = Cocktail.parseCocktails(allCocktails);
            } catch (IndexOutOfBoundsException e) {
                Log.e("CocktailFragment", "Non ci sono cocktails nella lista: " +e.getMessage());
                cocktails = new ArrayList<>();
            } catch (Exception e){
                Log.e("CocktailFragment", "Errore parsing lista dei cocktail: " +e.getMessage());
                cocktails = new ArrayList<>();
            }
            for (Cocktail c : cocktails) {
                list.add(new CocktailLayoutClass(c.getNome(),c.getIngredienti(),c.getGradazione_alcolica(),c.getPrezzo(),c.getQuantita()));
            }

            adapter = new CocktailRecyclerViewAdapter(list,getContext(),cocktails,model);
            recyclerView.setAdapter(adapter);
            if(adapter != null && !isObserverRegistered) {
                adapter.registerAdapterDataObserver(observer);
                isObserverRegistered = true;
            }else if(adapter == null) {
                Log.e("CocktailFragment", "onCreateView: adapter null quando allCocktails NON vuoto ");
            }
        }

        //observerCall();


        model.getResetCocktails().observe(getViewLifecycleOwner(), resetCocktails -> {
            Log.i("CocktailFragment", "observerCall resetCocktails: " +resetCocktails);
            ExecutorService localExecutor = Executors.newSingleThreadExecutor();
            Handler localHandler = new Handler(Looper.getMainLooper());
            if (resetCocktails) {
                localExecutor.execute(() -> {
                    boolean parsingSuccessful = false;
                    do {
                        Log.d("CocktailFragment", "parsingSuccessful:" +parsingSuccessful);
                        try {
                            allCocktails = getAllCocktails();
                            Log.d("CocktailFragment", "allCocktails:" +allCocktails);
                            model.setAllCocktails(allCocktails);
                            cocktails = Cocktail.parseCocktails(allCocktails);
                            Log.d("CocktailFragment", "cocktails:" +cocktails);
                            parsingSuccessful = true;
                        } catch (IOException e) {
                            Log.e("CocktailFragment", "Impossibile recuperare la lista dei cocktail: " +e.getMessage());
                        } catch (InterruptedException e){
                            Log.e("CocktailFragment", "Errore esecuzione recupero lista dei cocktail: " +e.getMessage());
                        } catch (IndexOutOfBoundsException e) {
                            Log.e("CocktailFragment", "Non ci sono cocktails nella lista: " +e.getMessage());
                        } catch (Exception e){
                            Log.e("CocktailFragment", "Errore parsing lista dei cocktail: " +e.getMessage());
                        }
                    } while (!parsingSuccessful);

                    localHandler.post(() -> {
                        int listSize = list.size();
                        list.clear();
                        adapter.notifyItemRangeRemoved(0, listSize);
                    });
                });
                localExecutor.shutdown();
            }
            Log.i("CocktailFragment", "observerCall: rimozione observer");
            model.getResetCocktails().removeObservers(getViewLifecycleOwner());
        });


    }

//    private void observerCall(){
//        model.getResetCocktails().observe(getViewLifecycleOwner(), resetCocktails -> {
//            Log.i("CocktailFragment", "observerCall resetCocktails: " +resetCocktails);
//            executor = Executors.newSingleThreadExecutor();
//            handler = new Handler(Looper.getMainLooper());
//            if (resetCocktails) {
//                executor.execute(() -> {
//                    boolean parsingSuccessful = false;
//                    do {
//                        Log.d("CocktailFragment", "parsingSuccessful:" +parsingSuccessful);
//                        try {
//                            allCocktails = getAllCocktails();
//                            Log.d("CocktailFragment", "allCocktails:" +allCocktails);
//                            model.setAllCocktails(allCocktails);
//                            cocktails = Cocktail.parseCocktails(allCocktails);
//                            Log.d("CocktailFragment", "cocktails:" +cocktails);
//                            parsingSuccessful = true;
//                        } catch (IOException e) {
//                            Log.e("CocktailFragment", "Impossibile recuperare la lista dei cocktail: " +e.getMessage());
//                        } catch (InterruptedException e){
//                            Log.e("CocktailFragment", "Errore esecuzione recupero lista dei cocktail: " +e.getMessage());
//                        } catch (IndexOutOfBoundsException e) {
//                            Log.e("CocktailFragment", "Non ci sono cocktails nella lista: " +e.getMessage());
//                        } catch (Exception e){
//                            Log.e("CocktailFragment", "Errore parsing lista dei cocktail: " +e.getMessage());
//                        }
//                    } while (!parsingSuccessful);
//
//                    handler.post(() -> {
//                        int listSize = list.size();
//                        list.clear();
//                        adapter.notifyItemRangeRemoved(0, listSize);
//                    });
//                });
//                executor.shutdown();
//            }
//            Log.i("CocktailFragment", "observerCall: rimozione observer");
//            model.getResetCocktails().removeObservers(getViewLifecycleOwner());
//        });
//
//    }
    private String getAllCocktails() throws IOException, InterruptedException {
        Log.i("CocktailFragment", "getAllCocktails ");
        String command = "3";
        client.sendData(command);
        client.setSocketTimeout(5000);
        return client.bufferedReceive();
    }

    private void fillCocktails(int listSize){
        adapter.setCocktailList(cocktails);
        if(!cocktails.isEmpty()){
            for (Cocktail c : cocktails) {
                list.add(new CocktailLayoutClass(c.getNome(),c.getIngredienti(),c.getGradazione_alcolica(),c.getPrezzo(),c.getQuantita()));
            }
            adapter.notifyItemRangeInserted(0,listSize);
        }
        model.setResetCocktails(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && !isObserverRegistered) {
            adapter.registerAdapterDataObserver(observer);
            isObserverRegistered = true;
        }


        Log.d("CocktailFragment", "onResume: sto per chiamare l'observer");
        //observerCall();

        model.getResetCocktails().observe(getViewLifecycleOwner(), resetCocktails -> {
            Log.i("CocktailFragment", "observerCall resetCocktails: " +resetCocktails);
            ExecutorService localExecutor = Executors.newSingleThreadExecutor();
            Handler localHandler = new Handler(Looper.getMainLooper());
            if (resetCocktails) {
                localExecutor.execute(() -> {
                    boolean parsingSuccessful = false;
                    do {
                        Log.d("CocktailFragment", "parsingSuccessful:" +parsingSuccessful);
                        try {
                            allCocktails = getAllCocktails();
                            Log.d("CocktailFragment", "allCocktails:" +allCocktails);
                            model.setAllCocktails(allCocktails);
                            cocktails = Cocktail.parseCocktails(allCocktails);
                            Log.d("CocktailFragment", "cocktails:" +cocktails);
                            parsingSuccessful = true;
                        } catch (IOException e) {
                            Log.e("CocktailFragment", "Impossibile recuperare la lista dei cocktail: " +e.getMessage());
                        } catch (InterruptedException e){
                            Log.e("CocktailFragment", "Errore esecuzione recupero lista dei cocktail: " +e.getMessage());
                        } catch (IndexOutOfBoundsException e) {
                            Log.e("CocktailFragment", "Non ci sono cocktails nella lista: " +e.getMessage());
                        } catch (Exception e){
                            Log.e("CocktailFragment", "Errore parsing lista dei cocktail: " +e.getMessage());
                        }
                    } while (!parsingSuccessful);

                    localHandler.post(() -> {
                        int listSize = list.size();
                        list.clear();
                        adapter.notifyItemRangeRemoved(0, listSize);
                    });
                });
                localExecutor.shutdown();
            }
            Log.i("CocktailFragment", "observerCall: rimozione observer");
            model.getResetCocktails().removeObservers(getViewLifecycleOwner());
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null && isObserverRegistered) {
            adapter.unregisterAdapterDataObserver(observer);
            isObserverRegistered = false;
        }else if(adapter == null){
            Log.e("CocktailFragment", "onResume: adapter null e isObserverRegistered: "+isObserverRegistered);
        }
    }

}

