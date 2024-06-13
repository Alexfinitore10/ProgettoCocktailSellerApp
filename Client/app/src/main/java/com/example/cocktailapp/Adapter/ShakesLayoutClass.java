package com.example.cocktailapp.Adapter;

import java.math.BigDecimal;
import java.util.List;

public class ShakesLayoutClass {
    private String nome;
    private List<String> ingredienti;
    private BigDecimal prezzo;
    private int quantita;

    public ShakesLayoutClass(String nome, List<String> ingredienti, BigDecimal prezzo, int quantita) {
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

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }
}
