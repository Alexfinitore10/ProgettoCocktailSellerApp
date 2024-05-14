import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Shake {
    private String nome;
    private List<String> ingredienti;
    private float prezzo;
    private int quantita;

    // Costruttore
    public Shake(String nome, List<String> ingredienti, float prezzo, int quantita) {
        this.nome = nome;
        this.ingredienti = ingredienti;
        this.prezzo = prezzo;
        this.quantita = quantita;
    }

    // Getter per il campo "nome"
    public String getNome() {
        return nome;
    }

    // Getter per il campo "ingredienti"
    public List<String> getIngredienti() {
        return ingredienti;
    }

    // Getter per il campo "prezzo"
    public float getPrezzo() {
        return prezzo;
    }

    // Getter per il campo "quantita"
    public int getQuantita() {
        return quantita;
    }

    // Parse della tringa da Server
    public static Shake parseString(String input) {
        String[] parts = input.split(", ");

        String nome = parts[0].trim();

        // Rimuovi le parentesi quadre dagli ingredienti
        String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
        String[] ingredientiArray = ingredientiString.split(";");

        float prezzo = Float.parseFloat(parts[2].trim());
        int quantita = Integer.parseInt(parts[3].trim());

        return new Shake(nome, Arrays.asList(ingredientiArray), prezzo, quantita);
    }

    // Metodo toString per rappresentazione testuale dell'oggetto
    @Override
    public String toString() {
        return "Shake{" +
                "nome='" + nome + '\'' +
                ", ingredienti=" + ingredienti +
                ", prezzo=" + prezzo +
                ", quantita=" + quantita +
                '}';
    }
}
