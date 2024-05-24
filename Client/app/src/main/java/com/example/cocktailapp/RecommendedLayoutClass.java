package com.example.cocktailapp;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecommendedLayoutClass {
    private Bevanda bevanda;

    public RecommendedLayoutClass(Bevanda bevanda) {
        this.bevanda = bevanda;
    }

    public Bevanda getBevanda() {
        return bevanda;
    }

    public void setBevanda(Bevanda bevanda) {
        this.bevanda = bevanda;
    }
}
