package com.example.cocktailapp;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

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
                Double prezzo = queue.poll();
                String prezzo_string = String.valueOf(prezzo);
                prezzo_string = String.format("%.2f", prezzo);
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

}