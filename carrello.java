import java.util.ArrayList;
import java.util.List;

public class carrello {
    private List<Cocktail> cocktails;
    private List<Shake> shakes;

    public carrello() {
        cocktails = new ArrayList<>();
        shakes = new ArrayList<>();
    }

    // Aggiungi un cocktail al carrello
    public void addCocktail(Cocktail cocktail) {
        cocktails.add(cocktail);
    }

    // Aggiungi uno shake al carrello
    public void addShake(Shake shake) {
        shakes.add(shake);
    }

    // Ottieni la lista di cocktail nel carrello
    public List<Cocktail> getCocktails() {
        return cocktails;
    }

    // Ottieni la lista di shake nel carrello
    public List<Shake> getShakes() {
        return shakes;
    }

    // vedi tutti gli elementi del carrello
    public void viewItems() {
        System.out.println("Cocktails nel carrello:");
        if (cocktails.isEmpty()) {
            System.out.println("Non ci sono cocktail nel carrello.");
        } else {
            for (Cocktail cocktail : cocktails) {
                System.out.println(cocktail.getNome());
            }
        }

        System.out.println("Shake nel carrello:");
        if (shakes.isEmpty()) {
            System.out.println("Non ci sono shake nel carrello.");
        } else {
            for (Shake shake : shakes) {
                System.out.println(shake.getNome());
            }
        }
    }

    // Calcola il totale del carrello
    public float calculateTotal() {
        float total = 0;

        // Calcola il totale dei cocktail
        for (Cocktail cocktail : cocktails) {
            total += cocktail.getPrezzo();
        }

        // Calcola il totale degli shake
        for (Shake shake : shakes) {
            total += shake.getPrezzo();
        }

        return total;
    }
}