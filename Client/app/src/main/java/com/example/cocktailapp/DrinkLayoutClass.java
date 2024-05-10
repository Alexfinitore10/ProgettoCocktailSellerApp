package com.example.cocktailapp;
 // ora c'è tutto giusto? sì mi trovo, ci sono degli errori che non so a cosa sono dovuti ma forse sono cazzate
public class DrinkLayoutClass {

    private String nome,ingredienti,gradazione_alcolica,prezzo,quantità;
    private double gradazione_alcolica;
    private double prezzo;
    

    public DrinkLayoutClass(String nome, String ingredienti, String gradazione_alcolica, String prezzo, String quantità) {
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

    public String getGradazione_alcolica() {
        return gradazione_alcolica;
    }

    public void setGradazione_alcolica(String gradazione_alcolica) {
        this.gradazione_alcolica = gradazione_alcolica;
    }

    public String getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(String prezzo) {
        this.prezzo = prezzo;
    }

    public String getQuantità() {
        return quantità;
    }

    public void setQuantità(String quantità) {
        this.quantità = quantità;
    }
}
