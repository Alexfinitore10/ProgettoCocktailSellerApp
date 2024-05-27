package com.example.cocktailapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

public class Carrello {
    private ArrayList<Bevanda> beverages;
    private static final Carrello istanza = new Carrello();
    private CartObserver cartObserver;

    private Carrello() {
        beverages = new ArrayList<>();
    }
    public static Carrello getInstance() {
        return istanza;
    }
    public void addBeverage(Bevanda bevanda) {
        beverages.add(bevanda);
        if (cartObserver != null) {
            cartObserver.setTotalCartValue(calculateTotal());
        }
    }
    public void removeBeverage(Bevanda bevanda) {
        for(int i = 0; i < beverages.size(); i++) {
            if(beverages.get(i).getNome().equals(bevanda.getNome())) {
                Log.d("Carrello", "Sto per rimuovere " + bevanda.getNome());
                beverages.remove(beverages.get(i));
                Log.d("Carrello", "Bevanda rimossa");
                if (cartObserver != null) {
                    cartObserver.setTotalCartValue(calculateTotal());
                }
                break;
            }
        }

    }

    public void setObserver(CartObserver observer) {
        this.cartObserver = observer;
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
            total += beverages.get(i).getPrezzo()*beverages.get(i).getQuantita();
        }

        return total;
    }

    public void emptyCarrello() {
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

    public boolean isBeverageInCart(Bevanda bevanda) {
        if (beverages.isEmpty()) {
            return false;
        }
        for (int i = 0; i < beverages.size(); i++) {
            if (beverages.get(i).getNome().equals(bevanda.getNome())) {
                return true;
            }
        }

        return false;

    }

    public int getAmountSelectedBeverage(Bevanda bevanda) {
        int amount = 0;
        for (int i = 0; i < beverages.size(); i++) {
            if (beverages.get(i).getNome().equals(bevanda.getNome())) {
                amount = beverages.get(i).getQuantita();
                return amount;
            }
        }
        return amount;
    }

    public void setAmountSelectedBeverage(Bevanda bevanda, int amount) {
        for (int i = 0; i < beverages.size(); i++) {
            if (beverages.get(i).getNome().equals(bevanda.getNome())) {
                beverages.get(i).setQuantita(amount);
            }
        }
    }

    public ArrayList<Bevanda> getBeverages() {
        return beverages;
    }
}
