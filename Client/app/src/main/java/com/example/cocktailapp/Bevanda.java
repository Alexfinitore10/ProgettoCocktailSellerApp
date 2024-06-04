package com.example.cocktailapp;

import java.util.List;

public class Bevanda {
    private String nome;
    private float prezzo;
    private List<String> ingredienti;
    private int quantita;

    public Bevanda(String nome, float prezzo, List<String> ingredienti, int quantita) {
        this.nome = nome;
        this.prezzo = prezzo;
        this.ingredienti = ingredienti;
        this.quantita = quantita;
    }

    public Bevanda() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public List<String> getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(List<String> ingredienti) {
        this.ingredienti = ingredienti;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }




}



