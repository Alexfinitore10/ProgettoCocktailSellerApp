package com.example.cocktailapp;

public class Cocktail extends Bevanda {
    private String ingredienti;
    private double gradazione_alcolica;

    public Cocktail(String name, double price, String ingredients, double alcoholContent) {
        super(name, price);
        this.ingredienti = ingredients;
        this.gradazione_alcolica = alcoholContent;
    }
}
