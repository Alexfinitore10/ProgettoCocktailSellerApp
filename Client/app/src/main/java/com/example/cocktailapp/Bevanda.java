package com.example.cocktailapp;

public class Bevanda {
    private String nome;
    private double prezzo;

    public Bevanda(String name, double price) {
        this.nome = name;
        this.prezzo = price;
    }

    public String getName() {
        return nome;
    }

    public double getPrezzo() {
        return prezzo;
    }
}


