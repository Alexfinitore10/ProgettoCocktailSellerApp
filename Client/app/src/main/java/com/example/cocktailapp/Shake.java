package com.example.cocktailapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Shake extends Bevanda {

    public Shake(String nome, float prezzo, List<String> ingredienti, int quantità) {
        super(nome, prezzo, ingredienti, quantità);
    }



    public static Shake parseString(String input) {
        String[] parts = input.split(", ");

        String nome = parts[0].trim();

        List<String> ingredienti = new ArrayList<>();
        if (!parts[1].trim().equals("N/A")) {
            String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
            String[] ingredientiArray = ingredientiString.split(";");
            ingredienti.addAll(Arrays.asList(ingredientiArray));
        }

        float prezzo = Float.parseFloat(parts[2].trim());
        int quantita = Integer.parseInt(parts[3].trim());

        return new Shake(nome, prezzo,ingredienti, quantita);
    }

    public static List<Shake> setShakes(String buffer) {
        List<Shake> Shakes = new ArrayList<>();
        String[] shakes = buffer.split("\n");

        for (String shake : shakes) {
            Shake recommendedShakes = parseString(shake);
            Shakes.add(recommendedShakes);
        }
        return Shakes;
    }

    @Override
    public String toString() {
        return "Nome: " + getNome() + ",\n Ingredienti: " + getIngredienti() + ",\n Quantità: " + getQuantita() + ",\n Prezzo: " + getPrezzo();
    }


}
