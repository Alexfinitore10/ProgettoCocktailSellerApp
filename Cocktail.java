import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cocktail {
    private String nome;
    private List<String> ingredienti;
    private float gradazioneAlcolica;
    private float prezzo;
    private int quantita;

    // Costruttore
    public Cocktail(String nome, List<String> ingredienti, float gradazioneAlcolica, float prezzo, int quantita) {
        this.nome = nome;
        this.ingredienti = ingredienti;
        this.gradazioneAlcolica = gradazioneAlcolica;
        this.prezzo = prezzo;
        this.quantita = quantita;
    }

    // Metodo per parsare la stringa e creare un oggetto Cocktail
    public static Cocktail parseString(String input) {
        String[] parts = input.split(", ");

        String nome = parts[0].trim();

        // Rimuovi le parentesi quadre dagli ingredienti
        String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
        String[] ingredientiArray = ingredientiString.split(";");

        float gradazioneAlcolica = Float.parseFloat(parts[2].trim());
        float prezzo = Float.parseFloat(parts[3].trim());
        int quantita = Integer.parseInt(parts[4].trim());

        return new Cocktail(nome, Arrays.asList(ingredientiArray), gradazioneAlcolica, prezzo, quantita);
    }

    // Metodo toString per rappresentazione testuale dell'oggetto
    @Override
    public String toString() {
        return "Cocktail{" +
                "nome='" + nome + '\'' +
                ", ingredienti=" + ingredienti +
                ", gradazioneAlcolica=" + gradazioneAlcolica +
                ", prezzo=" + prezzo +
                ", quantita=" + quantita +
                '}';
    }

    // Metodo main per testare la classe
    public static void main(String[] args) {
        //String input = "Mojito , [Rum; Lime ; Zucchero ; Menta], 18        , 6 , 10 ";
        String input = "Mojito , [Rum; Lime ; Zucchero ; Menta], 18       , 6 , 10\nNegroni , [Ghiaccio;Gin;Bitter Campari;Vermut Rosso], 28       , 5.9 , 10 ";
        Cocktail mojito = Cocktail.parseString(input);
        System.out.println(mojito);
    }
}
