package com.example.cocktailapp;


import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.math.BigDecimal;

public class ShopActivity extends AppCompatActivity {
    private TextView TotalPrice;
    private Carrello carrello;
    private Client client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        client = Client.getIstance();

        TabLayout tabLayout = findViewById(R.id.NameTabLayout);
        ViewPager2 viewPager = findViewById(R.id.ViewPagerCategories);
        
        PagerAdapter adapter = new PagerAdapter(this);
        viewPager.setAdapter(adapter);
        TotalPrice = findViewById(R.id.TotalTextView);
        TotalPrice.setText("Totale carrello: ");


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("ViewPager2", "Page selected: " + position);
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + position);

                if(fragment instanceof CocktailFragment){
                    Log.d("ShopActivity", "CocktailFragment selected");
                }else if(fragment instanceof ShakesFragment){
                    Log.d("ShopActivity", "ShakesFragment selected");
                }else if(fragment instanceof RecommendedFragment){
                    Log.d("ShopActivity", "RecommendedFragment selected");
                }else if(fragment instanceof CartFragment){
                    Log.d("ShopActivity", "CartFragment selected");
                }else if(fragment instanceof PaymentFragment){
                    Log.d("ShopActivity", "PaymentFragment selected");
                }else if(fragment instanceof LogOutFragment){
                    Log.d("ShopActivity", "LogOutFragment selected");
                }
            }
        });


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Cocktail");
                    break;
                case 1:
                    tab.setText("Frullati");
                    break;
                case 2:
                    tab.setText("Consigliati");
                    break;
                case 3:
                    tab.setText("Carrello");
                    break;
                case 4:
                    tab.setText("Pagamento");
                    break;
                case 5:
                    tab.setText("Log Out");
                    break;
            }
        }).attach();

        CartObserver model = new ViewModelProvider(this).get(CartObserver.class);
        carrello = Carrello.getInstance();
        carrello.setObserver(model);

        model.getTotalCartValue().observe(this, queue -> {
            while (!queue.isEmpty()) {
                BigDecimal prezzo = queue.poll();
                String prezzo_string = prezzo.toString();
                TotalPrice.setText("Totale carrello: " + prezzo_string + "€");
            }
        });


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
               //Non mettere codice così l'utente non può uscire. Per il Log Out esiste una schermata apposita
            }
        });



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        try {
            for (Fragment fragment : manager.getFragments()) {
                if (fragment != null) {
                    trans.remove(fragment);
                }
            }
            trans.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.e("ShopActivity", "onDestroy: errore nella rimozione dei fragment:" +e.getMessage());
            client.closeConnection();
        } catch(Exception e){
            Log.e("ShopActivity", "onDestroy: errore generico nella rimozione dei fragment:" +e.getMessage());
            client.closeConnection();
        }
    }


}