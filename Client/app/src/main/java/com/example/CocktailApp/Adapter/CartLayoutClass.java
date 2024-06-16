package com.example.CocktailApp.Adapter;



import com.example.CocktailApp.Model.Bevanda;

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
