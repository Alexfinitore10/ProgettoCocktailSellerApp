package com.example.cocktailapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cocktailapp.CocktailFragment;
import com.example.cocktailapp.SettingsFragment;
import com.example.cocktailapp.ShakesFragment;

public class PagerAdapter extends FragmentStateAdapter {


    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new NewCocktailFragment();
            case 1:
                return new ShakesFragment();
            case 2:
                return new SettingsFragment();
        }
        return new CocktailFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
