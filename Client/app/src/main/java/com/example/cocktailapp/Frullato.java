package com.example.cocktailapp;

import androidx.annotation.NonNull;

import java.util.List;

public class Frullato extends Bevanda {

    public Frullato(String nome, double prezzo, List<String> ingredienti, int quantità) {
        super(nome, prezzo, ingredienti, quantità);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }


}
