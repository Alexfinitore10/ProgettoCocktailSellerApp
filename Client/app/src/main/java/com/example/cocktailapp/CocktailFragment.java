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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CocktailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CocktailFragment extends Fragment {
    private CocktailRecyclerViewAdapter cocktailRecyclerViewAdapter;
    private ArrayList<CocktailLayoutClass> list;
    private RecyclerView recyclerView;
    private Client client;
    private ArrayList<Cocktail> cocktails;
    private String allCocktails;
    private Carrello carrello;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CocktailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewCocktailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CocktailFragment newInstance(String param1, String param2) {
        CocktailFragment fragment = new CocktailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Register for back button callbacks

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

        carrello = Carrello.getInstance();
        client = Client.getIstanza();
        list = new ArrayList<>();
        cocktails = new ArrayList<>();

        Runnable getCocktailsTask = () -> allCocktails = getAllCocktails(client);
        Thread getCocktailsThread = new Thread(getCocktailsTask);
        getCocktailsThread.start();

        try {
            getCocktailsThread.join();
        } catch (InterruptedException e) {
            Log.e("onViewCreated CocktailFragment","Errore nella join del thread: " + e.getMessage());
        }


        recyclerView = view.findViewById(R.id.CocktailRecyclerView);

        for (String c : allCocktails.split("\\n")) {
            cocktails.add(Cocktail.parseString(c));
        }

        for (Cocktail c : cocktails) {
            list.add(new CocktailLayoutClass(c.getNome(),c.getIngredienti(),c.getGradazione_alcolica(),c.getPrezzo(),c.getQuantita()));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cocktailRecyclerViewAdapter = new CocktailRecyclerViewAdapter(list,getContext(),cocktails);
        recyclerView.setAdapter(cocktailRecyclerViewAdapter);



    }

    private String getAllCocktails(Client client){
        String command = "3";
        client.sendData(command);
        return client.bufferedReceive();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Ottieni l'istanza di SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("coming Cocktail Flag", Context.MODE_PRIVATE);

        // Controlla se stai tornando da CartFragment
        boolean comingFromCartFragment = prefs.getBoolean("comingFromCartFragment", false);

        if (comingFromCartFragment) {
            Log.d("resume Cocktail Frag","Prima di aggiornare lastSize:" + carrello.getLastSize());
            carrello.setLastSize(carrello.getBeverages().size());
            Log.d("resume Cocktail Frag","Dopo aggiornamento lastSize:" + carrello.getLastSize());


            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("comingFromCartFragment", false);
            editor.apply();
        }





    }


}

