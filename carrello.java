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

    // Ottieni tutti gli elementi del carrello
    // todo

    // vedi tutti gli elementi del carrello
    public void viewItems() {
        System.out.println(
                "+---------------+---------------------------------------------+-------------------+---------------+");
        System.out.println(
                "|   Cocktails   |                    Ingredienti              | Gradazione Alcolica|    Prezzo     |");
        System.out.println(
                "+---------------+---------------------------------------------+-------------------+---------------+");
        if (cocktails.isEmpty()) {
            System.out.println("| Nessun cocktail nel carrello.                                              |");
        } else {
            for (Cocktail cocktail : cocktails) {
                System.out.printf("| %-13s | %-21s | %-17.1f | %-13.2f |%n", cocktail.getNome(),
                        cocktail.getIngredienti(), cocktail.getGradazioneAlcolica(), cocktail.getPrezzo());
            }
        }
        System.out.println("+---------------+---------------------------------------------+---------------+");
        System.out.println("|     Shakes    |                    Ingredienti              |       Prezzo  |");
        System.out.println("+---------------+---------------------------------------------+---------------+");
        if (shakes.isEmpty()) {
            System.out.println("| Nessuno shake nel carrello.                                             |");
        } else {
            for (Shake shake : shakes) {
                System.out.printf("| %-13s | %-21s | %-17.2f |%n", shake.getNome(), shake.getIngredienti(),
                        shake.getPrezzo());
            }
        }
        System.out.println("+---------------+-----------------------+-------------------+---------------+");
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