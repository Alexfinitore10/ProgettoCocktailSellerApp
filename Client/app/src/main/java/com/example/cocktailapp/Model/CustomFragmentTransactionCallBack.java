package com.example.cocktailapp.Model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CustomFragmentTransactionCallBack extends FragmentStateAdapter.FragmentTransactionCallback {

    @NonNull
    @Override
    public OnPostEventListener onFragmentPreAdded(@NonNull Fragment fragment) {
        Log.d("FragmentLifecycle", "Fragment pre-added: " + fragment.getClass().getSimpleName());
        return super.onFragmentPreAdded(fragment);
    }

    @NonNull
    @Override
    public OnPostEventListener onFragmentPreRemoved(@NonNull Fragment fragment) {
        Log.d("FragmentLifecycle", "Fragment pre-removed: " + fragment.getClass().getSimpleName());
        return super.onFragmentPreRemoved(fragment);
    }

    @NonNull
    @Override
    public OnPostEventListener onFragmentMaxLifecyclePreUpdated(@NonNull Fragment fragment, @NonNull Lifecycle.State maxLifecycleState) {
        Log.d("FragmentLifecycle", "Fragment max lifecycle pre-updated: " + fragment.getClass().getSimpleName() + " to " + maxLifecycleState);
        return super.onFragmentMaxLifecyclePreUpdated(fragment, maxLifecycleState);
    }


}
