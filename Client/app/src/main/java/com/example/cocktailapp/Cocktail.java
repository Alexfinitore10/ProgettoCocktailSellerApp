package com.example.cocktailapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cocktail extends Bevanda {
    private float gradazione_alcolica;

    public Cocktail(String nome, float prezzo, List<String> ingredienti, int quantita, float grado_alcol) {
        super(nome, prezzo, ingredienti, quantita);
        this.gradazione_alcolica = grado_alcol;
    }

    public Cocktail() {

    }

    public float getGradazione_alcolica() {
        return gradazione_alcolica;
    }


    public static Cocktail parseString(String input) {
        String[] parts = input.split(", ");
    
        String nome = parts[0].trim();
    
        String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
        String[] ingredientiArray = ingredientiString.split(";");
    
        float gradazioneAlcolica = Float.parseFloat(parts[2].trim());
        float prezzo = Float.parseFloat(parts[3].trim());
        int quantita = Integer.parseInt(parts[4].trim());
    
        return new Cocktail(nome, prezzo, Arrays.asList(ingredientiArray), quantita, gradazioneAlcolica);
    }

    public static ArrayList<Cocktail> setRecommendedCocktails(String buffer) {
        ArrayList<Cocktail> Cocktails = new ArrayList<>();
        String[] cocktails = buffer.split("\n");

        for (String cocktail : cocktails) {
            Cocktail recommendedCocktail = parseString(cocktail);
            Cocktails.add(recommendedCocktail);
        }
        return Cocktails;
    }

    public static ArrayList<Cocktail> parseCocktails(String bufferCock) {
        ArrayList<Cocktail> drink = new ArrayList<>();
        for (String c : bufferCock.split("\\n")) {
            drink.add(Cocktail.parseString(c));
        }
        return drink;
    }

    
    @Override
    public String toString() {
        return "Nome: " + getNome() + ",\n Ingredienti: " + getIngredienti() + ",\n Quantità: " + getQuantita() + ",\n Gradazione Alcolica: " + this.gradazione_alcolica+ ",\n Prezzo: " + getPrezzo();
    }

    public void setGradazione_alcolica(float gradazione_alcolica) {
        this.gradazione_alcolica = gradazione_alcolica;
    }
}
