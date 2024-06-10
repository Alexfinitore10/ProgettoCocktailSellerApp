package com.example.cocktailapp;

import java.util.List;

public class ShakesLayoutClass {
    private String nome;
    private List<String> ingredienti;
    private float prezzo;
    private int quantita;

    public ShakesLayoutClass(String nome, List<String> ingredienti, float prezzo, int quantita) {
        this.nome = nome;
        this.ingredienti = ingredienti;
        this.prezzo = prezzo;
        this.quantita = quantita;
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

    public float getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }
}
