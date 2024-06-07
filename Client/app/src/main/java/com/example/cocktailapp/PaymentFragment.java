package com.example.cocktailapp;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PaymentFragment extends Fragment {
    private Button PayButton;
    private Carrello carrello;
    private Client client;
    private CartObserver model;
    private boolean success;
    private ExecutorService executor;
    private Handler handler;
    private SweetAlertDialog waitingDialog;



    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance() {
        return new PaymentFragment();
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
        model = new ViewModelProvider(requireActivity()).get(CartObserver.class);
        PayButton = view.findViewById(R.id.PayButton);

        waitingDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        waitingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        waitingDialog.setTitleText("Attendere prego...");
        waitingDialog.setCancelable(false);


        PayButton.setOnClickListener(v -> {
            if(carrello.getBeverages().isEmpty()) {
                Toast.makeText(getContext(), "Carrello vuoto", Toast.LENGTH_SHORT).show();
            }else{
                executor = Executors.newSingleThreadExecutor();
                handler = new Handler(Looper.getMainLooper());
                waitingDialog.show();

                executor.execute(() -> {
                    success = sendBeveragesToServer();
                    handler.post(() -> {
                        model.setTotalCartValue(carrello.calculateTotal());
                        carrello.viewItems();
                        if(success){
                            Log.d("PaymentFragment", "Sto nell'if del pagamento e success è " +success);
                            waitingDialog.dismiss();
                            Toast.makeText(getContext(), "Pagamento completato con successo!", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d("PaymentFragment", "Sto nell'else del pagamento e success è " +success);
                            waitingDialog.dismiss();
                            Toast.makeText(getContext(), "Errore durante il pagamento", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                executor.shutdown();
                
            }
        });
    }

    private boolean sendBeveragesToServer(){
        try {
            boolean hasCocktails = false;
            boolean hasShakes = false;
            ArrayList<String> cocktails = new ArrayList<>();
            ArrayList<String> shakes =  new ArrayList<>();


            for (int i = 0; i < carrello.getBeverages().size(); i++) {
                if(carrello.getBeverages().get(i) instanceof Cocktail){
                    cocktails.add("1`" + carrello.getBeverages().get(i).getNome() + "`" + carrello.getBeverages().get(i).getQuantita());
                    hasCocktails = true;
                }else{
                    shakes.add("2`" + carrello.getBeverages().get(i).getNome() + "`" + carrello.getBeverages().get(i).getQuantita());
                    hasShakes = true;
                }
            }

            client.sendData("8");
            for (String c : cocktails) {
                Log.d("sendBeveragesToServer","Sto per inviare il cocktail: " +c);
                client.sendData(c);
                try {
                    Log.d("sendBeveragesToServer","Sto dormendo");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e("sendBeveragesToServer", "Errore sleep durante l'invio dei cocktail: " + e.getMessage());
                }
            }
            for (String c : shakes) {
                Log.d("sendBeveragesToServer","Sto per inviare il frullato: " +c);
                client.sendData(c);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e("sendBeveragesToServer", "Errore sleep durante l'invio dei frullati: " +e.getMessage());
                }
            }


            try {
                client.sendData("Fine");
                client.setSocketTimeout(10000);
                System.out.println("Aspetto la risposta dal server....");
                String risposta = client.receiveData();
                if (!risposta.equals("ERRORE")) {
                    System.out.println("Cancellazione completata di :");
                    System.out.println("Cocktails: " + cocktails);
                    System.out.println("Shakes: " + shakes);
                    if(hasCocktails){
                        model.setResetCocktails(true);
                    }
                    if(hasShakes){
                        model.setResetShakes(true);
                    }
                    model.setResetCart(true);
                    model.setResetRecommended(true);
                    carrello.emptyCarrello();
                    Thread.sleep(1000);
                    return true;
                } else {
                    Log.e("sendBeveragesToServer","Il server ha riscontrato un errore nella cancellazione dei drink e dei frullati");
                    if(hasCocktails){
                        model.setResetCocktails(true);
                    }
                    if(hasShakes){
                        model.setResetShakes(true);
                    }
                    model.setResetCart(true);
                    model.setResetRecommended(true);
                    carrello.emptyCarrello();
                    return false;
                }
            }catch (IOException e) {
                Log.e("sendBeveragesToServer", "Errore durante l'invio/ricezione dei dati: " + e.getMessage());
            }catch(Exception e) {
                Log.e("sendBeveragesToServer", "Errore durante la ricezione della risposta al comando di cancellazione dei drink e dei frullati, impossibile cancellare: " + e.getMessage());
            }

        }catch (Exception e) {
            Log.e("PaymentFragment", "Errore durante l'invio dei drink e dei frullati: " + e.getMessage());
        }


        return false;
    }
}