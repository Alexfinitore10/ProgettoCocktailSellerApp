package com.example.CocktailApp.Adapter;

import com.example.CocktailApp.Model.Bevanda;

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
