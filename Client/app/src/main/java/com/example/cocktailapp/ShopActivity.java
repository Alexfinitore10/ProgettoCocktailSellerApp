package com.example.cocktailapp;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ShopActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private PagerAdapter adapter;
    private TextView TotalPrice;
    private Carrello carrello;
    private Client client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        client = Client.getIstance();
        tabLayout = findViewById(R.id.NameTabLayout);
        viewPager = findViewById(R.id.ViewPagerCategories);
        adapter = new PagerAdapter(this);
        viewPager.setAdapter(adapter);
        TotalPrice = findViewById(R.id.TotalTextView);
        TotalPrice.setText("Totale carrello: ");

        CartObserver model = new ViewModelProvider(this).get(CartObserver.class);
        carrello = Carrello.getInstance();
        carrello.setObserver(model);

        model.getTotalCartValue().observe(this, queue -> {
            while (!queue.isEmpty()) {
                Double prezzo = queue.poll();
                String prezzo_string = String.valueOf(prezzo);
                prezzo_string = String.format("%.2f", prezzo);
                TotalPrice.setText("Totale carrello: " + prezzo_string + "€");
            }
        });



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
                    tab.setText("Carrello");
                    break;
                case 3:
                    tab.setText("Consigliati");
                    break;
                case 4:
                    tab.setText("Log Out");
                    break;
            }
        }).attach();


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
               //Non mettere codice così l'utente non può uscire. Per il Log Out esiste una schermata apposita
            }
        });



    }

}