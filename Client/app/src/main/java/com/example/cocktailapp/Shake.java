package com.example.cocktailapp;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class Shake extends Bevanda {

    public Shake(String nome, double prezzo, List<String> ingredienti, int quantità) {
        super(nome, prezzo, ingredienti, quantità);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public static Shake parseString(String input) {
        String[] parts = input.split(", ");

        String nome = parts[0].trim();

        // Rimuovi le parentesi quadre dagli ingredienti
        String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
        String[] ingredientiArray = ingredientiString.split(";");

        float prezzo = Float.parseFloat(parts[2].trim());
        int quantita = Integer.parseInt(parts[3].trim());

        return new Shake(nome, prezzo,Arrays.asList(ingredientiArray), quantita);
    }


}
