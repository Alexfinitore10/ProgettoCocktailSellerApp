package com.example.cocktailapp;

import androidx.annotation.NonNull;

import java.util.List;

public abstract class Bevanda {
    private String nome;
    private double prezzo;
    private List<String> ingredienti;
    private int quantità;

    public Bevanda(String nome, double prezzo, List<String> ingredienti, int quantità) {
        this.nome = nome;
        this.prezzo = prezzo;
        this.ingredienti = ingredienti;
        this.quantità = quantità;
    }

    protected Bevanda() {
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public List<String> getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(List<String> ingredienti) {
        this.ingredienti = ingredienti;
    }

    public int getQuantità() {
        return quantità;
    }

    public void setQuantità(int quantità) {
        this.quantità = quantità;
    }
}



