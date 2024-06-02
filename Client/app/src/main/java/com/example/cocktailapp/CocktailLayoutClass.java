package com.example.cocktailapp;

import java.util.List;

public class CocktailLayoutClass {
    private String nome;
    private List<String> ingredienti;
    private double gradazione_alcolica;
    private double prezzo;
    private int quantita;

    public CocktailLayoutClass(String nome, List<String> ingredienti, double gradazione_alcolica, double prezzo, int amount) {
        this.nome = nome;
        this.ingredienti = ingredienti;
        this.gradazione_alcolica = gradazione_alcolica;
        this.prezzo = prezzo;
        this.quantita = amount;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<String> getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(List<String> ingredienti) {
        this.ingredienti = ingredienti;
    }

    public double getGradazione_alcolica() {
        return gradazione_alcolica;
    }

    public void setGradazione_alcolica(double gradazione_alcolica) {
        this.gradazione_alcolica = gradazione_alcolica;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public String toString() {
        return "CocktailLayoutClass{" +
                "nome='" + nome + '\'' +
                ", ingredienti=" + ingredienti+
                ", gradazione_alcolica=" + gradazione_alcolica +
                ", prezzo=" + prezzo +
                ", quantità=" + quantita +
                '}';
    }
}
