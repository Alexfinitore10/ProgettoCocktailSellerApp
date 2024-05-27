package com.example.cocktailapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {


    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
          switch (position){
              case 0:
                  return new CocktailFragment();
              case 1:
                  return new ShakesFragment();
              case 2:
                  return new RecommendedFragment();
              case 3:
                  return new CartFragment();
              case 4:
                  return new PaymentFragment();
              case 5:
                  return new LogOutFragment();
          }
          return new CocktailFragment();
    }

    @Override
    public int getItemCount() {
        return 6;
    }

}
