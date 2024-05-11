package com.example.cocktailapp;

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
 * Use the {@link NewCocktailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewCocktailFragment extends Fragment {
    private CocktailRecyclerViewAdapter adapter;
    private ArrayList<CocktailLayoutClass> list;
    private RecyclerView recyclerView;
    private Client client;
    private ArrayList<Cocktail> cocktails;
    private String allCocktails;
    private Cocktail newCocktail;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewCocktailFragment() {
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
    public static NewCocktailFragment newInstance(String param1, String param2) {
        NewCocktailFragment fragment = new NewCocktailFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_cocktail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = Client.getIstanza();
        list = new ArrayList<>();
        cocktails = new ArrayList<>();
        newCocktail = new Cocktail();


        Runnable getCocktailsTask = () -> {
            allCocktails = getAllCocktails(client);
        };
        Thread getCocktailsThread = new Thread(getCocktailsTask);
        getCocktailsThread.start();

        try {
            getCocktailsThread.join();
        } catch (InterruptedException e) {
            Log.e("onViewCreated","Errore nella join del thread: " + e.getMessage());
        }



//        newCocktail = Cocktail.parseString(allCocktails);
//        cocktails.add(newCocktail);



        recyclerView = view.findViewById(R.id.CocktailRecyclerView);

//        for(Cocktail temp: cocktails){
//            list.add(new DrinkLayoutClass(temp.getNome(),temp.getIngredienti(),temp.getGradazione_alcolica(),temp.getPrezzo(),temp.getQuantità()));
//        }


//        list.add(new CocktailLayoutClass("Mojito","Rum, Lime, Zucchero, Menta",18.0,6.0,10));
//        list.add(new CocktailLayoutClass("Bloody Mary","Vodka, Succo di pomodoro, Tabasco , Sedano , Sale , Pepe nero , Succo di limone , Salsa Worchestershire",25.0,6.0,13));
//        list.add(new CocktailLayoutClass("White Russian","Vodka, Liquore al caffè, Ghiaccio , Panna fresca",25.0,7.0,16));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CocktailRecyclerViewAdapter(list,getContext());
        recyclerView.setAdapter(adapter);

    }

    private String getAllCocktails(Client client){
        String command = "3";
        client.sendData(command);
        return client.bufferedReceive();
    }
}

