package com.example.CocktailApp.ActivityView;

import static com.example.CocktailApp.ActivityView.ShopActivity.COCKTAILS;
import static com.example.CocktailApp.ActivityView.ShopActivity.SHAKES;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.CocktailApp.Adapter.CartLayoutClass;
import com.example.CocktailApp.Adapter.CartRecyclerViewAdapter;
import com.example.CocktailApp.Model.Bevanda;
import com.example.CocktailApp.Model.Carrello;
import com.example.CocktailApp.Model.Client;
import com.example.CocktailApp.Model.Cocktail;
import com.example.CocktailApp.Model.CustomSharedPreferences;
import com.example.CocktailApp.Model.Shake;

import com.example.CocktailApp.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CartActivity extends AppCompatActivity  {
    private ArrayList<CartLayoutClass> cartItems;
    private CartRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Cocktail> cocktailArrayList;
    private ArrayList<Shake> shakeArrayList;
    private String allCocktailsString;
    private String allShakesString;
    private static final String TAG = "CartActivity";
    private ExtendedFloatingActionButton payButton;
    private Carrello carrello;
    private Client client;
    private SweetAlertDialog waitingDialog,questionDialog;
    private CustomSharedPreferences exportString;
    private boolean paymentSuccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        payButton = findViewById(R.id.payFloatingActionButton);
        cocktailArrayList = new ArrayList<>();
        shakeArrayList = new ArrayList<>();
        cartItems = new ArrayList<>();
        carrello = Carrello.getInstance();
        client = Client.getIstance();

        for (Bevanda bevanda : carrello.getBeverages()) {
            cartItems.add(new CartLayoutClass(bevanda));
        }


        recyclerView = findViewById(R.id.CartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        exportString = CustomSharedPreferences.getInstance(this);
        allCocktailsString = exportString.read(COCKTAILS,"");
        allShakesString = exportString.read(SHAKES,"");

        Log.v(TAG, "la stringa dei cocktails e':" + allCocktailsString);
        Log.v(TAG, "la stringa dei shakes e':" + allShakesString);

        try {
            cocktailArrayList = Cocktail.parseCocktails(allCocktailsString);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Stringa dei cocktail non conforme all'arraylist:" +e.getMessage());
        } catch(Exception e) {
            Log.e(TAG, "Errore parsing cocktails:" +e.getMessage());
        }

        try {
            shakeArrayList = Shake.parseShakes(allShakesString);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Stringa dei cocktail non conforme all'arraylist:" +e.getMessage());
        } catch(Exception e) {
            Log.e(TAG, "Errore parsing cocktails:" +e.getMessage());
        }

        adapter = new CartRecyclerViewAdapter(cartItems,this,cocktailArrayList,shakeArrayList);
        recyclerView.setAdapter(adapter);

        waitingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        waitingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        waitingDialog.setTitleText("Attendere prego...");
        waitingDialog.setCancelable(false);




        payButton.setOnClickListener(v -> {
            if(carrello.getBeverages().isEmpty()){
                Toast.makeText(this, "Il carrello è vuoto", Toast.LENGTH_SHORT).show();
            }else{
                questionDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                questionDialog.setTitleText("Pagamento");
                questionDialog.setContentText("Il totale del carrello è:" + carrello.calculateTotal().toString() + "€.\nVuoi davvero procedere col pagamento?");
                questionDialog.setConfirmText("Sì");
                questionDialog.setCancelText("No");
                questionDialog.show();

                //Se premo sì
                questionDialog.setConfirmClickListener(sweetAlertDialog -> {
                    questionDialog.dismiss();
                    waitingDialog.show();

                     // Crea un nuovo ExecutorService con un singolo thread
                        ExecutorService executor = Executors.newSingleThreadExecutor();

                        // Invia un'operazione al ExecutorService e ottieni un Future
                        Future<Boolean> future = executor.submit(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                return sendBeveragesToServer();
                            }
                        });

                        // Crea un nuovo Handler per inviare il risultato al thread dell'interfaccia utente
                        Handler handler = new Handler(Looper.getMainLooper());

                        // Esegui un altro task sul ExecutorService per ottenere il risultato del Future
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Ottieni il risultato del Future
                                    final boolean result = future.get();

                                    // Invia il risultato al thread dell'interfaccia utente
                                    handler.post(() -> {
                                        if (result) {
                                            cartItems.clear();
                                            adapter.notifyDataSetChanged();
                                            paymentSuccess = true;
                                            waitingDialog.dismiss();
                                            Toast.makeText(CartActivity.this, "Pagamento effettuato con successo", Toast.LENGTH_SHORT).show();
                                        } else {
                                            cartItems.clear();
                                            adapter.notifyDataSetChanged();
                                            paymentSuccess = false;
                                            waitingDialog.dismiss();
                                            Toast.makeText(CartActivity.this, "Pagamento fallito", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (InterruptedException | ExecutionException e) {
                                    Log.e(TAG, "Errore durante l'invio dei prodotti nel carrello: " + e.getMessage());
                                }
                            }
                        });
                });

                questionDialog.setCancelClickListener(sweetAlertDialog -> {
                    questionDialog.dismiss();
                });

                getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Intent onPayment = new Intent(CartActivity.this,ShopActivity.class);
                        onPayment.putExtra("paymentSuccess",paymentSuccess);
                        startActivity(onPayment);
                    }
                });
            }

        });



    }




    private boolean sendBeveragesToServer(){
        try {
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
                client.setSocketTimeout(5000);
                System.out.println("Aspetto la risposta dal server....");
                String risposta = client.receiveData();
                if (!risposta.contains("ERRORE")) {
                    System.out.println("Cancellazione completata di :");
                    System.out.println("Cocktails: " + cocktails);
                    System.out.println("Shakes: " + shakes);
                    carrello.emptyCarrello();
                    Thread.sleep(1000);
                    return true;
                } else {
                    Log.e("sendBeveragesToServer","Il server ha riscontrato un errore nella cancellazione dei drink e dei frullati");
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