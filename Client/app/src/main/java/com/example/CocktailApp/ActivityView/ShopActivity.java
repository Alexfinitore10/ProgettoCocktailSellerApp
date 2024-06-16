package com.example.CocktailApp.ActivityView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.CocktailApp.FragmentView.CocktailFragment;
import com.example.CocktailApp.FragmentView.LogOutFragment;
import com.example.CocktailApp.FragmentView.RecommendedFragment;
import com.example.CocktailApp.FragmentView.ShakesFragment;
import com.example.CocktailApp.Model.Client;
import com.example.CocktailApp.Model.Cocktail;
import com.example.CocktailApp.Model.CustomSharedPreferences;
import com.example.CocktailApp.Model.Shake;
import com.example.CocktailApp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ShopActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Client client;
    private FloatingActionButton toCartButton;
    private String allCocktailsString,allShakesString,recommendedCocktailsString,recommendedShakesString = "";
    private ArrayList<Cocktail> allCocktails = new ArrayList<>(); //testo il parsing per vedere se va a buon fine prima di mandarlo ai fragment
    private ArrayList<Shake> allShakes = new ArrayList<>();  //testo il parsing per vedere se va a buon fine prima di mandarlo ai fragment
    private CustomSharedPreferences exportString;
    private static final String TAG = "ShopActivity";
    public static final String COCKTAILS = "cocktail string";
    public static final String SHAKES = "shake string";
    public static final String RECOMMENDED_COCKTAILS = "recommended cocktails";
    public static final String RECOMMENDED_SHAKES = "recommended shakes";
    private ExecutorService executor;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        client = Client.getIstance();
        exportString = CustomSharedPreferences.getInstance(this);

        toCartButton = findViewById(R.id.toCartFloatingButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        fragmentManager.beginTransaction().add(R.id.frameLayout, CocktailFragment.getInstance(), "1").commit();


        toCartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, CartActivity.class);
            startActivity(intent);
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.cocktails:
                    Log.d(TAG, "Cocktails fragment");
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, CocktailFragment.getInstance()).commit();
                    break;

                case R.id.shakes:
                    Log.d(TAG, "Shakes fragment");
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, ShakesFragment.getInstance()).commit();
                    break;

                case R.id.recommended:
                    Log.d(TAG, "Recommended fragment");
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, RecommendedFragment.getInstance()).commit();
                    break;

                case R.id.logOut:
                    Log.d(TAG, "Log out fragment");
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, LogOutFragment.getInstance()).commit();
                    break;

            }

            return true;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Impedire il logout cliccando back (basta non scrivere codice)
            }
        });

        Intent onPayment = getIntent();
        boolean paymentSuccessful = onPayment.getBooleanExtra("paymentSuccess", true);

        if(paymentSuccessful){
            exportString.clear();
        }

        if(exportString.isEmpty()){
            downloadData();
        }

    }

    private void downloadData(){
        executor = Executors.newFixedThreadPool(2);

        executor.execute(() -> {
            boolean parsingsuccesfull = false;
            do {
                try {
                    allCocktailsString = retrieveCocktails();
                    allCocktails = Cocktail.parseCocktails(allCocktailsString);
                    parsingsuccesfull = true;
                    exportString.write(COCKTAILS, allCocktailsString);
                } catch (IOException e) {
                    Log.e(TAG, "Impossibile recuperare la lista dei cocktail: " +e.getMessage());
                } catch (InterruptedException e) {
                    Log.e(TAG, "Errore esecuzione recupero lista dei cocktail: " +e.getMessage());
                } catch (IndexOutOfBoundsException e){
                    Log.e(TAG, "Non ci sono cocktails nella lista: " +e.getMessage());
                } catch (Exception e){
                    Log.e(TAG, "Errore parsing lista dei cocktail: " +e.getMessage());
                }
            } while (!parsingsuccesfull);

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                Log.w(TAG, "Sto per chiedere i cocktail raccomandati al server");

                recommendedCocktailsString = retrieveRecommendedCocktails();
                Log.v(TAG,"Stringa cocktail raccomandati:" +recommendedCocktailsString);
                if(isGetCocktailsOk()) {
                    Log.v(TAG, "isGetCocktailsOk dovrebbe essere true");
                    exportString.write(RECOMMENDED_COCKTAILS, recommendedCocktailsString);
                    Log.v(TAG, "La stringa dei cocktail raccomandati salvata nelle preferences è:" +exportString.read(RECOMMENDED_COCKTAILS,""));
                }else{
                    Log.v(TAG, "isGetCocktailsOk dovrebbe essere false");
                    exportString.write(RECOMMENDED_COCKTAILS, "");
                    Log.v(TAG, "La stringa dei cocktail raccomandati salvata nelle preferences è:" +exportString.read(RECOMMENDED_COCKTAILS,""));
                }
            } catch (IOException e) {
                Log.e(TAG, "Impossibile recuperare la lista dei cocktail raccomandati: " +e.getMessage());
            } catch (InterruptedException e) {
                Log.e(TAG, "Errore esecuzione recupero lista dei cocktail raccomandati: " +e.getMessage());
            }
        });

        executor.execute(() -> {
            boolean parsingsuccesfull = false;
            do {
                try {
                    allShakesString = retrieveShakes();
                    allShakes = Shake.parseShakes(allShakesString);
                    parsingsuccesfull = true;
                    exportString.write(SHAKES, allShakesString);
                } catch (IOException e) {
                    Log.e(TAG, "Impossibile recuperare la lista dei frullati: " +e.getMessage());
                } catch (InterruptedException e) {
                    Log.e(TAG, "Errore esecuzione recupero lista dei frullati: " +e.getMessage());
                } catch (IndexOutOfBoundsException e){
                    Log.e(TAG, "Non ci sono frullati nella lista: " +e.getMessage());
                } catch (Exception e){
                    Log.e(TAG, "Errore parsing lista dei frullati: " +e.getMessage());
                }
            } while (!parsingsuccesfull);

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                recommendedShakesString = retrieveRecommendedShakes();
                Log.v(TAG,"Stringa shake raccomandati:" +recommendedShakesString);
                if(isGetShakesOk()) {
                    Log.v(TAG, "isGetShakesOk dovrebbe essere true");
                    exportString.write(RECOMMENDED_SHAKES, recommendedShakesString);
                    Log.v(TAG, "La stringa degli shakes raccomandati salvata nelle preferences è:" +exportString.read(RECOMMENDED_SHAKES,""));
                }else{
                    Log.v(TAG, "isGetShakesOk dovrebbe essere false");
                    exportString.write(RECOMMENDED_SHAKES, "");
                    Log.v(TAG, "La stringa degli shakes raccomandati salvata nelle preferences è:" +exportString.read(RECOMMENDED_SHAKES,""));
                }
            } catch (IOException e) {
                Log.e(TAG, "Impossibile recuperare la lista dei frullati raccomandati: " +e.getMessage());
            } catch (InterruptedException e) {
                Log.e(TAG, "Errore esecuzione recupero lista dei frullati raccomandati: " +e.getMessage());
            }
        });
    }


    private String retrieveCocktails() throws IOException, InterruptedException {
        Log.i(TAG, "retrieveCocktails ");
        String command = "3";
        client.sendData(command);
        client.setSocketTimeout(10000);
        return client.bufferedReceive();
    }

    private String retrieveShakes() throws IOException, InterruptedException {
        Log.i(TAG, "retrieveShakes ");
        String command = "4";
        client.sendData(command);
        client.setSocketTimeout(5000);
        return client.bufferedReceive();
    }

    private String retrieveRecommendedCocktails() throws IOException, InterruptedException {
        Log.i(TAG, "retrieveRecommendedCocktails");
        String command = "9";
        client.sendData(command);
        client.setSocketTimeout(5000);
        return client.bufferedReceive();
    }

    private String retrieveRecommendedShakes() throws IOException, InterruptedException {
        Log.i(TAG, "retrieveRecommendedShakes");
        String command = "10";
        client.sendData(command);
        client.setSocketTimeout(5000);
        return client.bufferedReceive();
    }

    private boolean isGetCocktailsOk() {

          String pattern_cocktails = "^[a-zA-Z ]+, \\[[a-zA-Z ;'\\u00C0-\\u00FF]+\\], \\d+(\\.\\d+)?, \\d+(\\.\\d+)?, \\d+\t\n$";

        switch (recommendedCocktailsString) {
            case "NOKERR" -> {
                Log.w(TAG, "Non è stato possibile prendere i cocktail consigliati dal server");
                return false;
            }
            case "Ness" -> {
                Log.w(TAG, "Nessun cocktail è presente nei recommended, quindi non è possibile effettuare i recommended");
                return false;
            }
            case "Pochi" -> {
                Log.w(TAG, "Non ci sono abbastanza cocktail per effettuare un recommended, acquista qualche cocktail prima");
                return false;
            }
            default -> {
                boolean regexMatch = checkRegex(recommendedCocktailsString,pattern_cocktails);
                Log.v(TAG, "isGetCocktailsOk - regexMatch: " + regexMatch);
                return regexMatch;
            }

        }
    }


    private boolean isGetShakesOk() {

        String pattern_shakes = "^[a-zA-Z ]+, \\[[a-zA-Z ;'\\u00C0-\\u00FF]+\\], \\d+(\\.\\d+)?, \\d+\t\n$";

        switch(recommendedShakesString){
            case "NOKERR" -> {
                Log.w(TAG, "Non è stato possibile prendere i frullati consigliati dal server");
                return false;
            }
            case "Ness" -> {
                Log.w(TAG, "Nessun frullato è presente nei recommended, quindi non è possibile effettuare i recommended");
                return false;
            }
            case "Pochi" -> {
                Log.w(TAG, "Non ci sono abbastanza frullati per effettuare un recommended, acquista qualche frullato prima");
                return false;
            }
            default -> {
                boolean regexMatch = checkRegex(recommendedShakesString,pattern_shakes);
                Log.v(TAG, "isGetShakesOk - regexMatch: " + regexMatch);
                return regexMatch;
            }

        }

    }

    private boolean checkRegex(String input, String regex){

        Log.v("Sono nel checkRegex", "l'input é: " + input);

        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

        Matcher matcher = pattern.matcher(input);

        boolean found = matcher.find();
        Log.v("Sono nel checkRegex", "matcher.find é: " + found);
        return found;
    }

    public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        Log.v(TAG, "Spegnimento threadpool in corso...");
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(2, TimeUnit.SECONDS)) {
                Log.v(TAG, "Non sono riuscito ad aspettare che il threadpool terminasse: spegnimento forzato");
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            Log.e(TAG, "Eccezione interruzione threadpool: " + ex.getMessage());
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        awaitTerminationAfterShutdown(executor);
    }

}
