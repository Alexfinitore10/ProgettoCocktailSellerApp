package com.example.CocktailApp.Adapter;

import java.math.BigDecimal;
import java.util.List;

public class CocktailLayoutClass {
    private String nome;
    private List<String> ingredienti;
    private BigDecimal gradazione_alcolica;
    private BigDecimal prezzo;
    private int quantita;

    public CocktailLayoutClass(String nome, List<String> ingredienti, BigDecimal gradazione_alcolica, BigDecimal prezzo, int amount) {
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

    public BigDecimal getGradazione_alcolica() {
        return gradazione_alcolica;
    }

    public void setGradazione_alcolica(BigDecimal gradazione_alcolica) {
        this.gradazione_alcolica = gradazione_alcolica;
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
