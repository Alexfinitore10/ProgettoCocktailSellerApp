package com.example.cocktailapp;

public class DrinkLayoutClass {
    private String nome,ingredienti;
    private double gradazione_alcolica;
    private double prezzo;
    private int quantità;

    public DrinkLayoutClass(String nome, String ingredienti, double gradazione_alcolica, double prezzo, int quantità) {
        this.nome = nome;
        this.ingredienti = ingredienti;
        this.gradazione_alcolica = gradazione_alcolica;
        this.prezzo = prezzo;
        this.quantità = quantità;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(String ingredienti) {
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

    public int getQuantità() {
        return quantità;
    }

    public void setQuantità(int quantità) {
        this.quantità = quantità;
    }
}
