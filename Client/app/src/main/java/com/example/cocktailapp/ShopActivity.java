package com.example.cocktailapp;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ShopActivity extends AppCompatActivity {
    private TabLayout tabLayout;

    private ViewPager2 viewPager;

    private PagerAdapter adapter;
    private int backButtonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        Client client = Client.getIstanza();
        tabLayout = findViewById(R.id.NameTabLayout);
        viewPager = findViewById(R.id.ViewPagerCategories);
        adapter = new PagerAdapter(this);
        viewPager.setAdapter(adapter);


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

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Cocktail");
                        break;
                    case 1:
                        tab.setText("Frullati");
                        break;
                    case 2:
                        tab.setText("Carrello");
                }
            }
        }).attach();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backButtonCount++;
                int position = viewPager.getCurrentItem();
                // Show the pop-up dialog if the back button has been pressed 3 times
                if (backButtonCount == 3 && position == 0) {
                    showLogoutDialog(client);
                    backButtonCount = 0;
                }

                if(position > 0){
                    viewPager.setCurrentItem(position - 1);
                }

            }
        });


    }

    private void showLogoutDialog(Client client) {
        // Create a pop-up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log Out")
                .setMessage("Vuoi davvero disconnetterti?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        client.setLogged(false);
                        startActivity(new Intent(ShopActivity.this, LoginActivity.class));
                    }
                })
                .setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Change the color of the positive button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#F98500"));

        // Change the color of the negative button
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F98500"));


    }



}