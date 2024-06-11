package com.example.cocktailapp;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Shake extends Bevanda {

    public Shake(String nome, BigDecimal prezzo, List<String> ingredienti, int quantità) {
        super(nome, prezzo, ingredienti, quantità);
    }



    public static Shake parseString(String input) throws IndexOutOfBoundsException{
        String[] parts = input.split(", ");

        if(parts.length < 4) {
            Log.e("Shakes parse String","Stringa che ha causato errore:" +input);
            throw new IndexOutOfBoundsException("Shake string has less than 4 parts");
        }

        String nome = parts[0].trim();

        List<String> ingredienti = new ArrayList<>();
        if (!parts[1].trim().equals("N/A")) {
            String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
            String[] ingredientiArray = ingredientiString.split(";");
            ingredienti.addAll(Arrays.asList(ingredientiArray));
        }

        BigDecimal prezzo = new BigDecimal(parts[2].trim());
        prezzo = prezzo.setScale(2, RoundingMode.HALF_UP);
        int quantita = Integer.parseInt(parts[3].trim());

        return new Shake(nome, prezzo,ingredienti, quantita);
    }

//    public static Shake parseString(String input) {
//        String[] parts = input.split(", ");
//        if(parts.length != 4) {
//            Log.e("Shakes parse String","Stringa che ha causato errore:" +input);
//            throw new IndexOutOfBoundsException("Shake string has number of parts = " +parts.length);
//        }
//        Log.d("Shake Parse String", "Parts size: " + parts.length);
//        Log.d("Shake Parse String", "parse array: " + Arrays.toString(parts));
//        String nome = parts[0].trim();
//
//        Log.d("Shake Parse string", "Nome: " + nome);
//
//        List<String> ingredienti = new ArrayList<>();
//        Log.d("parseString Shakes","parts[1]: "+parts[1]);
//        if (!parts[1].trim().equals("N/A")) {
//            Log.d("Shake Parse string", "Sto nell'if");
//
//            String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
//
//            Log.d("Shake Parse string", "Ingredienti string: " + ingredientiString);
//
//            String[] ingredientiArray = ingredientiString.split(";");
//
//            Log.d("Shake Parse string", "Ingredienti Array: " + Arrays.toString(ingredientiArray));
//
//            ingredienti.addAll(Arrays.asList(ingredientiArray));
//
//            Log.d("Shake Parse string", "Ingredienti List: " + ingredienti);
//
//        }
//
//        BigDecimal prezzo = new BigDecimal(parts[2].trim());
//
//        Log.d("Shake Parse string", "Prezzo: " + prezzo);
//
//        int quantita = Integer.parseInt(parts[3].trim());
//
//        Log.d("Shake Parse string", "Quantita: " + quantita);
//        Shake shake = new Shake(nome, prezzo,ingredienti, quantita);
//
//        return shake;
//        //return new Shake(nome, prezzo,ingredienti, quantita);
//    }

    public static ArrayList<Shake> setRecommendedShakes(String buffer) {
        ArrayList<Shake> Shakes = new ArrayList<>();
        String[] shakes = buffer.split("\n");

        for (String shake : shakes) {
            Shake recommendedShakes = parseString(shake);
            Shakes.add(recommendedShakes);
        }
        return Shakes;
    }

    public static ArrayList<Shake> parseShakes(String bufferShake) {
        ArrayList<Shake> shake = new ArrayList<>();
        for (String c : bufferShake.split("\\n")) {
            shake.add(Shake.parseString(c));
        }
        return shake;
    }



    @Override
    public String toString() {
        return "Nome: " + getNome() + ",\n Ingredienti: " + getIngredienti() + ",\n Quantità: " + getQuantita() + ",\n Prezzo: " + getPrezzo();
    }


}
