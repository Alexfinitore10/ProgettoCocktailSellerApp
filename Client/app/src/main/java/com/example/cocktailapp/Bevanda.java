package com.example.cocktailapp;

public class Beverage {
    private String nome;
    private double prezzo;

    public Beverage(String name, double price) {
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


