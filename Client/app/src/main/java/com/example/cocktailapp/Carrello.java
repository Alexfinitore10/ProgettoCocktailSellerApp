package com.example.cocktailapp;

import java.util.ArrayList;

public class Carrello {
    private ArrayList<Bevanda> beverages;

    private static final Carrello istanza = new Carrello();
    private boolean cartModified = false;
    private int lastSize = 0;

    public int getLastSize() {
        return lastSize;
    }

    public void setLastSize(int lastSize) {
        this.lastSize = lastSize;
    }

    public boolean isCartModified() {
        return cartModified;
    }

    public void setCartModified(boolean addingToCart) {
        cartModified = addingToCart;
    }


    private Carrello() {
        beverages = new ArrayList<>();
    }

    public static Carrello getInstance() {
        return istanza;
    }

    public void addBeverage(Bevanda bevanda) {
        beverages.add(bevanda);
    }


    public ArrayList<Bevanda> getBeverages() {
        return beverages;
    }

    public void viewItems() {
        int cocktailsNumber = getCocktailsNumber(beverages);
        int shakesNumber = getShakesNumber(beverages);
        System.out.println(
                "+---------------+---------------------------------------------+-------------------+---------------+");
        System.out.println(
                "|   Cocktails   |                    Ingredienti              | Gradazione Alcolica|    Prezzo     |");
        System.out.println(
                "+---------------+---------------------------------------------+-------------------+---------------+");
        if (cocktailsNumber == 0) {
            System.out.println("| Nessun cocktail nel carrello.                                              |");
        } else {
            for (Bevanda bevanda : beverages) {
                if (bevanda instanceof Cocktail) {
                    System.out.printf("| %-13s | %-21s | %-17.1f | %-13.2f |%n", bevanda.getNome(),
                            bevanda.getIngredienti(), ((Cocktail) bevanda).getGradazione_alcolica(), bevanda.getPrezzo());
                }
            }
        }
        System.out.println("+---------------+---------------------------------------------+---------------+");
        System.out.println("|     Shakes    |                    Ingredienti              |       Prezzo  |");
        System.out.println("+---------------+---------------------------------------------+---------------+");
        if (shakesNumber == 0) {
            System.out.println("| Nessuno shake nel carrello.                                             |");
        } else {
            for (Bevanda bevanda : beverages) {
                if (bevanda instanceof Shake) {
                    System.out.printf("| %-13s | %-21s | %-17.2f |%n", bevanda.getNome(), bevanda.getIngredienti(),
                            bevanda.getPrezzo());
                }
            }
        }
        System.out.println("+---------------+-----------------------+-------------------+---------------+");
    }


    // Calcola il totale del carrello
    public double calculateTotal() {
        double total = 0;

        // Calcola il totale dei cocktail
        for (int i = 0; i < beverages.size(); i++) {
            total += beverages.get(i).getPrezzo();
        }

        return total;
    }

    public void emptyCarrello() {
        if (!beverages.isEmpty()) {
            beverages.clear();
        }
        if (!beverages.isEmpty()) {
            beverages.clear();
        }
    }

    public int getCocktailsNumber(ArrayList<Bevanda> beverages){
        int count = 0;
        for (Bevanda beverage : beverages) {
            if (beverage instanceof Cocktail) {
                count++;
            }
        }
        return count;
    }

    public int getShakesNumber(ArrayList<Bevanda> beverages){
        int count = 0;
        for (Bevanda beverage : beverages) {
            if (beverage instanceof Shake) {
                count++;
            }
        }
        return count;
    }


}
