package com.example.cocktailapp;

public class Bevanda {
    private String nome;
    private double prezzo;
    private String ingredienti;

    public Bevanda(String name, double price, String ingredients) {
        this.nome = name;
        this.prezzo = price;
        this.ingredienti = ingredients;
    }

    public String getNome() {
        return nome;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public String getIngredienti() {
        return ingredienti;
    }
}


