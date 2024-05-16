package com.example.cocktailapp;

import java.util.List;

public class CartLayoutClass {
    private List<Cocktail> cocktails;
    private List<Shake> shakes;

    public CartLayoutClass(List<Cocktail> cocktails, List<Shake> shakes) {
        this.cocktails = cocktails;
        this.shakes = shakes;
    }

    public List<Cocktail> getCocktails() {
        return cocktails;
    }

    public List<Shake> getShakes() {
        return shakes;
    }
}
