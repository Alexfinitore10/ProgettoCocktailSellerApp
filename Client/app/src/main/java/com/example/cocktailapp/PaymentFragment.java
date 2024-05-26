package com.example.cocktailapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class PaymentFragment extends Fragment {
    private Button YesButton;
    private Carrello carrello;
    private CartObserver model;

    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance() {
        PaymentFragment fragment = new PaymentFragment();
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
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
        carrello = Carrello.getInstance();
        YesButton = view.findViewById(R.id.YesButton);


        YesButton.setOnClickListener(v -> {
            if(carrello.calculateTotal() == 0) {
                Toast.makeText(getContext(), "Carrello vuoto", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Pagamento effettuato", Toast.LENGTH_SHORT).show();
                carrello.emptyCarrello();
                carrello.viewItems();
                model.setTotalCartValue(carrello.calculateTotal());
                model.setPaymentSuccess(true);
            }
        });
    }
}