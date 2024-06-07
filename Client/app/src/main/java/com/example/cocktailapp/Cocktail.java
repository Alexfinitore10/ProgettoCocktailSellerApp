package com.example.cocktailapp;

import android.util.Log;

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


    public static Cocktail parseString(String input) throws IndexOutOfBoundsException{
        String[] parts = input.split(", ");

        if(parts.length < 5) {
            throw new IndexOutOfBoundsException("Cocktail string has less than 5 parts");
        }

        String nome = parts[0].trim();

        String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
        String[] ingredientiArray = ingredientiString.split(";");

        float gradazioneAlcolica = Float.parseFloat(parts[2].trim());
        float prezzo = Float.parseFloat(parts[3].trim());
        int quantita = Integer.parseInt(parts[4].trim());

        return new Cocktail(nome, prezzo, Arrays.asList(ingredientiArray), quantita, gradazioneAlcolica);
    }

//    public static Cocktail parseString(String input) throws IndexOutOfBoundsException {
//        String[] parts = input.split(", ");
//        Log.d("Cocktail Parse String", "Parts size: " + parts.length);
//        Log.d("Cocktail Parse String", "parse array: " + Arrays.toString(parts));
//
//        if(parts.length < 5) {
//            throw new IndexOutOfBoundsException("Cocktail string has less than 5 parts");
//        }
//            String nome = parts[0].trim();
//
//            Log.d("Cocktail Parse string", "Nome: " + nome);
//
//            String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
//            Log.d("Cocktail Parse String", "Ingredienti string: " + ingredientiString);
//            String[] ingredientiArray = ingredientiString.split(";");
//            Log.d("Cocktail Parse String", "Ingredienti array: " + Arrays.toString(ingredientiArray));
//
//            float gradazioneAlcolica = Float.parseFloat(parts[2].trim());
//
//            Log.d("Cocktail Parse String", "Gradazione alcolica: " + gradazioneAlcolica);
//
//            float prezzo = Float.parseFloat(parts[3].trim());
//
//            Log.d("Cocktail Parse String", "Prezzo: " + prezzo);
//
//            int quantita = Integer.parseInt(parts[4].trim());
//
//            Log.d("Cocktail Parse String", "Quantita: " + quantita);
//
//
//            Cocktail cocktail = new Cocktail(nome, prezzo, Arrays.asList(ingredientiArray), quantita, gradazioneAlcolica);
//
//            Log.d("Cocktail Parse String", "Sto per stampare il Cocktail: " + cocktail);
//
//            return cocktail;
//            //return new Cocktail(nome, prezzo, Arrays.asList(ingredientiArray), quantita, gradazioneAlcolica);
//
//    }

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
