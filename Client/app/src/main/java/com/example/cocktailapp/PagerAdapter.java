package com.example.cocktailapp;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {
    private final SparseArray<Fragment> fragmentCache = new SparseArray<>();

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragmentCache.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = CocktailFragment.newInstance();
                    break;
                case 1:
                    fragment = ShakesFragment.newInstance();
                    break;
                case 2:
                    fragment = RecommendedFragment.newInstance();
                    break;
                case 3:
                    fragment = CartFragment.newInstance();
                    break;
                case 4:
                    fragment = PaymentFragment.newInstance();
                    break;
                case 5:
                    fragment = LogOutFragment.newInstance();
                    break;
            }
            fragmentCache.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 6;
    }

}
