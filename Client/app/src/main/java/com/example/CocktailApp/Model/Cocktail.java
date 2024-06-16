package com.example.CocktailApp.Model;

import android.os.Parcel;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cocktail extends Bevanda {
    private BigDecimal gradazione_alcolica;
    private static final String TAG = "Cocktail";

    public Cocktail(String nome, BigDecimal prezzo, List<String> ingredienti, int quantita, BigDecimal grado_alcol) {
        super(nome, prezzo, ingredienti, quantita);
        this.gradazione_alcolica = grado_alcol;
    }

    public Cocktail() {

    }

    protected Cocktail(Parcel in) {
        super.setNome(in.readString());
        super.setIngredienti(in.createStringArrayList());
        super.setQuantita(in.readInt());
        super.setPrezzo(new BigDecimal(in.readString()));
        this.gradazione_alcolica = new BigDecimal(in.readString());
    }


    public BigDecimal getGradazione_alcolica() {
        return gradazione_alcolica;
    }


    public static Cocktail parseString(String input) throws IndexOutOfBoundsException{
        String[] parts = input.split(", ");

        if(parts.length < 5) {
            Log.e(TAG,"Stringa che ha causato errore:" +input);
            throw new IndexOutOfBoundsException("Cocktail string has less than 5 parts");
        }

        String nome = parts[0].trim();

        String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
        String[] ingredientiArray = ingredientiString.split(";");

        BigDecimal gradazioneAlcolica = new BigDecimal(parts[2].trim());
        gradazioneAlcolica = gradazioneAlcolica.setScale(2, RoundingMode.HALF_UP);
        BigDecimal prezzo = new BigDecimal(parts[3].trim());
        prezzo = prezzo.setScale(2, RoundingMode.HALF_UP);
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

    public void setGradazione_alcolica(BigDecimal gradazione_alcolica) {
        this.gradazione_alcolica = gradazione_alcolica;
    }
}
