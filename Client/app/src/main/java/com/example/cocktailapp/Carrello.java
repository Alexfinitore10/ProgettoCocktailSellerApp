package com.example.cocktailapp;

import java.util.ArrayList;
import java.util.List;

public class Carrello {
    private List<Bevanda> bevande; // Lista di bevande nel carrello

    public Carrello() {
        this.bevande = new ArrayList<>();
    }

    public void aggiungiBevanda(Bevanda bevanda) {
        bevande.add(bevanda);
    }

    public void rimuoviBevanda(Bevanda bevanda) {
        bevande.remove(bevanda);
    }


}
