package com.example.cocktailapp;

public class Frullato extends Bevanda {
    private String ingredienti;

    public Frullato(String nome, double prezzo, String ingredienti) {
        super(nome, prezzo);
        this.ingredienti = ingredienti;
    }

}
