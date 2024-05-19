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
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.CartRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartRecyclerViewAdapter(list,getContext());
        recyclerView.setAdapter(adapter);



    }

    @Override
    public void onResume() {
        super.onResume();
                if(carrello.isCartModified()){
                    for(int i = carrello.getLastSize(); i < carrello.getBeverages().size(); i++){
                        list.add(new CartLayoutClass(carrello.getBeverages().get(i)));
                        adapter.notifyItemInserted(i);
                    }
                    carrello.setLastSize(carrello.getBeverages().size());
                    carrello.setCartModified(false);
                }
    }

    @Override
    public void onPause() {
        super.onPause();

        //Aggiorna flag ritorno dal cart fragment allo Shakes Fragment
        SharedPreferences prefs = getActivity().getSharedPreferences("Coming Shakes Cart Flag", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("comingFromCartFragment", true);
        editor.apply();

        SharedPreferences prefs2 = getActivity().getSharedPreferences("Coming Cocktail Cart Flag", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = prefs2.edit();
        editor2.putBoolean("comingFromCartFragment", true);
        editor2.apply();
    }
}