package com.example.cocktailapp;

import java.util.List;

public class CartLayoutClass {
    private List<Bevanda> bevande;

    public CartLayoutClass(List<Bevanda> bevande) {
        this.bevande = bevande;
    }

    public List<Bevanda> getBevande() {
        return bevande;
    }

    public void setBevande(List<Bevanda> bevande) {
        this.bevande = bevande;
    }
}
