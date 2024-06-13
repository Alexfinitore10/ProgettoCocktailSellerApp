package com.example.cocktailapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cocktailapp.Model.CustomFragmentTransactionCallBack;
import com.example.cocktailapp.FragmentView.CartFragment;
import com.example.cocktailapp.FragmentView.CocktailFragment;
import com.example.cocktailapp.FragmentView.LogOutFragment;
import com.example.cocktailapp.FragmentView.PaymentFragment;
import com.example.cocktailapp.FragmentView.RecommendedFragment;
import com.example.cocktailapp.FragmentView.ShakesFragment;

public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        registerFragmentTransactionCallback(new CustomFragmentTransactionCallBack());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    //fragment = CocktailFragment.newInstance();
                    return CocktailFragment.getInstance();
                    //break;
                case 1:
                    //fragment = ShakesFragment.newInstance();
                    return ShakesFragment.getInstance();
                    //break;
                case 2:
                    //fragment = RecommendedFragment.newInstance();
                    return RecommendedFragment.getInstance();
                    //break;
                case 3:
                    //fragment = CartFragment.newInstance();
                    return CartFragment.getInstance();
                    //break;
                case 4:
                    //fragment = PaymentFragment.newInstance();
                    return PaymentFragment.getInstance();
                    //break;
                case 5:
                    //fragment = LogOutFragment.newInstance();
                    return LogOutFragment.getInstance();
                    //break;
                default:
                    throw new IllegalStateException("Unexpected value: " + position);
            }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

}
