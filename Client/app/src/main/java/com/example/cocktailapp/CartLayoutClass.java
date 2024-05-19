package com.example.cocktailapp;

import java.util.ArrayList;
import java.util.List;

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
