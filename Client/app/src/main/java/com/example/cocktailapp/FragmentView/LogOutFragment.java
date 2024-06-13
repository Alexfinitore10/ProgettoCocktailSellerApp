package com.example.cocktailapp.FragmentView;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cocktailapp.Model.CartObserver;
import com.example.cocktailapp.MainActivity;
import com.example.cocktailapp.Model.Carrello;
import com.example.cocktailapp.Model.Client;
import com.example.cocktailapp.R;


public class LogOutFragment extends Fragment {
    private Button LogOut;
    private Client client;
    private Carrello carrello;
    private CartObserver model;
    private static LogOutFragment instance;


//    public LogOutFragment() {
//        // Required empty public constructor
//    }


//    public static LogOutFragment newInstance() {
//        return new LogOutFragment();
//    }

    private LogOutFragment(){

    }

    public static LogOutFragment getInstance() {
        if (instance == null) {
            instance = new LogOutFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = Client.getIstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        carrello = Carrello.getInstance();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_out, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       model = new ViewModelProvider(requireActivity()).get(CartObserver.class);

       LogOut = view.findViewById(R.id.LogOutButton);

       LogOut.setOnClickListener(v -> {
           client.setLogged(false);
           model.setIsLoggedIn(client.isLogged());
           carrello.emptyCarrello();

           startActivity(new Intent(getContext(), MainActivity.class));
       });



    }
}