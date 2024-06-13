package com.example.cocktailapp.Adapter;

import com.example.cocktailapp.Model.Bevanda;

public class CartLayoutClass {
    private Bevanda bevanda;

    public CartLayoutClass(Bevanda bevanda) {
        this.bevanda = bevanda;
    }

    public Bevanda getBevanda() {
        return bevanda;
    }

    public void setBevanda(Bevanda bevanda) {
        this.bevanda = bevanda;
    }

}
