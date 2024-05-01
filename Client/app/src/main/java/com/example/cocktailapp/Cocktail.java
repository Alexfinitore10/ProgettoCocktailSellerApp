package com.example.cocktailapp;

public class Cocktail extends Bevanda {
    private double gradazione_alcolica;

    public Cocktail(String name, double price,String ingredients, double alcoholContent) {
        super(name, price, ingredients);
        this.gradazione_alcolica = alcoholContent;
    }

    public double getGradazione_alcolica() {
        return gradazione_alcolica;
    }
}
