package com.example.cocktailapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class PaymentFragment extends Fragment {
    private Button YesButton;
    private Carrello carrello;
    private Client client;

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
        carrello = Carrello.getInstance();
        client = Client.getIstance();
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
        CartObserver model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
        YesButton = view.findViewById(R.id.YesButton);


        YesButton.setOnClickListener(v -> {
            if(carrello.calculateTotal() == 0) {
                Toast.makeText(getContext(), "Carrello vuoto", Toast.LENGTH_SHORT).show();
            }else{
                Runnable sendToServer = () -> sendBeveragesToServer(carrello);
                Thread sendToServerStarter = new Thread(sendToServer);
                sendToServerStarter.start();
                try {
                    sendToServerStarter.join();
                } catch (InterruptedException e) {
                    Log.e("PaymentFragment", "Errore join");
                }
                carrello.emptyCarrello();
                carrello.viewItems();
                model.setTotalCartValue(carrello.calculateTotal());
                model.setPaymentSuccess(true);
                Toast.makeText(getContext(), "Pagamento completato con successo!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendBeveragesToServer(Carrello carrello){
        String response = "";
        ArrayList<String> cocktails = new ArrayList<>();
        ArrayList<String> shakes =  new ArrayList<>();
        for (int i = 0; i < carrello.getBeverages().size(); i++) {
            if(carrello.getBeverages().get(i) instanceof Cocktail){
                cocktails.add("1`" + carrello.getBeverages().get(i).getNome() + "`" + carrello.getBeverages().get(i).getQuantita());
            }else{
                shakes.add("2`" + carrello.getBeverages().get(i).getNome() + "`" + carrello.getBeverages().get(i).getQuantita());
            }
        }

        client.sendData("8");
        for (String c : cocktails) {
            client.sendData(c);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.e("sendBeveragesToServer", "Errore sleep durante l'invio dei cocktail");
            }
        }
        for (String c : shakes) {
            client.sendData(c);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.e("sendBeveragesToServer", "Errore sleep durante l'invio dei frullati");
            }
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        do{
            try {
                response = client.receiveData();
            } catch (IOException e) {
                Log.e("sendBeveragesToServer", "Errore durante la ricezione di dati dal server: " + e.getMessage());
            } catch (InterruptedException e) {
                Log.e("sendBeveragesToServer", "Errore chiamata receive:" +e.getMessage());
            }catch (Exception e){
                Log.e("sendBeveragesToServer", "Errore:" +e.getMessage());
            }
        }while(!response.equals("Fine"));
    }
}