package com.example.cocktailapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cocktail extends Bevanda {
    private double gradazione_alcolica;

    public Cocktail(String nome, double prezzo, List<String> ingredienti, int quantità, double grado_alcol) {
        super(nome, prezzo, ingredienti, quantità);
        this.gradazione_alcolica = grado_alcol;
    }

    public Cocktail() {

    }

    public double getGradazione_alcolica() {
        return gradazione_alcolica;
    }

    // Metodo per parsare la stringa e creare un oggetto Cocktail
    public static Cocktail parseString(String input) {
        String[] parts = input.split(", ");

        String nome = parts[0].trim();

        // Rimuovi le parentesi quadre dagli ingredienti
        String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
        String[] ingredientiArray = ingredientiString.split(";");

        float gradazioneAlcolica = Float.parseFloat(parts[2].trim());
        float prezzo = Float.parseFloat(parts[3].trim());
        int quantita = Integer.parseInt(parts[4].trim());

        return new Cocktail(nome, prezzo, Arrays.asList(ingredientiArray), quantita,gradazioneAlcolica);
    }

    @Override
    public String toString() {
        return super.toString() + "Gradazione alcolica: " + gradazione_alcolica;
    }

    public void setGradazione_alcolica(double gradazione_alcolica) {
        this.gradazione_alcolica = gradazione_alcolica;
    }
}
