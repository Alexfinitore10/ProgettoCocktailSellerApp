package com.example.cocktailapp.Adapter;

import com.example.cocktailapp.Model.Bevanda;

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
